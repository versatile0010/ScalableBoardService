package com.scalable.hotarticle.service.response

import com.scalable.hotarticle.client.ArticleClient
import java.time.LocalDateTime

data class HotArticleResponse(
    val articleId: Long,
    val title: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(response: ArticleClient.ArticleResponse): HotArticleResponse {
            return HotArticleResponse(
                articleId = response.articleId,
                title = response.title,
                createdAt = response.createdAt,
            )
        }
    }
}
