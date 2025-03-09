package com.common.event.payload

import java.time.LocalDateTime

data class ArticleLikedEventPayload(
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: LocalDateTime,
    val articleLikeCount: Int,
) : EventPayload