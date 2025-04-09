package com.scalable.view

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["com.common", "com.scalable.view"])
@EnableJpaRepositories(basePackages = ["com.common", "com.scalable.view"])
@SpringBootApplication
class ViewApplication

fun main(args: Array<String>) {
    runApplication<ViewApplication>(*args)
}