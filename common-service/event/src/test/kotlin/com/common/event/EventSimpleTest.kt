package com.common.event

import com.common.event.payload.ArticleCreatedEventPayload
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EventSimpleTest {
    @Test
    fun serde() {
        // given
        val payload = ArticleCreatedEventPayload(
            articleId = 1,
            title = "title",
            content = "content",
            boardId = 1,
            writerId = 1,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now(),
            boardArticleCount = 1
        )

        val event = Event(
            eventId = 1234,
            eventType = EventType.ARTICLE_CREATED,
            payload = payload,
        )
        // when
        val json = event.toJson()
        println("json = $json")
        val result = Event.fromJson(json)

        // then
        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result?.eventId).isEqualTo(1234)
        Assertions.assertThat(result?.eventType).isEqualTo(EventType.ARTICLE_CREATED)
        Assertions.assertThat(result?.payload).isEqualTo(payload)

    }
}