package com.common.outboxrelay

import com.common.event.EventType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "outbox")
@Entity
class Outbox(
    @Id
    val outboxId: Long,
    @Enumerated(EnumType.STRING)
    val eventType: EventType,
    val payload: String,
    val shardKey: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun create(
            outboxId: Long,
            eventType: EventType,
            payload: String,
            shardKey: Long,
        ): Outbox {
            return Outbox(
                outboxId = outboxId,
                eventType = eventType,
                payload = payload,
                shardKey = shardKey,
                createdAt = LocalDateTime.now(),
            )
        }
    }
}