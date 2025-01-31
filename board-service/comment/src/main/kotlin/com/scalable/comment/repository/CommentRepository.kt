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

    @Query(
        value = """
            select comment.comment_id, comment.parent_comment_id, comment.article_id, comment.writer_id,
                   comment.content, comment.is_deleted,  comment.created_at
            from comment
            where article_id = :articleId
            order by parent_comment_id, comment_id 
            limit :limit offset :offset
        """,
        nativeQuery = true
    )
    fun findAllByArticleId(
        @Param("articleId") articleId: Long,
        @Param("offset") offset: Long,
        @Param("limit") limit: Long,
    ): List<Comment>

    @Query(
        value = """
            select count(*)
            from (
                select comment_id
                from comment
                where article_id = :articleId
                limit :limit
            ) t
        """,
        nativeQuery = true
    )
    fun countAll(
        @Param("articleId") articleId: Long,
        @Param("limit") limit: Long,
    ): Long

    @Query(
        value = """
            select comment.comment_id, comment.parent_comment_id, comment.article_id, comment.writer_id,
                   comment.content, comment.is_deleted,  comment.created_at
            from comment
            where article_id = :articleId and (
                comment_id > :lastCommentId or 
                (comment_id = :lastCommentId and parent_comment_id > :lastParentCommentId) 
            )
            order by parent_comment_id, comment_id limit :limit
        """,
        nativeQuery = true
    )
    fun findAllByArticeIdAndLastCommentIdAndLastParentCommentId(
        @Param("articleId") articleId: Long,
        @Param("lastCommentId") lastCommentId: Long,
        @Param("lastParentCommentId") lastParentCommentId: Long,
        @Param("limit") limit: Long,
    ): List<Comment>
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