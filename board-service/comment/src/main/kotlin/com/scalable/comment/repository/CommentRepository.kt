package com.scalable.comment.repository

import com.scalable.comment.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.repository.query.Param

interface CommentRepository : JpaRepository<Comment, Long> {
    @Query(
        value = """
            select count(*)
            from (
                select comment_id
                from comment
                where article_id = :articleId and parent_comment_id = :parentCommentId
                limit :limit
            ) t
        """,
        nativeQuery = true
    )
    fun countByArticleIdAndParentCommentId(
        @Param("articleId") articleId: Long,
        @Param("parentCommentId") parentCommentId: Long,
        @Param("limit") limit: Long,
    ): Long
}


fun CommentRepository.findByIdOrThrow(id: Long): Comment {
    return findByIdOrNull(id)
        ?: throw IllegalArgumentException("Comment with id $id not found (even if soft-deleted)")
}

fun CommentRepository.findActivatedByIdOrThrow(id: Long): Comment {
    return findByIdOrNull(id)
        ?.takeIf { !it.isDeleted }
        ?: throw IllegalArgumentException("Comment with id $id not found or is soft-deleted")
}