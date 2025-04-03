package com.scalable.hotarticle.consumer

import com.common.event.Event
import com.common.event.topic.Topic
import com.scalable.hotarticle.service.HotArticleService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class HotArticleEventConsumer(
    private val hotArticleService: HotArticleService,
) {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = [Topic.ARTICLE, Topic.ARTICLE_LIKE, Topic.COMMENT, Topic.ARTICLE_VIEW])
    fun listen(message: String, ack: Acknowledgment) {
        logger.info { "Received event: $message" }
        val event = Event.fromJson(message)
        if (event != null) {
            hotArticleService.handleEvent(event)
        }
        ack.acknowledge()
    }
}