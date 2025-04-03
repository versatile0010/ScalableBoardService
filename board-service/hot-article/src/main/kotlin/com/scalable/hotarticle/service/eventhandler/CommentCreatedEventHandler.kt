package com.scalable.hotarticle.service.eventhandler

import com.common.event.Event
import com.common.event.payload.CommentCreatedEventPayload
import com.scalable.hotarticle.repository.ArticleCommentCountRepository
import com.scalable.hotarticle.util.TimeCalculator
import org.springframework.stereotype.Component

@Component
class CommentCreatedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository,
) : EventHandler<CommentCreatedEventPayload> {
    override fun handle(event: Event<CommentCreatedEventPayload>) {
        val payload = event.payload
        articleCommentCountRepository.upsert(
            articleId = payload.articleId,
            commentCount = payload.articleCommentCount.toLong(),
            ttl = TimeCalculator.calculateDurationToMidnight(),
        )
    }

    override fun supports(event: Event<CommentCreatedEventPayload>): Boolean {
        return event.eventType == com.common.event.EventType.COMMENT_CREATED
    }

    override fun findArticleId(event: Event<CommentCreatedEventPayload>): Long {
        return event.payload.articleId
    }
}