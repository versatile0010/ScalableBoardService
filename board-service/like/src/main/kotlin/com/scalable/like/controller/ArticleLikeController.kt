package com.scalable.like.controller

import com.scalable.like.dto.ArticleLikeResponse
import com.scalable.like.service.ArticleLikeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleLikeController(
    val articleLikeService: ArticleLikeService
) {
    @GetMapping("/v1/articles-likes/articles/{articleId}/users/{userId}")
    fun read(
        @PathVariable(value = "articleId") articleId: Long,
        @PathVariable(value = "userId") userId: Long,
    ): ResponseEntity<ArticleLikeResponse> {
        val response = articleLikeService.read(
            articleId = articleId,
            userId = userId
        )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/v1/articles-likes/articles/{articleId}/users/{userId}")
    fun like(
        @PathVariable(value = "articleId") articleId: Long,
        @PathVariable(value = "userId") userId: Long,
    ): ResponseEntity<Unit> {
        articleLikeService.like(
            articleId = articleId,
            userId = userId
        )
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/v1/articles-likes/articles/{articleId}/users/{userId}")
    fun unlike(
        @PathVariable(value = "articleId") articleId: Long,
        @PathVariable(value = "userId") userId: Long,
    ): ResponseEntity<Unit> {
        articleLikeService.unlike(
            articleId = articleId,
            userId = userId
        )
        return ResponseEntity.ok().build()
    }
}