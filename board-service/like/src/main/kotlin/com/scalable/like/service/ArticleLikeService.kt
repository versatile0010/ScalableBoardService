package com.scalable.like.service

import com.common.snowflake.Snowflake
import com.scalable.like.dto.ArticleLikeResponse
import com.scalable.like.entity.ArticleLike
import com.scalable.like.repository.ArticleLikeRepository
import com.scalable.like.repository.findByArticleIdAndUserIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleLikeService(
    private val snowflake: Snowflake,
    private val articleLikeRepository: ArticleLikeRepository,
) {
    fun read(articleId: Long, userId: Long): ArticleLikeResponse {
        val article = articleLikeRepository.findByArticleIdAndUserIdOrThrow(
            articleId = articleId,
            userId = userId,
        )
        return ArticleLikeResponse.from(article)
    }

    @Transactional
    fun like(articleId: Long, userId: Long) {
        articleLikeRepository.save(
            ArticleLike.create(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId,
            )
        )
    }

    @Transactional
    fun unlike(articleId: Long, userId: Long) {
        val article = articleLikeRepository.findByArticleIdAndUserIdOrThrow(
            articleId = articleId,
            userId = userId,
        )
        articleLikeRepository.delete(article)
    }
}