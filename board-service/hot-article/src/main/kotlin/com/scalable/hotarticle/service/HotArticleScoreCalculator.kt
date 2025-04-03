package com.scalable.hotarticle.service

import com.scalable.hotarticle.repository.ArticleCommentCountRepository
import com.scalable.hotarticle.repository.ArticleLikeCountRepository
import com.scalable.hotarticle.repository.ArticleViewCountRepository
import org.springframework.stereotype.Component

@Component
class HotArticleScoreCalculator(
    private val articleLikeCountRepository: ArticleLikeCountRepository,
    private val articleViewCountRepository: ArticleViewCountRepository,
    private val articleCommentCountRepository: ArticleCommentCountRepository,
) {
    fun calculate(articleId: Long): Long {
        val likeCount = articleLikeCountRepository.read(articleId)
        val viewCount = articleViewCountRepository.read(articleId)
        val commentCount = articleCommentCountRepository.read(articleId)

        return (likeCount * ARTICLE_LIKE_COUNT_WEIGHT) +
                (viewCount * ARTICLE_VIEW_COUNT_WEIGHT) +
                (commentCount * ARTICLE_COMMENT_COUNT_WEIGHT)
    }

    companion object {
        private const val ARTICLE_LIKE_COUNT_WEIGHT = 3
        private const val ARTICLE_COMMENT_COUNT_WEIGHT = 2
        private const val ARTICLE_VIEW_COUNT_WEIGHT = 1
    }
}