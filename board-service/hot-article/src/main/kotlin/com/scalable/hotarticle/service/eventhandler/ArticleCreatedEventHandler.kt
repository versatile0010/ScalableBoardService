package com.scalable.hotarticle.service.eventhandler

import com.common.event.Event
import com.common.event.EventType
import com.common.event.payload.ArticleCreatedEventPayload
import com.scalable.hotarticle.repository.ArticleCreatedTimeRepository
import com.scalable.hotarticle.util.TimeCalculator
import org.springframework.stereotype.Component

@Component
class ArticleCreatedEventHandler(
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository,
) : EventHandler<ArticleCreatedEventPayload> {
    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload = event.payload
        articleCreatedTimeRepository.upsert(
            articleId = payload.articleId,
            createdTime = payload.createdAt,
            ttl = TimeCalculator.calculateDurationToMidnight(),
        )
    }

    override fun supports(event: Event<ArticleCreatedEventPayload>): Boolean {
        return event.eventType == EventType.ARTICLE_CREATED
    }

    override fun findArticleId(event: Event<ArticleCreatedEventPayload>): Long {
        return event.payload.articleId
    }

}