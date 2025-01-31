package com.scalable.comment.service

import com.common.snowflake.Snowflake
import com.scalable.comment.dto.request.CommentCreateRequest
import com.scalable.comment.dto.response.CommentPageResponse
import com.scalable.comment.dto.response.CommentResponse
import com.scalable.comment.entity.Comment
import com.scalable.comment.global.PageHelper
import com.scalable.comment.repository.CommentRepository
import com.scalable.comment.repository.findActivatedByIdOrThrow
import com.scalable.comment.repository.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val snowflake: Snowflake,
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

    private fun deleteCascade(comment: Comment) {
        commentRepository.delete(comment)
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