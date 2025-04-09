package com.scalable.articleread.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class ViewClient(
    @Value("\${endpoints.view-service.url}")
    private val viewEndpoint: String,
) {
    private val restClient: RestClient = RestClient.create(viewEndpoint)
    private val logger = KotlinLogging.logger {}

    @Cacheable(key = "#articleId", value = ["articleViewCount"])
    fun count(articleId: Long): Long? {
        return try {
            restClient.get()
                .uri("/v1/article-views/articles/{articleId}", articleId)
                .retrieve()
                .body(Long::class.java)
        } catch (ex: Error) {
            logger.error(ex) { "Error calling article-views service for articeId=$articleId" }
            null
        }
    }
}