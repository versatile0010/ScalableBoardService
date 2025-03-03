package com.scalable.like.controller

import com.scalable.like.LikeApplication
import com.scalable.like.dto.ArticleLikeCountResponse
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SpringBootTest(
    classes = [LikeApplication::class],
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@Transactional
class ArticleLikeApiPerformanceTest {
    @LocalServerPort
    private var port: Int = 0

    private lateinit var client: RestClient

    @Autowired
    private lateinit var entityManager: EntityManager

    @BeforeEach
    fun init() {
        client = RestClient.create("http://localhost:$port")
    }

    @Test
    fun likeSimplePerformanceTest() {
        val executorService = Executors.newFixedThreadPool(5000)
        likeSimplePerformanceTest(
            executorService = executorService,
            articleId = 1L,
            lockType = LockType.PESSIMISTIC_1,
        )
        likeSimplePerformanceTest(
            executorService = executorService,
            articleId = 2L,
            lockType = LockType.PESSIMISTIC_2,
        )
        likeSimplePerformanceTest(
            executorService = executorService,
            articleId = 3L,
            lockType = LockType.OPTIMISTIC,
        )
    }

    private fun likeSimplePerformanceTest(
        executorService: ExecutorService,
        articleId: Long,
        lockType: LockType,
    ) {
        val latch = CountDownLatch(1000)
        println("likeSimplePerformanceTest articleId: $articleId, lockType: $lockType")
        cleanUpDatabase()

        entityManager.createNativeQuery("TRUNCATE TABLE article_like.article_like").executeUpdate()
        entityManager.createNativeQuery("TRUNCATE TABLE article_like.article_like_count").executeUpdate()

        likeApiCall(
            articleId = articleId,
            userId = 1L,
            lockType = lockType,
        )

        // start time
        val startTime = System.currentTimeMillis()
        for(i in 1 until 1000) {
            val userId = i + 2
            executorService.submit {
                likeApiCall(
                    articleId = articleId,
                    userId = userId.toLong(),
                    lockType = lockType,
                )
                latch.countDown()
            }
        }
        latch.await()
        val endTime = System.currentTimeMillis()
        println("""
            likeSimplePerformanceTest articleId: $articleId, lockType: $lockType
            Total time: ${endTime - startTime} ms
            Count: ${getLikeCountApiCall(articleId)}
        """.trimIndent())

        cleanUpDatabase()
    }

    private fun likeApiCall(
        articleId: Long,
        userId: Long,
        lockType: LockType,
    ) {
        client.post()
            .uri("/v1/articles-likes/articles/$articleId/users/$userId/${lockType.path}")
            .retrieve()
    }

    private fun getLikeCountApiCall(
        articleId: Long,
    ) : ArticleLikeCountResponse {
        return client.get()
            .uri("/v1/articles-likes/articles/$articleId/count")
            .retrieve()
            .body(ArticleLikeCountResponse::class.java)
            ?: throw IllegalStateException("Failed to get like count")
    }

    private fun cleanUpDatabase() {
        entityManager.createNativeQuery("TRUNCATE TABLE article_like.article_like").executeUpdate()
        entityManager.createNativeQuery("TRUNCATE TABLE article_like.article_like_count").executeUpdate()
    }

    private enum class LockType(
        val path: String,
    ) {
        PESSIMISTIC_1("pessimistic-lock-1"), // update 시점에 pessimistic lock 점유
        PESSIMISTIC_2("pessimistic-lock-2"), // select 시점에 pessimistic lock 점유
        OPTIMISTIC("optimistic-lock"), // version
        ;
    }
}