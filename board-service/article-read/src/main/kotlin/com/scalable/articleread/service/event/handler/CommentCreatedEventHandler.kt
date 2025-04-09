package com.scalable.articleread.service.event.handler

import com.common.event.Event
import com.common.event.EventType
import com.common.event.payload.CommentCreatedEventPayload
import com.scalable.articleread.repository.ArticleQueryModelRedisRepository
import org.springframework.stereotype.Component

@Component
class CommentCreatedEventHandler(
    private val articleQueryModelRedisRepository: ArticleQueryModelRedisRepository,
) : EventHandler<CommentCreatedEventPayload> {
    override fun handle(event: Event<CommentCreatedEventPayload>) {
        articleQueryModelRedisRepository.read(event.payload.articleId)
            ?.let { queryModel ->
                queryModel.updatedBy(event.payload)
                articleQueryModelRedisRepository.update(queryModel)
            }
    }

    override fun supports(event: Event<CommentCreatedEventPayload>): Boolean {
        return EventType.COMMENT_CREATED == event.eventType
    }
}