package com.scalable.like.service

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

@Service
class ArticleLikeService(
    private val snowflake: Snowflake,
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
        val article = articleLikeRepository.save(
            ArticleLike.create(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId,
            )
        )
        val articleLikeCount = articleLikeCountRepository.increase(article.articleId)
        if (articleLikeCount == 0) {
            articleLikeCountRepository.save(
                ArticleLikeCount.init(
                    articleId = article.articleId,
                    likeCount = 1,
                )
            )
        }
    }

    @Transactional
    fun unlikePessimisticLock1(articleId: Long, userId: Long) {
        val article = articleLikeRepository.findByArticleIdAndUserIdOrThrow(
            articleId = articleId,
            userId = userId,
        )
        articleLikeRepository.delete(article)
        articleLikeCountRepository.decrease(article.articleId)
    }

    @Transactional
    fun likePessimisticLock2(articleId: Long, userId: Long) {
        val article = articleLikeRepository.save(
            ArticleLike.create(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId,
            )
        )
        val articleLikeCount = articleLikeCountRepository.findLockedByArticleId(article.articleId)
            ?: ArticleLikeCount.init(articleId = article.articleId, likeCount = 0)
        articleLikeCount.increment()
        articleLikeCountRepository.save(articleLikeCount)
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
    }

    @Transactional
    fun likeOptimisticLock(articleId: Long, userId: Long) {
        val article = articleLikeRepository.save(
            ArticleLike.create(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId,
            )
        )
        val articleLikeCount = articleLikeCountRepository.findByIdOrNull(articleId)
            ?: ArticleLikeCount.init(articleId = article.articleId, likeCount = 0)

        articleLikeCount.increment()
        articleLikeCountRepository.save(articleLikeCount)
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
    }

    fun count(articleId: Long): ArticleLikeCountResponse {
        return ArticleLikeCountResponse.from(
            articleLikeCount = articleLikeCountRepository.findByIdOrNull(articleId)
        )
    }
}