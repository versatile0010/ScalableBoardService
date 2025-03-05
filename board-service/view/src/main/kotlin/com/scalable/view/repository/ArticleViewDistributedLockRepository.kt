package com.scalable.view.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Repository
class ArticleViewDistributedLockRepository(
    private val stringRedisTemplate: StringRedisTemplate
) {
    fun lock(
        articleId: Long,
        userId: Long,
        ttl: Duration,
    ): Boolean {
        val key = generateKey(
            articleId = articleId,
            userId = userId,
        )
        return stringRedisTemplate.opsForValue().setIfAbsent(key, "", ttl.toJavaDuration()) ?: false
    }

    private fun generateKey(
        articleId: Long,
        userId: Long,
    ): String {
        return "view::article::$articleId::user::$userId::lock"
    }
}