package com.common.event.payload

import java.time.LocalDateTime

data class ArticleUnlikedEventPayload(
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: LocalDateTime,
    val articleLikeCount: Long,
) : EventPayload