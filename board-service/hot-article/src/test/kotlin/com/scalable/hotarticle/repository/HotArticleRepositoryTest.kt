package com.scalable.hotarticle.repository

import com.scalable.hotarticle.HotArticleApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest(
    classes = [HotArticleApplication::class],
)
class HotArticleRepositoryTest {
    @Autowired
    lateinit var sut: HotArticleRepository

    @Test
    fun `add test`() {
        // given
        val time = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        val limit = 3

        // when
        sut.addByLua(
            articleId = 1L,
            time = time,
            score = 2L,
            limit = limit.toLong(),
            ttl = java.time.Duration.ofSeconds(500)
        )
        sut.addByLua(
            articleId = 2L,
            time = time,
            score = 2L,
            limit = limit.toLong(),
            ttl = java.time.Duration.ofSeconds(500)
        )
        sut.addByLua(
            articleId = 3L,
            time = time,
            score = 3L,
            limit = limit.toLong(),
            ttl = java.time.Duration.ofSeconds(500)
        )
        sut.addByLua(
            articleId = 4L,
            time = time,
            score = 4L,
            limit = limit.toLong(),
            ttl = java.time.Duration.ofSeconds(500)
        )
        sut.addByLua(
            articleId = 5L,
            time = time,
            score = 5L,
            limit = limit.toLong(),
            ttl = java.time.Duration.ofSeconds(500)
        )

        // then
        val articleIds = sut.readAll("20250101")

        assertThat(articleIds).hasSize(limit)
        assertThat(articleIds[0]).isEqualTo("5")
        assertThat(articleIds[1]).isEqualTo("4")
        assertThat(articleIds[2]).isEqualTo("3")
    }
}