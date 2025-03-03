package com.scalable.comment.repository

import com.scalable.comment.entity.ArticleCommentCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.repository.query.Param

interface ArticleCommentCountRepository : JpaRepository<ArticleCommentCount, Long> {
    @Query(
        value = """
            update article_comment_count set comment_count = comment_count + 1 where article_id = :articleId
        """,
        nativeQuery = true,
    )
    @Modifying
    fun increase(@Param("articleId") articleId: Long): Int

    @Query(
        value = """
            update article_comment_count set comment_count = comment_count - 1 where article_id = :articleId and comment_count > 0
        """,
        nativeQuery = true,
    )
    @Modifying
    fun decrease(@Param("articleId") articleId: Long): Int
}

fun ArticleCommentCountRepository.increaseIfNotExistsThenInit(articleId: Long): ArticleCommentCount {
    val articleCount = findByIdOrNull(articleId)
    return articleCount?.increase() ?: save(ArticleCommentCount.init(articleId = articleId, commentCount = 1))
}

fun ArticleCommentCountRepository.decreaseIfNotExistsThenInit(articleId: Long): ArticleCommentCount {
    val articleCount = findByIdOrNull(articleId)
    return articleCount?.decrease() ?: save(ArticleCommentCount.init(articleId = articleId, commentCount = 0))
}