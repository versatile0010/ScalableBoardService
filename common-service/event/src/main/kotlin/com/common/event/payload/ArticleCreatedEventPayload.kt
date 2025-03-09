package com.common.event.payload

import java.time.LocalDateTime

data class ArticleCreatedEventPayload(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val boardArticleCount: Long,
) : EventPayload