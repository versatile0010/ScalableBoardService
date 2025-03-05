package com.scalable.view.service

import com.scalable.view.repository.ArticleViewCountBackUpRepository
import com.scalable.view.repository.updateViewCountIfNotExistsThenInit
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ArticleViewCountBackUpProcessor(
    private val backUpRepository: ArticleViewCountBackUpRepository
) {
    @Transactional
    fun backUp(
        articleId: Long,
        viewCount: Long
    ) {
        backUpRepository.updateViewCountIfNotExistsThenInit(
            articleId = articleId,
            viewCount = viewCount,
        )
    }
}