package com.scalable.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
class ArticleCreatedTimeRepository(
    private val redisTemplate: StringRedisTemplate,
) {
    fun upsert(
        articleId: Long,
        createdTime: LocalDateTime,
        ttl: Duration,
    ) {
        redisTemplate.opsForValue()
            .set(generateKey(articleId), createdTime.toInstant(ZoneOffset.UTC).toEpochMilli().toString(), ttl)
    }

    fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId))
    }

    fun read(articleId: Long): LocalDateTime? {
        return redisTemplate.opsForValue().get(generateKey(articleId))
            ?.let { result ->
                LocalDateTime.ofInstant(Instant.ofEpochMilli(result.toLong()), ZoneOffset.UTC)
            }
    }

    private fun generateKey(articleId: Long): String {
        return String.format(KEY_FORMAT, articleId)
    }

    companion object {
        private const val KEY_FORMAT = "hot-article::article::%s::created-time"
    }
}