package com.scalable.like

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["com.common", "com.scalable.like"])
@EnableJpaRepositories(basePackages = ["com.common", "com.scalable.like"])
@SpringBootApplication
class LikeApplication

fun main(args: Array<String>) {
    runApplication<LikeApplication>(*args)
}