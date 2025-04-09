package com.scalable.articleread.repository

import com.common.event.payload.ArticleCreatedEventPayload
import com.common.event.payload.ArticleLikedEventPayload
import com.common.event.payload.ArticleUnlikedEventPayload
import com.common.event.payload.ArticleUpdatedEventPayload
import com.common.event.payload.CommentCreatedEventPayload
import com.common.event.payload.CommentDeletedEventPayload
import com.scalable.articleread.client.ArticleClient

data class ArticleQueryModel(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: String,
    val modifiedAt: String,
    val commentCount: Long,
    val likeCount: Long,
) {
    companion object {
        fun fromEventPayload(articleCreatedEventPayload: ArticleCreatedEventPayload): ArticleQueryModel {
            return ArticleQueryModel(
                articleId = articleCreatedEventPayload.articleId,
                title = articleCreatedEventPayload.title,
                content = articleCreatedEventPayload.content,
                boardId = articleCreatedEventPayload.boardId,
                writerId = articleCreatedEventPayload.writerId,
                createdAt = articleCreatedEventPayload.createdAt.toString(),
                modifiedAt = articleCreatedEventPayload.modifiedAt.toString(),
                commentCount = 0L,
                likeCount = 0L,
            )
        }

        fun create(
            articleResponse: ArticleClient.ArticleResponse,
            commentCount: Long,
            likeCount: Long,
            viewCount: Long,
        ): ArticleQueryModel {
            return ArticleQueryModel(
                articleId = articleResponse.articleId,
                title = articleResponse.title,
                content = articleResponse.content,
                boardId = articleResponse.boardId,
                writerId = articleResponse.writerId,
                createdAt = articleResponse.createdAt.toString(),
                modifiedAt = articleResponse.createdAt.toString(),
                commentCount = commentCount,
                likeCount = likeCount,
            )
        }
    }

    fun updatedBy(payload: CommentCreatedEventPayload): ArticleQueryModel {
        return this.copy(
            commentCount = payload.articleCommentCount,
            modifiedAt = payload.createdAt.toString(),
        )
    }

    fun updatedBy(payload: CommentDeletedEventPayload): ArticleQueryModel {
        return this.copy(
            commentCount = payload.articleCommentCount,
            modifiedAt = payload.createdAt.toString(),
        )
    }

    fun updatedBy(payload: ArticleLikedEventPayload): ArticleQueryModel {
        return this.copy(
            likeCount = payload.articleLikeCount,
            modifiedAt = payload.createdAt.toString(),
        )
    }

    fun updatedBy(payload: ArticleUnlikedEventPayload): ArticleQueryModel {
        return this.copy(
            likeCount = payload.articleLikeCount,
            modifiedAt = payload.createdAt.toString(),
        )
    }

    fun updatedBy(payload: ArticleUpdatedEventPayload): ArticleQueryModel {
        return this.copy(
            articleId = payload.articleId,
            title = payload.title,
            content = payload.content,
            boardId = payload.boardId,
            writerId = payload.writerId,
            createdAt = payload.createdAt.toString(),
            modifiedAt = payload.createdAt.toString(),
            commentCount = commentCount,
            likeCount = likeCount,
        )
    }
}
