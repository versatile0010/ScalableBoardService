package com.scalable.comment.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "comment")
class Comment(
    @Id
    @Column(name = "comment_id", nullable = false, updatable = false)
    var commentId: Long,
    var content: String,
    var parentCommentId: Long,
    var articleId: Long, // shard key
    var writerId: Long,
    var isDeleted: Boolean,
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun of(
            commentId: Long,
            content: String,
            parentCommentId: Long?,
            articleId: Long,
            writerId: Long,
        ): Comment {
            return Comment(
                commentId = commentId,
                content = content,
                parentCommentId = parentCommentId ?: commentId,
                articleId = articleId,
                writerId = writerId,
                isDeleted = false,
            )
        }
    }

    fun isRoot(): Boolean {
        return parentCommentId == commentId
    }

    fun softDeleted(): Comment {
        return this.apply {
            isDeleted = true
        }
    }
}