package com.scalable.view.repository

import com.scalable.view.entity.ArticleViewCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ArticleViewCountBackUpRepository : JpaRepository<ArticleViewCount, Long> {
    @Query(
        nativeQuery = true,
        value = """
            update article_view_count set view_count = :viewCount where article_id = :articleId and view_count < :viewCount
        """
    )
    @Modifying
    fun updateViewCount(
        @Param("articleId") articleId: Long,
        @Param("viewCount") viewCount: Long,
    )
}

fun ArticleViewCountBackUpRepository.updateViewCountIfNotExistsThenInit(
    articleId: Long,
    viewCount: Long
): ArticleViewCount {
    val articleViewCount = findByIdOrNull(articleId)
    if (articleViewCount != null) {
        updateViewCount(
            articleId = articleId,
            viewCount = viewCount,
        )
    }
    return save(ArticleViewCount.init(articleId = articleId, viewCount = viewCount))
}