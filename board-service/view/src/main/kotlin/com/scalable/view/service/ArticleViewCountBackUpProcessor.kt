package com.scalable.view.service

import com.common.event.EventType
import com.common.event.payload.ArticleViewedEventPayload
import com.common.outboxrelay.OutboxEventPublisher
import com.scalable.view.repository.ArticleViewCountBackUpRepository
import com.scalable.view.repository.updateViewCountIfNotExistsThenInit
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ArticleViewCountBackUpProcessor(
    private val outboxEventPublisher: OutboxEventPublisher,
    private val backUpRepository: ArticleViewCountBackUpRepository
) {
    @Transactional
    fun backUp(
        articleId: Long,
        viewCount: Long
    ) {
        val articleViewCount = backUpRepository.updateViewCountIfNotExistsThenInit(
            articleId = articleId,
            viewCount = viewCount,
        )
        outboxEventPublisher.publish(
            type = EventType.ARTICLE_VIEWED,
            payload = ArticleViewedEventPayload(
                articleId = articleId,
                articleViewCount = articleViewCount.viewCount,
            ),
            shardKey = articleId,
        )
    }
}