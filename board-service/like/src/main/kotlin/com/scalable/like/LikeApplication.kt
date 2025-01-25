package com.scalable.like

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LikeApplication

fun main(args: Array<String>) {
    runApplication<LikeApplication>(*args)
}