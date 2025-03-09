package com.common.dataserializer

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.oshai.kotlinlogging.KotlinLogging

object DataSerializer {
    private val objectMapper: ObjectMapper = init()

    private val logger = KotlinLogging.logger {}

    private fun init(): ObjectMapper {
        return ObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun <T> deserialize(json: String, clazz: Class<T>): T? {
        return runCatching {
            objectMapper.readValue(json, clazz)
        }.onFailure {
            logger.error(it) { "Failed to deserialize json: $json with class: $clazz" }
        }.getOrNull()
    }

    fun <T> deserialize(data: Any, clazz: Class<T>): T? {
        return runCatching {
            objectMapper.convertValue(data, clazz)
        }.onFailure {
            logger.error(it) { "Failed to deserialize object: $data with class: $clazz" }
        }.getOrNull()
    }

    fun <T> serialize(data: T): String? {
        return runCatching {
            objectMapper.writeValueAsString(data)
        }.onFailure {
            logger.error(it) { "Failed to serialize object: $data" }
        }.getOrNull()
    }
}