package com.scalable.article.dto.response

import com.scalable.article.entity.BoardArticleCount

data class BoardArticleCountResponse(
    val articleCount: Long,
) {
    companion object {
        fun from(boardArticleCount: BoardArticleCount): BoardArticleCountResponse {
            return BoardArticleCountResponse(
                articleCount = boardArticleCount.articleCount,
            )
        }
    }
}
