package com.scalable.like.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "article_like")
@Entity
class ArticleLike(
    @Id
    @Column(name = "article_like_id")
    val articleLikeId: Long,
    @Column(name = "article_id", nullable = false)
    val articleId: Long, // shard key
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(
            articleLikeId: Long,
            articleId: Long,
            userId: Long,
        ): ArticleLike {
            return ArticleLike(
                articleLikeId = articleLikeId,
                articleId = articleId,
                userId = userId,
            )
        }
    }
}