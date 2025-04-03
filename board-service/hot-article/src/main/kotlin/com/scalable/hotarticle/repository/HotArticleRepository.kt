package com.scalable.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class HotArticleRepository(
    private val redisTemplate: StringRedisTemplate,
) {
    fun add(
        articleId: Long,
        time: LocalDateTime,
        score: Long,
        limit: Long,
        ttl: Duration,
    ) {
        val key = generateKey(time)
        redisTemplate.opsForZSet().add(key, articleId.toString(), score.toDouble())
        val size = redisTemplate.opsForZSet().zCard(key) ?: 0
        if (size > limit) {
            val removeCount = size - limit
            redisTemplate.opsForZSet().removeRange(key, 0, removeCount - 1)
        }
        redisTemplate.expire(key, ttl)
    }

    fun addByLua(
        articleId: Long,
        time: LocalDateTime,
        score: Long,
        limit: Long,
        ttl: Duration,
    ) {
        val lua = """
            redis.call('ZADD', KEYS[1], ARGV[1], ARGV[2])
            local size = redis.call('ZCARD', KEYS[1])
            if size > tonumber(ARGV[3]) then
                redis.call('ZREMRANGEBYRANK', KEYS[1], 0, size - tonumber(ARGV[3]) - 1)
            end
            redis.call('EXPIRE', KEYS[1], tonumber(ARGV[4]))
            return 1
        """.trimIndent()

        redisTemplate.execute(
            DefaultRedisScript(lua, Long::class.java),
            listOf(generateKey(time)),
            score.toString(),
            articleId.toString(),
            limit.toString(),
            ttl.toSeconds().toString()
        )
    }

    fun remove(
        articleId: Long,
        time: LocalDateTime,
    ) {
        redisTemplate.opsForZSet().remove(generateKey(time), articleId.toString())
    }

    fun readAll(timeString: String): List<String> {
        return redisTemplate.opsForZSet()
            .reverseRangeWithScores(generateKey(timeString), 0, -1)
            ?.mapNotNull { it.value }
            ?: emptyList()
    }

    private fun generateKey(time: LocalDateTime): String {
        return String.format(KEY_FORMAT, time.format(formatter))
    }

    private fun generateKey(timeString: String): String {
        return String.format(KEY_FORMAT, timeString)
    }

    companion object {
        private const val KEY_FORMAT = "hot-article::list::%s"
        private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    }
}