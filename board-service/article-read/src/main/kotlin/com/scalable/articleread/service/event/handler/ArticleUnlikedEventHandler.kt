package com.scalable.articleread.service.event.handler

import com.common.event.Event
import com.common.event.EventType
import com.common.event.payload.ArticleLikedEventPayload
import com.common.event.payload.ArticleUnlikedEventPayload
import com.scalable.articleread.repository.ArticleQueryModelRedisRepository
import org.springframework.stereotype.Component

@Component
class ArticleUnlikedEventHandler(
    private val articleQueryModelRedisRepository: ArticleQueryModelRedisRepository,
) : EventHandler<ArticleUnlikedEventPayload> {
    override fun handle(event: Event<ArticleUnlikedEventPayload>) {
        articleQueryModelRedisRepository.read(event.payload.articleId)
            ?.let { queryModel ->
                queryModel.updatedBy(event.payload)
                articleQueryModelRedisRepository.update(queryModel)
            }
    }

    override fun supports(event: Event<ArticleUnlikedEventPayload>): Boolean {
        return EventType.ARTICLE_UNLIKED == event.eventType
    }
}