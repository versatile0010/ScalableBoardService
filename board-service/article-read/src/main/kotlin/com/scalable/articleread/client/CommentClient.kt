package com.scalable.articleread.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class CommentClient(
    @Value("\${endpoints.comment-service.url}")
    private val commentEndpoint: String,
) {
    private val restClient: RestClient = RestClient.create(commentEndpoint)
    private val logger = KotlinLogging.logger {}

    fun count(articleId: Long): Long? {
        return try {
            restClient.get()
                .uri("/v1/comments/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long::class.java)
        } catch (ex: Error) {
            logger.error(ex) { "Error calling comment service for articleId=$articleId" }
            null
        }
    }
}