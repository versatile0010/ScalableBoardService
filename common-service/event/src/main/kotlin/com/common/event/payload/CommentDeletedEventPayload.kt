package com.common.event.payload

import java.time.LocalDateTime

data class CommentDeletedEventPayload(
    val commentId: Long,
    val content: String,
    val path: String?,
    val articleId: Long,
    val writerId: Long,
    val deleted: Boolean,
    val createdAt: LocalDateTime,
    val articleCommentCount: Long,
) : EventPayload