package com.scalable.articleread.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class LikeClient(
    @Value("\${endpoints.like-service.url}")
    private val likeEndpoint: String,
) {
    private val restClient: RestClient = RestClient.create(likeEndpoint)
    private val logger = KotlinLogging.logger {}

    fun count(articleId: Long): Long? {
        return try {
            restClient.get()
                .uri("/v1/article-likes/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long::class.java)
        } catch (ex: Error) {
            logger.error(ex) { "Error calling article-like service for articeId=$articleId" }
            null
        }
    }
}