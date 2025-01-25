package com.scalable.article.dto.response

import com.scalable.article.entity.Article
import java.time.LocalDateTime

data class ArticleResponse(
    val articleId: Long,
    var title: String,
    var content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
) {
    companion object {
        fun from(article: Article): ArticleResponse {
            return ArticleResponse(
                articleId = article.articleId,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt,
            )
        }
    }
}
