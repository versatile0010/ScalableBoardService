package com.scalable.view.controller

import com.scalable.view.dto.ArticleViewCountResponse
import com.scalable.view.service.ArticleViewService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleViewController(
    private val articleViewService: ArticleViewService,
) {
    @PostMapping("/v1/article-views/articles/{articleId}/users/{userId}")
    fun increase(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long,
    ): ResponseEntity<ArticleViewCountResponse> {
        val response = articleViewService.increase(
            articleId = articleId,
            userId = userId,
        )
        return ResponseEntity.ok(ArticleViewCountResponse.from(response))
    }

    @GetMapping("/v1/article-views/articles/{articleId}/count")
    fun count(
        @PathVariable("articleId") articleId: Long,
    ): ResponseEntity<ArticleViewCountResponse> {
        val response = articleViewService.count(articleId)
        return ResponseEntity.ok(ArticleViewCountResponse.from(response))
    }
}