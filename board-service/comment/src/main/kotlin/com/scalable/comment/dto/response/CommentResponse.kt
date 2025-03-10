package com.scalable.comment.dto.response

import com.scalable.comment.entity.Comment
import java.time.LocalDateTime

data class CommentResponse(
    val commentId: Long,
    val content: String,
    val parentCommentId: Long,
    val articleId: Long,
    val writerId: Long,
    val deleted: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(comment: Comment): CommentResponse {
            return CommentResponse(
                commentId = comment.commentId,
                content = comment.content,
                parentCommentId = comment.parentCommentId,
                articleId = comment.articleId,
                writerId = comment.writerId,
                deleted = comment.isDeleted,
                createdAt = comment.createdAt,
            )
        }
    }
}

data class CommentPageResponse(
    val comments: List<CommentResponse>,
    val commentCount: Long,
) {
    companion object {
        fun of(
            comments: List<Comment>,
            commentCount: Long,
        ): CommentPageResponse {
            return CommentPageResponse(
                comments = comments.map { comment -> CommentResponse.from(comment) },
                commentCount = commentCount,
            )
        }

        fun from(
            comments: List<Comment>,
        ): CommentPageResponse {
            return CommentPageResponse(
                comments = comments.map { comment -> CommentResponse.from(comment) },
                commentCount = comments.size.toLong(),
            )
        }
    }
}