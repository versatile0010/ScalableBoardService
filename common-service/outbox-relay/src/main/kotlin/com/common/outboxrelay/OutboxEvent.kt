package com.common.outboxrelay

data class OutboxEvent(
    val outbox: Outbox,
) {
    companion object {
        fun from(
            outbox: Outbox,
        ): OutboxEvent {
            return OutboxEvent(
                outbox = outbox,
            )
        }
    }
}