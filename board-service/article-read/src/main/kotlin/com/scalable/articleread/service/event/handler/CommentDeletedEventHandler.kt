package com.scalable.articleread.service.event.handler

import com.common.event.Event
import com.common.event.EventType
import com.common.event.payload.CommentCreatedEventPayload
import com.common.event.payload.CommentDeletedEventPayload
import com.scalable.articleread.repository.ArticleQueryModelRedisRepository
import org.springframework.stereotype.Component

@Component
class CommentDeletedEventHandler(
    private val articleQueryModelRedisRepository: ArticleQueryModelRedisRepository,
) : EventHandler<CommentDeletedEventPayload> {
    override fun handle(event: Event<CommentDeletedEventPayload>) {
        articleQueryModelRedisRepository.read(event.payload.articleId)
            ?.let { queryModel ->
                queryModel.updatedBy(event.payload)
                articleQueryModelRedisRepository.update(queryModel)
            }
    }

    override fun supports(event: Event<CommentDeletedEventPayload>): Boolean {
        return EventType.COMMENT_DELETED == event.eventType
    }
}