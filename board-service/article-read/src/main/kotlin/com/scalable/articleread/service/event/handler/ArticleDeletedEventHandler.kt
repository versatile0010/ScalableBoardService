package com.scalable.articleread.service.event.handler

import com.common.event.Event
import com.common.event.EventType
import com.common.event.payload.ArticleDeletedEventPayload
import com.scalable.articleread.repository.ArticleQueryModelRedisRepository
import org.springframework.stereotype.Component

@Component
class ArticleDeletedEventHandler(
    private val articleQueryModelRedisRepository: ArticleQueryModelRedisRepository,
) : EventHandler<ArticleDeletedEventPayload> {
    override fun handle(event: Event<ArticleDeletedEventPayload>) {
        articleQueryModelRedisRepository.delete(event.payload.articleId)
    }

    override fun supports(event: Event<ArticleDeletedEventPayload>): Boolean {
        return EventType.ARTICLE_DELETED == event.eventType
    }
}