package com.scalable.view.dto

data class ArticleViewCountResponse(
    val viewCount: Long,
) {
    companion object {
        fun from(viewCount: Long): ArticleViewCountResponse {
            return ArticleViewCountResponse(
                viewCount = viewCount,
            )
        }
    }
}
