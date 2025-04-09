package com.scalable.like.service

import com.common.event.EventType
import com.common.event.payload.ArticleLikedEventPayload
import com.common.event.payload.ArticleUnlikedEventPayload
import com.common.outboxrelay.OutboxEventPublisher
import com.common.snowflake.Snowflake
import com.scalable.like.dto.ArticleLikeCountResponse
import com.scalable.like.dto.ArticleLikeResponse
import com.scalable.like.entity.ArticleLike
import com.scalable.like.entity.ArticleLikeCount
import com.scalable.like.repository.ArticleLikeCountRepository
import com.scalable.like.repository.ArticleLikeRepository
import com.scalable.like.repository.findByArticleIdAndUserIdOrThrow
import com.scalable.like.repository.findByIdOrThrow
import com.scalable.like.repository.findLockedByArticleIdOrThrow
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ArticleLikeService(
    private val snowflake: Snowflake,
    private val outboxEventPublisher: OutboxEventPublisher,
    private val articleLikeRepository: ArticleLikeRepository,
    private val articleLikeCountRepository: ArticleLikeCountRepository,
) {
    fun read(articleId: Long, userId: Long): ArticleLikeResponse {
        val article = articleLikeRepository.findByArticleIdAndUserIdOrThrow(
            articleId = articleId,
            userId = userId,
        )
        return ArticleLikeResponse.from(article)
    }

    @Transactional
    fun likePessimisticLock1(articleId: Long, userId: Long) {
        val articleLike = articleLikeRepository.save(
            ArticleLike.create(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId,
            )
        )
        val articleLikeCount = articleLikeCountRepository.increase(articleLike.articleId)
        if (articleLikeCount == 0L) {
            articleLikeCountRepository.save(
                ArticleLikeCount.init(
                    articleId = articleLike.articleId,
                    likeCount = 1,
                )
            )
        }
        outboxEventPublisher.publish(
            type = EventType.ARTICLE_LIKED,
            payload = ArticleLikedEventPayload(
                articleId = articleLike.articleId,
                userId = articleLike.userId,
                articleLikeId = articleLike.articleLikeId,
                articleLikeCount = articleLikeCount,
                createdAt = articleLike.createdAt,
            ),
            shardKey = articleLike.articleId,
        )
    }

    @Transactional
    fun unlikePessimisticLock1(articleId: Long, userId: Long) {
        val article = articleLikeRepository.findByArticleIdAndUserIdOrThrow(
            articleId = articleId,
            userId = userId,
        )
        articleLikeRepository.delete(article)
        articleLikeCountRepository.decrease(article.articleId)
        outboxEventPublisher.publish(
            type = EventType.ARTICLE_UNLIKED,
            payload = ArticleUnlikedEventPayload(
                articleId = article.articleId,
                userId = userId,
                articleLikeId = 1L,
                articleLikeCount = 1,
                createdAt = LocalDateTime.now(),
            ),
            shardKey = article.articleId,
        )
    }

    @Transactional
    fun likePessimisticLock2(articleId: Long, userId: Long) {
        val articleLike = articleLikeRepository.save(
            ArticleLike.create(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId,
            )
        )
        val articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleLike.articleId)
            ?: ArticleLikeCount.init(articleId = articleLike.articleId, likeCount = 0)
        articleLikeCount.increment()
        articleLikeCountRepository.save(articleLikeCount)
        outboxEventPublisher.publish(
            type = EventType.ARTICLE_LIKED,
            payload = ArticleLikedEventPayload(
                articleId = articleLike.articleId,
                userId = articleLike.userId,
                articleLikeId = articleLike.articleLikeId,
                articleLikeCount = articleLikeCount.likeCount,
                createdAt = articleLike.createdAt,
            ),
            shardKey = articleLike.articleId,
        )
    }

    @Transactional
    fun unlikePessimisticLock2(articleId: Long, userId: Long) {
        val article = articleLikeRepository.findByArticleIdAndUserIdOrThrow(
            articleId = articleId,
            userId = userId,
        )
        articleLikeRepository.delete(article)
        val articleLikeCount = articleLikeCountRepository.findLockedByArticleIdOrThrow(article.articleId)
        articleLikeCount.decrement()
        outboxEventPublisher.publish(
            type = EventType.ARTICLE_UNLIKED,
            payload = ArticleUnlikedEventPayload(
                articleId = article.articleId,
                userId = userId,
                articleLikeId = 1L,
                articleLikeCount = 1,
                createdAt = LocalDateTime.now(),
            ),
            shardKey = article.articleId,
        )
    }

    @Transactional
    fun likeOptimisticLock(articleId: Long, userId: Long) {
        val articleLike = articleLikeRepository.save(
            ArticleLike.create(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId,
            )
        )
        val articleLikeCount = articleLikeCountRepository.findByIdOrNull(articleId)
            ?: ArticleLikeCount.init(articleId = articleLike.articleId, likeCount = 0)

        articleLikeCount.increment()
        articleLikeCountRepository.save(articleLikeCount)
        outboxEventPublisher.publish(
            type = EventType.ARTICLE_LIKED,
            payload = ArticleLikedEventPayload(
                articleId = articleLike.articleId,
                userId = articleLike.userId,
                articleLikeId = articleLike.articleLikeId,
                articleLikeCount = articleLikeCount.likeCount,
                createdAt = articleLike.createdAt,
            ),
            shardKey = articleLike.articleId,
        )
    }

    @Transactional
    fun unlikeOptimisticLock(articleId: Long, userId: Long) {
        val article = articleLikeRepository.findByArticleIdAndUserIdOrThrow(
            articleId = articleId,
            userId = userId,
        )
        articleLikeRepository.delete(article)
        val articleLikeCount = articleLikeCountRepository.findByIdOrThrow(article.articleId)
        articleLikeCount.decrement()
        articleLikeCountRepository.save(articleLikeCount)
        outboxEventPublisher.publish(
            type = EventType.ARTICLE_UNLIKED,
            payload = ArticleUnlikedEventPayload(
                articleId = article.articleId,
                userId = userId,
                articleLikeId = 1L,
                articleLikeCount = 1,
                createdAt = LocalDateTime.now(),
            ),
            shardKey = article.articleId,
        )
    }

    fun count(articleId: Long): ArticleLikeCountResponse {
        return ArticleLikeCountResponse.from(
            articleLikeCount = articleLikeCountRepository.findByIdOrNull(articleId)
        )
    }
}