package com.scalable.like.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Table(name = "article_like_count")
@Entity
class ArticleLikeCount(
    @Id
    @Column(name = "article_id")
    val articleId: Long, // shard key
    @Version
    val version: Long? = null,
    @Column(name = "like_count")
    var likeCount: Long = 0L,
) {
    companion object {
        fun init(
            articleId: Long,
            likeCount: Long,
        ): ArticleLikeCount {
            return ArticleLikeCount(
                articleId = articleId,
                likeCount = likeCount
            )
        }
    }

    fun increment(): ArticleLikeCount {
        return this.apply {
            this.likeCount += 1
        }

    }

    fun decrement(): ArticleLikeCount {
        return this.apply {
            if (this.likeCount > 0) {
                this.likeCount -= 1
            }
        }
    }
}