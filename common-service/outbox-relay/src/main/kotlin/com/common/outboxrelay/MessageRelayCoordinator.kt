package com.common.outboxrelay

import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class MessageRelayCoordinator(
    private val redisTemplate: StringRedisTemplate,
    @Value("\${spring.application.name}")
    private val applicationName: String,
) {

    fun retrieveAssignedShards(): AssignedShard {
        return AssignedShard.of(
            appId = APP_ID,
            appIds = findAppIds(),
            shardCount = MessageRelayConstants.SHARD_COUNT,
        )
    }

    @Scheduled(fixedDelay = PING_INTERVAL_SECOND, timeUnit = TimeUnit.SECONDS)
    fun ping() {
        redisTemplate.executePipelined { action ->
            val connection = action as StringRedisConnection
            val key = generateKey()
            connection.zAdd(key, Instant.now().toEpochMilli().toDouble(), APP_ID)
            connection.zRemRangeByScore(key, Double.NEGATIVE_INFINITY, Instant.now().minusSeconds(PING_INTERVAL_SECOND * PING_FAILURE_THRESHOLD).toEpochMilli().toDouble())
            return@executePipelined null
        }
    }

    @PreDestroy
    fun leave() {
        redisTemplate.opsForZSet().remove(generateKey(), APP_ID)
    }


    private fun findAppIds(): List<String> {
        val appIds = redisTemplate.opsForZSet().reverseRange(generateKey(), 0, -1)
        return appIds?.sorted() ?: emptyList()
    }

    private fun generateKey(): String {
        return "outbox-message-relay-coordinator::app-list::$applicationName"
    }

    companion object {
        private val APP_ID = UUID.randomUUID().toString()
        private const val PING_INTERVAL_SECOND = 3L
        private const val PING_FAILURE_THRESHOLD = 3L
    }
}