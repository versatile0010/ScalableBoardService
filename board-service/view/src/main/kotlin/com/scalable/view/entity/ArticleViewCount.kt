package com.scalable.view.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "article_view_count")
@Entity
class ArticleViewCount(
    @Id
    val articleId: Long, // shard key
    val viewCount: Long,
) {
    companion object {
        fun init(articleId: Long, viewCount: Long): ArticleViewCount {
            return ArticleViewCount(
                articleId = articleId,
                viewCount = viewCount,
            )
        }
    }
}