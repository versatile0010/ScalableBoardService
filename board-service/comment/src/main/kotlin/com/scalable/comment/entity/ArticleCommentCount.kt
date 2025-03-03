package com.scalable.comment.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "article_comment_count")
@Entity
class ArticleCommentCount(
    @Id
    val articleId: Long, // shard key
    var commentCount: Long,
) {
    companion object {
        fun init(
            articleId: Long,
            commentCount: Long,
        ): ArticleCommentCount {
            return ArticleCommentCount(
                articleId = articleId,
                commentCount = commentCount,
            )
        }
    }

    fun increase(): ArticleCommentCount {
        return this.apply {
            commentCount++
        }
    }

    fun decrease(): ArticleCommentCount {
        return this.apply {
            if (commentCount > 0) {
                commentCount--
            }
        }
    }
}