package com.scalable.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleViewCountRepository(
    private val redisTemplate: StringRedisTemplate,
) {
    fun upsert(
        articleId: Long,
        viewCount: Long,
        ttl: Duration,
    ) {
        redisTemplate.opsForValue().set(generateKey(articleId), viewCount.toString(), ttl)
    }

    fun read(
        articleId: Long,
    ): Long {
        return redisTemplate.opsForValue().get(generateKey(articleId))
            ?.toLong()
            ?: 0L
    }

    private fun generateKey(articleId: Long): String {
        return String.format(KEY_FORMAT, articleId)
    }

    companion object {
        private const val KEY_FORMAT = "hot-article::article::%s::view-count"
    }
}