package com.scalable.like.repository

import com.scalable.like.entity.ArticleLike
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleLikeRepository : JpaRepository<ArticleLike, Long> {
    fun findByArticleIdAndUserId(
        articleId: Long,
        userId: Long,
    ): ArticleLike?
}

fun ArticleLikeRepository.findByArticleIdAndUserIdOrThrow(
    articleId: Long,
    userId: Long,
): ArticleLike {
    return findByArticleIdAndUserId(
        articleId = articleId,
        userId = userId
    ) ?: throw IllegalArgumentException("Article like not found. articleId=$articleId, userId=$userId")
}
