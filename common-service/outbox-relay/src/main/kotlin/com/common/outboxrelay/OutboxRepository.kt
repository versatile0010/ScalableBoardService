package com.common.outboxrelay

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface OutboxRepository : JpaRepository<Outbox, Long> {
    fun findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
        shardKey: Long,
        from: LocalDateTime,
        pageable: Pageable,
    ): List<Outbox>
}