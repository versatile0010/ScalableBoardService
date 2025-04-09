package com.scalable.articleread.service.response

import com.scalable.articleread.repository.ArticleQueryModel

data class ArticleReadResponse(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: String,
    val modifiedAt: String,
    val commentCount: Long,
    val likeCount: Long,
    val viewCount: Long,
) {
    companion object {
        fun of(queryModel: ArticleQueryModel, viewCount: Long): ArticleReadResponse {
            return ArticleReadResponse(
                articleId = queryModel.articleId,
                title = queryModel.title,
                content = queryModel.content,
                boardId = queryModel.boardId,
                writerId = queryModel.writerId,
                createdAt = queryModel.createdAt,
                modifiedAt = queryModel.modifiedAt,
                commentCount = queryModel.commentCount,
                likeCount = queryModel.likeCount,
                viewCount = viewCount,
            )
        }
    }
}
