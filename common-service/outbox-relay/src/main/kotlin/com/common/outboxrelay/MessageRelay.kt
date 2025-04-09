package com.common.outboxrelay

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Component
class MessageRelay(
    private val outboxRepository: OutboxRepository,
    private val messageRelayCoordinator: MessageRelayCoordinator,
    private val messageRelayKafkaTemplate: KafkaTemplate<String, String>,
) {
    private val logger = KotlinLogging.logger {}

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun createOutbox(event: OutboxEvent) {
        outboxRepository.save(event.outbox)
    }

    @Async("messageRelayPublishEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun publishEvent(event: OutboxEvent) {
        publish(event.outbox)
    }

    @Scheduled(
        fixedDelay = 10,
        initialDelay = 5,
        timeUnit = TimeUnit.SECONDS,
        scheduler = "messageRelayPublishPendingEventExecutor"
    )
    fun publishPendingEvents() {
        val assignedShards = messageRelayCoordinator.retrieveAssignedShards()
            .also { logger.info { "Assigned shards: $it. size=${it.shards.size}" } }

        for (shard in assignedShards.shards) {
            val outboxes = outboxRepository.findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
                    shardKey = shard,
                    from = LocalDateTime.now().minusSeconds(10),
                    pageable = Pageable.ofSize(100),
            )
            for(outbox: Outbox in outboxes) {
                publish(outbox)
            }
        }

    }

    private fun publish(outbox: Outbox) {
        runCatching {
            messageRelayKafkaTemplate.send(
                outbox.eventType.topic,
                outbox.shardKey.toString(),
                outbox.payload,
            ).get(1, TimeUnit.SECONDS)
        }.onFailure {
            logger.error { "Failed to publish event $outbox" }
        }.onSuccess {
            outboxRepository.delete(outbox)
        }
    }
}