package com.scalable.articleread.service.event.handler

import com.common.event.Event
import com.common.event.EventType
import com.common.event.payload.ArticleUpdatedEventPayload
import com.scalable.articleread.repository.ArticleQueryModelRedisRepository
import org.springframework.stereotype.Component

@Component
class ArticleUpdatedEventHandler(
    private val articleQueryModelRedisRepository: ArticleQueryModelRedisRepository,
) : EventHandler<ArticleUpdatedEventPayload> {
    override fun handle(event: Event<ArticleUpdatedEventPayload>) {
        articleQueryModelRedisRepository.read(event.payload.articleId)
            ?.let { queryModel ->
                queryModel.updatedBy(event.payload)
                articleQueryModelRedisRepository.update(queryModel)
            }
    }

    override fun supports(event: Event<ArticleUpdatedEventPayload>): Boolean {
        return EventType.ARTICLE_UPDATED == event.eventType
    }
}