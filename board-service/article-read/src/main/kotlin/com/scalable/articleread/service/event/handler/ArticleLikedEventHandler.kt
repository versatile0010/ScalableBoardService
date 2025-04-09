package com.scalable.articleread.service.event.handler

import com.common.event.Event
import com.common.event.EventType
import com.common.event.payload.ArticleLikedEventPayload
import com.common.event.payload.CommentDeletedEventPayload
import com.scalable.articleread.repository.ArticleQueryModelRedisRepository
import org.springframework.stereotype.Component

@Component
class ArticleLikedEventHandler(
    private val articleQueryModelRedisRepository: ArticleQueryModelRedisRepository,
) : EventHandler<ArticleLikedEventPayload> {
    override fun handle(event: Event<ArticleLikedEventPayload>) {
        articleQueryModelRedisRepository.read(event.payload.articleId)
            ?.let { queryModel ->
                queryModel.updatedBy(event.payload)
                articleQueryModelRedisRepository.update(queryModel)
            }
    }

    override fun supports(event: Event<ArticleLikedEventPayload>): Boolean {
        return EventType.ARTICLE_LIKED == event.eventType
    }
}