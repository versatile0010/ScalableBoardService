package com.scalable.view.service

import com.scalable.view.repository.ArticleViewCountRepository
import org.springframework.stereotype.Service

@Service
class ArticleViewService(
    private val articleViewCountRepository: ArticleViewCountRepository,
    private val articleViewCountBackUpProcessor: ArticleViewCountBackUpProcessor,
) {
    companion object {
        private const val BACK_UP_BATCH_SIZE = 100
    }

    fun increase(
        articleId: Long,
        userId: Long,
    ): Long {
        val count = articleViewCountRepository.increase(articleId)
        if (count % BACK_UP_BATCH_SIZE == 0L) {
            articleViewCountBackUpProcessor.backUp(
                articleId = articleId,
                viewCount = count,
            )
        }
        return count
    }

    fun count(articleId: Long): Long = articleViewCountRepository.read(articleId)
}