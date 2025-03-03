package com.scalable.like.controller

import com.scalable.like.dto.ArticleLikeCountResponse
import com.scalable.like.dto.ArticleLikeResponse
import com.scalable.like.service.ArticleLikeService
import org.springframework.context.ApplicationContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleLikeController(
    val articleLikeService: ArticleLikeService,
    private val applicationContext: ApplicationContext
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

    @PostMapping("/v1/articles-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1")
    fun likePessimisticLock1(
        @PathVariable(value = "articleId") articleId: Long,
        @PathVariable(value = "userId") userId: Long,
    ): ResponseEntity<Unit> {
        articleLikeService.likePessimisticLock1(
            articleId = articleId,
            userId = userId,
        )
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/v1/articles-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1")
    fun unlikePessimisticLock1(
        @PathVariable(value = "articleId") articleId: Long,
        @PathVariable(value = "userId") userId: Long,
    ): ResponseEntity<Unit> {
        articleLikeService.unlikePessimisticLock1(
            articleId = articleId,
            userId = userId,
        )
        return ResponseEntity.ok().build()
    }


    @PostMapping("/v1/articles-likes/articles/{articleId}/users/{userId}/pessimistic-lock-2")
    fun likePessimisticLock2(
        @PathVariable(value = "articleId") articleId: Long,
        @PathVariable(value = "userId") userId: Long,
    ): ResponseEntity<Unit> {
        articleLikeService.likePessimisticLock2(
            articleId = articleId,
            userId = userId,
        )
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/v1/articles-likes/articles/{articleId}/users/{userId}/pessimistic-lock-2")
    fun unlikePessimisticLock2(
        @PathVariable(value = "articleId") articleId: Long,
        @PathVariable(value = "userId") userId: Long,
    ): ResponseEntity<Unit> {
        articleLikeService.unlikePessimisticLock2(
            articleId = articleId,
            userId = userId,
        )
        return ResponseEntity.ok().build()
    }


    @PostMapping("/v1/articles-likes/articles/{articleId}/users/{userId}/optimistic-lock")
    fun likeOptimisticLock(
        @PathVariable(value = "articleId") articleId: Long,
        @PathVariable(value = "userId") userId: Long,
    ): ResponseEntity<Unit> {
        articleLikeService.likeOptimisticLock(
            articleId = articleId,
            userId = userId,
        )
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/v1/articles-likes/articles/{articleId}/users/{userId}/optimistic-lock")
    fun unlikeOptimisticLock(
        @PathVariable(value = "articleId") articleId: Long,
        @PathVariable(value = "userId") userId: Long,
    ): ResponseEntity<Unit> {
        articleLikeService.unlikeOptimisticLock(
            articleId = articleId,
            userId = userId,
        )
        return ResponseEntity.ok().build()
    }

    @GetMapping("/v1/articles-likes/articles/{articleId}/count")
    fun count(
        @PathVariable(value = "articleId") articleId: Long,
    ): ResponseEntity<ArticleLikeCountResponse> {
        val response = articleLikeService.count(articleId)
        return ResponseEntity.ok(response)
    }

}