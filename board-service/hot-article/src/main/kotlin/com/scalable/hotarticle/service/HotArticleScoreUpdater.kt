package com.scalable.hotarticle.service

import com.common.event.Event
import com.common.event.payload.EventPayload
import com.scalable.hotarticle.repository.ArticleCreatedTimeRepository
import com.scalable.hotarticle.repository.HotArticleRepository
import com.scalable.hotarticle.service.eventhandler.EventHandler
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class HotArticleScoreUpdater(
    private val hotArticleRepository: HotArticleRepository,
    private val hotArticleScoreCalculator: HotArticleScoreCalculator,
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository,
) {
    fun update(event: Event<EventPayload>, eventHandler: EventHandler<EventPayload>) {
        val articleId = eventHandler.findArticleId(event)
        val createdTime = articleCreatedTimeRepository.read(articleId)
        if (!isArticleCreatedToday(createdTime)) {
            return
        }

        eventHandler.handle(event)

        val score = hotArticleScoreCalculator.calculate(articleId)
        hotArticleRepository.addByLua(
            articleId = articleId,
            score = score,
            time = createdTime ?: LocalDateTime.now(),
            limit = HOT_ARTICLE_COUNT,
            ttl = HOT_ARTICLE_TTL,
        )
    }

    private fun isArticleCreatedToday(createdTime: LocalDateTime?): Boolean {
        return createdTime != null && createdTime.toLocalDate() == LocalDate.now()
    }

    companion object {
        private const val HOT_ARTICLE_COUNT = 10L
        private val HOT_ARTICLE_TTL = Duration.ofDays(10)
    }
}