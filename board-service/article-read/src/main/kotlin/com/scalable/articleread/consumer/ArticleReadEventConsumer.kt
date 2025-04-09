package com.scalable.articleread.consumer

import com.common.event.Event
import com.common.event.topic.Topic
import com.scalable.articleread.service.ArticleReadService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class ArticleReadEventConsumer(
    private val articleReadService: ArticleReadService,
) {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = [Topic.ARTICLE, Topic.COMMENT, Topic.ARTICLE_LIKE])
    fun listen(
        message: String,
        ack: Acknowledgment,
    ) {
        logger.info { "Received message: $message" }
        val event = Event.fromJson(message)
        if (event != null) {
            articleReadService.handleEvent(event)
        }
        ack.acknowledge()
    }
}