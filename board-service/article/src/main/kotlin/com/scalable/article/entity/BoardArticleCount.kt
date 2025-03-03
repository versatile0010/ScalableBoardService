package com.scalable.article.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "board_artile_count")
@Entity
class BoardArticleCount(
    @Id
    val boardId: Long,
    var articleCount: Long,
) {
    companion object {
        fun init(boardId: Long, articleCount: Long): BoardArticleCount {
            return BoardArticleCount(
                boardId = boardId,
                articleCount = articleCount
            )
        }
    }

    fun increase() : BoardArticleCount {
        return this.apply {
            articleCount++
        }
    }

    fun decrease() : BoardArticleCount {
        return this.apply {
            articleCount--
        }
    }
}