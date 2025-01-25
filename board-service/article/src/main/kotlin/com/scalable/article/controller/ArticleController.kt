package com.scalable.article.controller

import com.scalable.article.dto.request.ArticleCreateRequest
import com.scalable.article.dto.request.ArticleUpdateRequest
import com.scalable.article.dto.response.ArticlePageResponse
import com.scalable.article.dto.response.ArticleResponse
import com.scalable.article.service.ArticleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class ArticleController(
    private val articleService: ArticleService,
) {
    @GetMapping("/v1/articles/{articleId}")
    fun read(@PathVariable articleId: Long): ResponseEntity<ArticleResponse> {
        val response = articleService.read(articleId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/articles")
    fun readAll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("pageSize") pageSize: Long,
        @RequestParam("page") page: Long,
    ): ResponseEntity<ArticlePageResponse> {
        val response = articleService.readAll(
            boardId = boardId,
            pageSize = pageSize,
            page = page,
        )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/v1/articles")
    fun create(@RequestBody request: ArticleCreateRequest): ResponseEntity<ArticleResponse> {
        val response = articleService.create(request)
        return ResponseEntity.created(URI.create("/v1/articles"))
            .body(response)
    }

    @PutMapping("/v1/articles/{articleId}")
    fun update(
        @PathVariable articleId: Long,
        @RequestBody request: ArticleUpdateRequest,
    ): ResponseEntity<ArticleResponse> {
        val response = articleService.update(
            articleId = articleId,
            request = request,
        )
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/articles/{articleId}")
    fun delete(@PathVariable articleId: Long): ResponseEntity<Unit> {
        articleService.delete(articleId)
        return ResponseEntity.noContent().build()
    }
}