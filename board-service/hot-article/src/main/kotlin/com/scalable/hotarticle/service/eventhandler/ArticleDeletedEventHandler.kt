package com.scalable.hotarticle.service.eventhandler

import com.common.event.Event
import com.common.event.payload.ArticleDeletedEventPayload
import com.scalable.hotarticle.repository.ArticleCreatedTimeRepository
import com.scalable.hotarticle.repository.HotArticleRepository
import org.springframework.stereotype.Component

@Component
class ArticleDeletedEventHandler(
    private val hotArticleRepository: HotArticleRepository,
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository,
) : EventHandler<ArticleDeletedEventPayload> {
    override fun handle(event: Event<ArticleDeletedEventPayload>) {
        val payload = event.payload
        articleCreatedTimeRepository.delete(payload.articleId)
        hotArticleRepository.remove(payload.articleId, payload.createdAt)
    }

    override fun supports(event: Event<ArticleDeletedEventPayload>): Boolean {
        return event.eventType == com.common.event.EventType.ARTICLE_DELETED
    }

    override fun findArticleId(event: Event<ArticleDeletedEventPayload>): Long {
        return event.payload.articleId
    }
}