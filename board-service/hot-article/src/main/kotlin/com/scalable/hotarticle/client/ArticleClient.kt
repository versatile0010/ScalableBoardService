package com.scalable.hotarticle.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

@Component
class ArticleClient(
    @Value("\${endpoints.article-service.url}")
    private val articleServiceUrl: String
) {
    private val logger = KotlinLogging.logger {}
    private val restClient: RestClient = RestClient.create(articleServiceUrl)

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
        val createdAt: LocalDateTime,
    )
}