package com.scalable.view.service

import com.scalable.view.repository.ArticleViewCountRepository
import com.scalable.view.repository.ArticleViewDistributedLockRepository
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.seconds

@Service
class ArticleViewService(
    private val articleViewCountRepository: ArticleViewCountRepository,
    private val articleViewCountBackUpProcessor: ArticleViewCountBackUpProcessor,
    private val articleViewDistributedLockRepository: ArticleViewDistributedLockRepository,
) {
    private val timeToLeave = 300.seconds
    private val batchSize = 100

    fun increase(
        articleId: Long,
        userId: Long,
    ): Long {
        // try lock acquire
        if (!articleViewDistributedLockRepository.lock(articleId, userId, timeToLeave)) {
            // if lock acquire failed, return current view count
            return articleViewCountRepository.read(articleId)
        }
        // else increase view count
        val count = articleViewCountRepository.increase(articleId)
        if (count % batchSize == 0L) {
            articleViewCountBackUpProcessor.backUp(
                articleId = articleId,
                viewCount = count,
            )
        }
        return count
    }

    fun count(articleId: Long): Long = articleViewCountRepository.read(articleId)
}