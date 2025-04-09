package com.scalable.articleread.repository

import com.common.dataserializer.DataSerializer
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleQueryModelRedisRepository(
    private val redisTemplate: StringRedisTemplate,
) {

    fun create(queryModel: ArticleQueryModel, ttl: Duration) {
        val value = DataSerializer.serialize(queryModel) ?: throw IllegalStateException("Failed to serialize ArticleQueryModel")
        redisTemplate.opsForValue().set(generateKey(queryModel.articleId), value, ttl)
    }

    fun update(queryModel: ArticleQueryModel) {
        val value = DataSerializer.serialize(queryModel) ?: throw IllegalStateException("Failed to serialize ArticleQueryModel")
        redisTemplate.opsForValue().setIfPresent(generateKey(queryModel.articleId), value)
    }

    fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId))
    }

    fun read(articleId: Long): ArticleQueryModel? {
        val key = generateKey(articleId)
        val serializedData = redisTemplate.opsForValue().get(key) ?: return null
        return DataSerializer.deserialize(serializedData, ArticleQueryModel::class.java)
    }

    private fun generateKey(queryModel: ArticleQueryModel): String {
        return generateKey(queryModel.articleId)
    }

    private fun generateKey(articleId: Long): String {
        return String.format(KEY_FORMAT, articleId)
    }

    companion object {
        private const val KEY_FORMAT = "article-read::article::%s"
    }
}