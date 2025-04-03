package com.scalable.hotarticle.service

import com.common.event.Event
import com.common.event.EventType
import com.common.event.payload.EventPayload
import com.scalable.hotarticle.client.ArticleClient
import com.scalable.hotarticle.repository.HotArticleRepository
import com.scalable.hotarticle.service.eventhandler.EventHandler
import com.scalable.hotarticle.service.response.HotArticleResponse
import org.springframework.stereotype.Service

@Service
class HotArticleService(
    private val articleClient: ArticleClient,
    private val eventHandlers: List<EventHandler<EventPayload>>,
    private val hotArticleScoreUpdater: HotArticleScoreUpdater,
    private val hotArticleRepository: HotArticleRepository,
) {
    fun handleEvent(event: Event<EventPayload>) {
        val eventHandler = findEventHandler(event) ?: return
        if (isArticleCreatedOrDeleted(event)) {
            eventHandler.handle(event)
        } else {
            hotArticleScoreUpdater.update(event, eventHandler)
        }
    }

    fun readAll(dateString: String): List<HotArticleResponse> {
        return hotArticleRepository.readAll(dateString)
            .mapNotNull { it ->
                val response = articleClient.read(it.toLong()) ?: return@mapNotNull null
                return@mapNotNull HotArticleResponse.from(response)
            }
    }

    private fun isArticleCreatedOrDeleted(event: Event<EventPayload>): Boolean {
        return EventType.ARTICLE_CREATED == event.eventType || EventType.ARTICLE_DELETED == event.eventType
    }

    private fun findEventHandler(event: Event<EventPayload>): EventHandler<EventPayload>? {
        return eventHandlers.firstOrNull { it.supports(event) }
    }
}

