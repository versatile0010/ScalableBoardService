package com.scalable.like.dto

import com.scalable.like.entity.ArticleLikeCount

data class ArticleLikeCountResponse(
    val count: Long,
) {
    companion object {
        fun from(articleLikeCount: ArticleLikeCount?): ArticleLikeCountResponse {
            return ArticleLikeCountResponse(
                count = articleLikeCount?.likeCount ?: 0L,
            )
        }
    }
}
