package com.scalable.hotarticle

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HotArticleApplication

fun main(args: Array<String>) {
    runApplication<HotArticleApplication>(*args)
}