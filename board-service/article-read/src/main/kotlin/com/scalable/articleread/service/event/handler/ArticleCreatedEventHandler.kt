package com.scalable.articleread.service.event.handler

import com.common.event.Event
import com.common.event.EventType
import com.common.event.payload.ArticleCreatedEventPayload
import com.scalable.articleread.repository.ArticleQueryModel
import com.scalable.articleread.repository.ArticleQueryModelRedisRepository
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleCreatedEventHandler(
    private val articleQueryModelRedisRepository: ArticleQueryModelRedisRepository,
) : EventHandler<ArticleCreatedEventPayload> {
    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        articleQueryModelRedisRepository.create(
            queryModel = ArticleQueryModel.fromEventPayload(event.payload),
            ttl = Duration.ofDays(1)
        )
    }
    override fun supports(event: Event<ArticleCreatedEventPayload>): Boolean {
        return EventType.ARTICLE_CREATED == event.eventType
    }
}