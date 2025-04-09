package com.common.outboxrelay

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OutboxRepository : JpaRepository<Outbox, Long> {
    fun findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
        shardKey: Long,
        from: LocalDateTime,
        pageable: Pageable,
    ): List<Outbox>
}