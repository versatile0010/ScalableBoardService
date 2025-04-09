package com.common.outboxrelay

import com.common.event.Event
import com.common.event.EventType
import com.common.event.payload.EventPayload
import com.common.snowflake.Snowflake
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class OutboxEventPublisher(
    private val outboxIdGenerator: Snowflake = Snowflake(),
    private val eventIdGenerator: Snowflake = Snowflake(),
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun publish(
        type: EventType,
        payload: EventPayload,
        shardKey: Long,
    ) {
        val outbox = Outbox.create(
            outboxId = outboxIdGenerator.nextId(),
            eventType = type,
            payload = Event(
                eventId = eventIdGenerator.nextId(),
                eventType = type,
                payload = payload,
            ).toJson(),
            shardKey = shardKey % MessageRelayConstants.SHARD_COUNT,
        )
        eventPublisher.publishEvent(
            OutboxEvent.from(outbox)
        )
    }
}