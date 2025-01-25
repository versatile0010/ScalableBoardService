package com.scalable.article.repository

import com.scalable.article.entity.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

interface ArticleRepository : JpaRepository<Article, Long>

fun ArticleRepository.findByIdOrThrow(id: Long): Article {
    return findByIdOrNull(id)
        ?: throw IllegalArgumentException("Article with id $id not found")
}