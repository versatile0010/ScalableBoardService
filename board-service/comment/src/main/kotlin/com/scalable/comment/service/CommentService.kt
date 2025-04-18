package com.scalable.comment.service

import com.common.event.EventType
import com.common.event.payload.CommentCreatedEventPayload
import com.common.event.payload.CommentDeletedEventPayload
import com.common.outboxrelay.OutboxEventPublisher
import com.common.snowflake.Snowflake
import com.scalable.comment.dto.request.CommentCreateRequest
import com.scalable.comment.dto.response.CommentCountResponse
import com.scalable.comment.dto.response.CommentPageResponse
import com.scalable.comment.dto.response.CommentResponse
import com.scalable.comment.entity.Comment
import com.scalable.comment.global.PageHelper
import com.scalable.comment.repository.ArticleCommentCountRepository
import com.scalable.comment.repository.CommentRepository
import com.scalable.comment.repository.decreaseIfNotExistsThenInit
import com.scalable.comment.repository.findActivatedByIdOrThrow
import com.scalable.comment.repository.findByIdOrThrow
import com.scalable.comment.repository.increaseIfNotExistsThenInit
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val snowflake: Snowflake,
    private val outboxEventPublisher: OutboxEventPublisher,
    private val commentRepository: CommentRepository,
    private val articleCommentCountRepository: ArticleCommentCountRepository,
) {

    @Transactional
    fun create(request: CommentCreateRequest): CommentResponse {
        val parent = findParent(request)
        val savedComment = commentRepository.save(
            Comment.of(
                commentId = snowflake.nextId(),
                content = request.content,
                parentCommentId = parent?.commentId,
                articleId = request.articleId,
                writerId = request.writerId,
            )
        )
        articleCommentCountRepository.increaseIfNotExistsThenInit(request.articleId)
        outboxEventPublisher.publish(
            type = EventType.COMMENT_CREATED,
            payload = CommentCreatedEventPayload(
                commentId = savedComment.commentId,
                content = savedComment.content,
                articleId = savedComment.articleId,
                writerId = savedComment.writerId,
                createdAt = savedComment.createdAt,
                articleCommentCount = count(savedComment.articleId).commentCount,
                path = null,
                deleted = false,
            ),
            shardKey = savedComment.articleId,
        )
        return CommentResponse.from(savedComment)
    }

    fun read(commentId: Long): CommentResponse {
        return CommentResponse.from(
            comment = commentRepository.findActivatedByIdOrThrow(commentId)
        )
    }

    @Transactional
    fun delete(commentId: Long) {
        val comment = commentRepository.findActivatedByIdOrThrow(commentId)
        if (comment.hasChildren()) {
            comment.softDeleted()
        } else {
            deleteCascade(comment)
        }
        outboxEventPublisher.publish(
            type = EventType.COMMENT_DELETED,
            payload = CommentDeletedEventPayload(
                commentId = comment.commentId,
                content = comment.content,
                articleId = comment.articleId,
                writerId = comment.writerId,
                createdAt = comment.createdAt,
                articleCommentCount = count(comment.articleId).commentCount,
                path = null,
                deleted = true,
            ),
            shardKey = comment.articleId,
        )
    }

    fun readAll(
        articleId: Long,
        page: Long,
        pageSize: Long,
    ): CommentPageResponse {
        val comments = commentRepository.findAllByArticleId(
            articleId = articleId,
            offset = PageHelper.getOffset(
                page = page,
                pageSize = pageSize,
            ),
            limit = pageSize,
        )
        val commentCount = PageHelper.getSearchablePageCount(
            page = page,
            pageSize = pageSize,
            searchablePageCount = 10L
        )
        return CommentPageResponse.of(
            comments = comments,
            commentCount = commentCount,
        )
    }

    fun readAll(
        articleId: Long,
        lastCommentId: Long?,
        lastParentCommentId: Long?,
        limit: Long,
    ): CommentPageResponse {
        val comments = if (lastCommentId != null && lastParentCommentId != null) {
            commentRepository.findAllByArticeIdAndLastCommentIdAndLastParentCommentId(
                articleId = articleId,
                lastCommentId = lastCommentId,
                lastParentCommentId = lastParentCommentId,
                limit = limit,
            )
        } else {
            commentRepository.findAllByArticleId(
                articleId = articleId,
                offset = 0L,
                limit = limit,
            )
        }
        return CommentPageResponse.from(comments)
    }

    fun count(articleId: Long): CommentCountResponse {
        val commentCount = articleCommentCountRepository.findByIdOrNull(articleId)
        return if (commentCount == null) {
            CommentCountResponse(0L)
        } else {
            CommentCountResponse(commentCount.commentCount)
        }
    }

    private fun deleteCascade(comment: Comment) {
        commentRepository.delete(comment)
        articleCommentCountRepository.decreaseIfNotExistsThenInit(comment.articleId)
        if (!comment.isRoot()) {
            val parent = commentRepository.findByIdOrThrow(comment.parentCommentId)
            // 이미 softDelete 된 부모이고, 더 이상 자식이 없으면
            if (parent.isDeleted && !parent.hasChildren()) {
                // 부모도 재귀적으로 삭제
                deleteCascade(parent)
            }
        }
    }

    private fun Comment.hasChildren(): Boolean {
        return commentRepository.countByArticleIdAndParentCommentId(
            articleId = articleId,
            parentCommentId = commentId,
            limit = 2L,
        ) == 2L
    }


    private fun findParent(request: CommentCreateRequest): Comment? {
        return request.parentCommentId?.let { commentId ->
            commentRepository.findActivatedByIdOrThrow(commentId)
        }
    }
}