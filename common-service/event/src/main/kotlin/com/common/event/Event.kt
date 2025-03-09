package com.common.event

import com.common.dataserializer.DataSerializer
import com.common.event.payload.ArticleCreatedEventPayload
import com.common.event.payload.ArticleDeletedEventPayload
import com.common.event.payload.ArticleLikedEventPayload
import com.common.event.payload.ArticleUnlikedEventPayload
import com.common.event.payload.ArticleUpdatedEventPayload
import com.common.event.payload.ArticleViewedEventPayload
import com.common.event.payload.CommentCreatedEventPayload
import com.common.event.payload.CommentDeletedEventPayload
import com.common.event.payload.EventPayload
import com.common.event.topic.Topic
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

data class Event<T : EventPayload>(
    val eventId: Long,
    val eventType: EventType,
    val payload: T,
) {
    fun toJson(): String {
        return DataSerializer.serialize(this) ?: ""
    }

    companion object {
        fun fromJson(json: String): Event<EventPayload>? {
            val row = DataSerializer.deserialize(json, EventRow::class.java) ?: return null
            val eventType = EventType.from(row.eventType) ?: return null
            val payload = DataSerializer.deserialize(row.payload, eventType.payloadType) ?: return null

            return Event(
                eventId = row.eventId,
                eventType = eventType,
                payload = payload,
            )
        }
    }

}

enum class EventType(
    val payloadType: Class<out EventPayload>,
    val topic: String,
) {
    ARTICLE_CREATED(ArticleCreatedEventPayload::class.java, Topic.ARTICLE),
    ARTICLE_UPDATED(ArticleUpdatedEventPayload::class.java, Topic.ARTICLE),
    ARTICLE_DELETED(ArticleDeletedEventPayload::class.java, Topic.ARTICLE),
    COMMENT_CREATED(CommentCreatedEventPayload::class.java, Topic.COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload::class.java, Topic.COMMENT),
    ARTICLE_LIKED(ArticleLikedEventPayload::class.java, Topic.ARTICLE_LIKE),
    ARTICLE_UNLIKED(ArticleUnlikedEventPayload::class.java, Topic.ARTICLE_LIKE),
    ARTICLE_VIEWED(ArticleViewedEventPayload::class.java, Topic.ARTICLE_VIEW),
    ;

    companion object {
        fun from(type: String): EventType? {
            return kotlin.runCatching {
                valueOf(type)
            }.onFailure {
                logger.error(it) { "Failed to convert EventType from $type" }
            }.getOrNull()
        }
    }
}

data class EventRow(
    val eventId: Long,
    val eventType: String,
    val payload: Any,
)


