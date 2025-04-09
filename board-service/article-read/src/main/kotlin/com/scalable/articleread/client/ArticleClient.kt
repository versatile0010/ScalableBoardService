package com.scalable.articleread.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

@Component
class ArticleClient(
    @Value("\${endpoints.article-service.url}")
    private val articleEndpoint: String,
) {
    private val restClient: RestClient = RestClient.create(articleEndpoint)
    private val logger = KotlinLogging.logger {}

    fun read(articleId: Long): ArticleResponse? {
        return try {
            restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse::class.java)
        } catch (ex: Error) {
            logger.error(ex) { "Error calling article service for articleId=$articleId" }
            null
        }
    }

    data class ArticleResponse(
        val articleId: Long,
        val title: String,
        val content: String,
        val boardId: Long,
        val writerId: Long,
        val createdAt: LocalDateTime,
        val modifiedAt: LocalDateTime,
    )
}