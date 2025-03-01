package com.scalable.like.repository

import com.scalable.like.entity.ArticleLikeCount
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ArticleLikeCountRepository : JpaRepository<ArticleLikeCount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findLockedByArticleId(articleId: Long): ArticleLikeCount?

    @Query(
        value = """
            update article_like_count set like_count = like_count + 1 where article_id = :articleId
        """,
        nativeQuery = true,
    )
    @Modifying
    fun increase(@Param("articleId") articleId: Long): Int

    @Query(
        value = """
            update article_like_count set like_count = like_count - 1 where article_id = :articleId and like_count > 0
        """,
        nativeQuery = true,
    )
    @Modifying
    fun decrease(@Param("articleId") articleId: Long): Int
}

fun ArticleLikeCountRepository.findLockedByArticleIdOrThrow(articleId: Long): ArticleLikeCount {
    return findLockedByArticleId(articleId) ?: throw IllegalArgumentException("Article like count not found. articleId=$articleId")
}

fun ArticleLikeCountRepository.findByIdOrThrow(articleId: Long): ArticleLikeCount {
    return findById(articleId).orElseThrow { IllegalArgumentException("Article like count not found. articleId=$articleId") }
}