package com.scalable.hotarticle.service.eventhandler

import com.common.event.Event
import com.common.event.payload.CommentDeletedEventPayload
import com.scalable.hotarticle.repository.ArticleCommentCountRepository
import com.scalable.hotarticle.util.TimeCalculator
import org.springframework.stereotype.Component

@Component
class CommentDeletedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository,
) : EventHandler<CommentDeletedEventPayload> {
    override fun handle(event: Event<CommentDeletedEventPayload>) {
        val payload = event.payload
        articleCommentCountRepository.upsert(
            articleId = payload.articleId,
            commentCount = payload.articleCommentCount.toLong(),
            ttl = TimeCalculator.calculateDurationToMidnight(),
        )
    }

    override fun supports(event: Event<CommentDeletedEventPayload>): Boolean {
        return event.eventType == com.common.event.EventType.COMMENT_DELETED
    }

    override fun findArticleId(event: Event<CommentDeletedEventPayload>): Long {
        return event.payload.articleId
    }
}