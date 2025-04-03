package com.scalable.hotarticle.controller

import com.scalable.hotarticle.service.HotArticleService
import com.scalable.hotarticle.service.response.HotArticleResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class HotArticleController(
    private val hotArticleService: HotArticleService,
) {
    @GetMapping("/v1/hot-articles/date/{dateString}")
    fun readAll(@PathVariable dateString: String): List<HotArticleResponse> {
        return hotArticleService.readAll(dateString)
    }
}