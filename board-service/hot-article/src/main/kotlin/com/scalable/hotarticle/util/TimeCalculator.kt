package com.scalable.hotarticle.util

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

object TimeCalculator {
    fun calculateDurationToMidnight(
        now: LocalDateTime = LocalDateTime.now(),
    ): Duration {
        val midnight = now.plusDays(1).with(LocalTime.MIDNIGHT)
        return Duration.between(now, midnight)
    }
}
