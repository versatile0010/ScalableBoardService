package com.scalable.articleread.service

import com.common.event.Event
import com.common.event.payload.EventPayload
import com.scalable.articleread.client.ArticleClient
import com.scalable.articleread.client.CommentClient
import com.scalable.articleread.client.LikeClient
import com.scalable.articleread.client.ViewClient
import com.scalable.articleread.repository.ArticleQueryModel
import com.scalable.articleread.repository.ArticleQueryModelRedisRepository
import com.scalable.articleread.service.event.handler.EventHandler
import com.scalable.articleread.service.response.ArticleReadResponse
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ArticleReadService(
    private val articleReadRedisRepository: ArticleQueryModelRedisRepository,
    private val articleClient: ArticleClient,
    private val commentClient: CommentClient,
    private val likeClient: LikeClient,
    private val viewClient: ViewClient,
    private val eventHandlers: List<EventHandler<EventPayload>>,
) {
    fun handleEvent(event: Event<EventPayload>) {
        for (handler in eventHandlers) {
            if (handler.supports(event)) {
                handler.handle(event)
                return
            }
        }
    }

    fun read(articleId: Long): ArticleReadResponse {
        val articleQueryModel = (articleReadRedisRepository.read(articleId)
            ?: fetch(articleId)
            ?: throw IllegalStateException("Article not found"))
        return ArticleReadResponse.of(
            queryModel = articleQueryModel,
            viewCount = viewClient.count(articleId) ?: 0L,
        )
    }

    private fun fetch(articleId: Long): ArticleQueryModel? {
        val articleResponse = articleClient.read(articleId) ?: return null

        val queryModel = ArticleQueryModel(
            articleId = articleResponse.articleId,
            title = articleResponse.title,
            content = articleResponse.content,
            boardId = articleResponse.boardId,
            writerId = articleResponse.writerId,
            createdAt = articleResponse.createdAt.toString(),
            modifiedAt = articleResponse.createdAt.toString(),
            commentCount = commentClient.count(articleId) ?: 0L,
            likeCount = likeClient.count(articleId) ?: 0L,
        )
        articleReadRedisRepository.create(queryModel, Duration.ofDays(1))

        return queryModel
    }
}