package com.scalable.comment.dto.response

import com.scalable.comment.entity.ArticleCommentCount

data class CommentCountResponse(
    val commentCount: Long,
) {
    companion object {
        fun from(articleCommentCount: ArticleCommentCount): CommentCountResponse {
            return CommentCountResponse(
                commentCount = articleCommentCount.commentCount,
            )
        }
    }
}
