package com.scalable.articleread.controller

import com.scalable.articleread.service.ArticleReadService
import com.scalable.articleread.service.response.ArticleReadResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleReadController(
    private val articleReadService: ArticleReadService,
) {
    @GetMapping("/v1/articles/{articleId}")
    fun read(@PathVariable("articleId") articleId: Long): ArticleReadResponse {
        return articleReadService.read(articleId)
    }
}