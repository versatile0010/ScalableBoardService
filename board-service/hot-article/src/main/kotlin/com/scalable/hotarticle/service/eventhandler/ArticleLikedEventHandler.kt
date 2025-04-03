package com.scalable.hotarticle.service.eventhandler

import com.common.event.Event
import com.common.event.payload.ArticleLikedEventPayload
import com.scalable.hotarticle.repository.ArticleLikeCountRepository
import com.scalable.hotarticle.util.TimeCalculator
import org.springframework.stereotype.Component

@Component
class ArticleLikedEventHandler(
    private val articleLikeCountRepository: ArticleLikeCountRepository,
) : EventHandler<ArticleLikedEventPayload> {
    override fun handle(event: Event<ArticleLikedEventPayload>) {
        val payload = event.payload
        articleLikeCountRepository.upsert(
            articleId = payload.articleId,
            likeCount = payload.articleLikeCount.toLong(),
            ttl = TimeCalculator.calculateDurationToMidnight(),
        )
    }

    override fun supports(event: Event<ArticleLikedEventPayload>): Boolean {
        return event.eventType == com.common.event.EventType.ARTICLE_LIKED
    }

    override fun findArticleId(event: Event<ArticleLikedEventPayload>): Long {
        return event.payload.articleId
    }
}