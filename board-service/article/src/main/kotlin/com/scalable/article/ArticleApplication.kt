package com.scalable.article

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["com.common", "com.scalable.article"])
@EnableJpaRepositories(basePackages = ["com.common", "com.scalable.article"])
@SpringBootApplication
class ArticleApplication

fun main(args: Array<String>) {
    runApplication<ArticleApplication>(*args)
}