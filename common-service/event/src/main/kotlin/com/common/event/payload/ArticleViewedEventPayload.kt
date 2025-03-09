package com.common.event.payload

data class ArticleViewedEventPayload(
    val articleId: Long,
    val articleViewCount: Long,
) : EventPayload