package com.scalable.comment.controller

import com.scalable.comment.dto.request.CommentCreateRequest
import com.scalable.comment.dto.response.CommentResponse
import com.scalable.comment.service.CommentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
}