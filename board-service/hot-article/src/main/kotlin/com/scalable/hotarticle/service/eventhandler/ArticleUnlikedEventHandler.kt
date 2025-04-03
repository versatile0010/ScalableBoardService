package com.scalable.hotarticle.service.eventhandler

import com.common.event.Event
import com.common.event.payload.ArticleUnlikedEventPayload
import com.scalable.hotarticle.repository.ArticleLikeCountRepository
import com.scalable.hotarticle.util.TimeCalculator
import org.springframework.stereotype.Component

@Component
class ArticleUnlikedEventHandler(
    private val articleLikeCountRepository: ArticleLikeCountRepository,
) : EventHandler<ArticleUnlikedEventPayload> {
    override fun handle(event: Event<ArticleUnlikedEventPayload>) {
        val payload = event.payload
        articleLikeCountRepository.upsert(
            articleId = payload.articleId,
            likeCount = payload.articleLikeCount.toLong(),
            ttl = TimeCalculator.calculateDurationToMidnight(),
        )
    }

    override fun supports(event: Event<ArticleUnlikedEventPayload>): Boolean {
        return event.eventType == com.common.event.EventType.ARTICLE_UNLIKED
    }

    override fun findArticleId(event: Event<ArticleUnlikedEventPayload>): Long {
        return event.payload.articleId
    }
}