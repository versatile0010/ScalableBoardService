package com.scalable.comment.controller

import com.scalable.comment.dto.request.CommentCreateRequest
import com.scalable.comment.dto.response.CommentCountResponse
import com.scalable.comment.dto.response.CommentPageResponse
import com.scalable.comment.dto.response.CommentResponse
import com.scalable.comment.entity.ArticleCommentCount
import com.scalable.comment.service.CommentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentController(
    private val commentService: CommentService,
) {
    @GetMapping("/v1/comments/{commentId}")
    fun read(
        @PathVariable("commentId") commentId: Long,
    ): ResponseEntity<CommentResponse> {
        val response = commentService.read(commentId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/comments")
    fun readAll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam("pageSize") pageSize: Long,
        @RequestParam("page") page: Long,
    ): ResponseEntity<CommentPageResponse> {
        val response = commentService.readAll(
            articleId = articleId,
            page = page,
            pageSize = pageSize,
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/comments/cursor")
    fun readAll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam(value = "lastParentCommentId", required = false) lastParentCommentId: Long?,
        @RequestParam(value = "lastCommentId", required = false) lastCommentId: Long?,
        @RequestParam("limit") limit: Long,
    ): ResponseEntity<CommentPageResponse> {
        val response = commentService.readAll(
            articleId = articleId,
            lastCommentId = lastCommentId,
            lastParentCommentId = lastParentCommentId,
            limit = limit,
        )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/v1/comments")
    fun create(
        @RequestBody request: CommentCreateRequest,
    ): ResponseEntity<CommentResponse> {
        val response = commentService.create(request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/comments/{commentId}")
    fun delete(
        @PathVariable("commentId") commentId: Long,
    ): ResponseEntity<Unit> {
        commentService.delete(commentId)
        return ResponseEntity.noContent().build()
    }


    @GetMapping("/v1/comments/articles/{articleId}")
    fun count(
        @PathVariable("articleId") articleId: Long,
    ): ResponseEntity<CommentCountResponse> {
        val response = commentService.count(articleId)
        return ResponseEntity.ok(response)
    }
}