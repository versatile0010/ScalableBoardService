package com.scalable.hotarticle.service.eventhandler

import com.common.event.Event
import com.common.event.payload.ArticleViewedEventPayload
import com.scalable.hotarticle.repository.ArticleViewCountRepository
import com.scalable.hotarticle.util.TimeCalculator
import org.springframework.stereotype.Component

@Component
class ArticleViewEventHandler(
    private val articleViewCountRepository: ArticleViewCountRepository,
) : EventHandler<ArticleViewedEventPayload> {
    override fun handle(event: Event<ArticleViewedEventPayload>) {
        val payload = event.payload
        articleViewCountRepository.upsert(
            articleId = payload.articleId,
            viewCount = payload.articleViewCount,
            ttl = TimeCalculator.calculateDurationToMidnight(),
        )
    }

    override fun supports(event: Event<ArticleViewedEventPayload>): Boolean {
        return event.eventType == com.common.event.EventType.ARTICLE_VIEWED
    }

    override fun findArticleId(event: Event<ArticleViewedEventPayload>): Long {
        return event.payload.articleId
    }
}