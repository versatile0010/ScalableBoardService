package com.scalable.article.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity(name = "article")
class Article(
    @Id
    @Column(name = "article_id", nullable = false, updatable = false)
    val articleId: Long,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var content: String,

    @Column(name = "board_id", nullable = false)
    val boardId: Long, // 샤드 키로 사용

    @Column(name = "writer_id", nullable = false)
    val writerId: Long,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "modified_at", nullable = false)
    var modifiedAt: LocalDateTime = createdAt
) {
    fun update(title: String, content: String): Article {
        return this.apply {
            this.title = title
            this.content = content
        }
    }

    companion object {
        fun of(
            articleId: Long,
            title: String,
            content: String,
            boardId: Long,
            writerId: Long,
        ): Article {
            return Article(
                articleId = articleId,
                title = title,
                content = content,
                boardId = boardId,
                writerId = writerId,
            )
        }
    }
}