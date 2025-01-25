package com.common.snowflake

import kotlin.random.Random

/**
 *   Snowflake Distributed Unique ID Generator.
 *   - Reference: https://github.com/callicoder/java-snowflake (Java Snowflake)
 */
class Snowflake {
    companion object {
        private const val NODE_ID_BITS = 10 // 노드(워커) ID를 표현할 비트 수
        private const val SEQUENCE_BITS = 12 // 시퀀스 번호를 표현할 비트 수

        private const val maxNodeId = (1L shl NODE_ID_BITS) - 1 // 노드 ID 최대값(2^10 - 1 = 1023)
        private const val maxSequence = (1L shl SEQUENCE_BITS) - 1 // 시퀀스 번호 최대값(2^12 - 1 = 4095)
    }

    private val nodeId: Long = Random.nextLong(maxNodeId + 1) // todo: random 제거
    private val startTimeMillis = 1704067200000L

    @Volatile
    private var lastTimeMillis = startTimeMillis // 마지막 ID 생성 시간

    @Volatile
    private var sequence = 0L // 동일한 밀리초 내에서 관리되는 시퀀스 번호

    @Synchronized
    fun nextId(): Long {
        // 1. 현재 시간에 대해서
        var currentTimeMillis = System.currentTimeMillis()

        // 2. 현재 시간 < 마지막 생성시간인 경우
        if (currentTimeMillis < lastTimeMillis) {
            throw IllegalStateException("Invalid Time")
        }

        // 3. 동일한 밀리초 요청
        if (currentTimeMillis == lastTimeMillis) {
            sequence = (sequence + 1) and maxSequence
            if (sequence == 0L) {
                currentTimeMillis = waitNextMillis(currentTimeMillis)
            }
        } else {
            sequence = 0
        }

        lastTimeMillis = currentTimeMillis

        return ((currentTimeMillis - startTimeMillis) shl (NODE_ID_BITS + SEQUENCE_BITS)) or (nodeId shl SEQUENCE_BITS) or sequence
    }

    private fun waitNextMillis(currentTimestamp: Long): Long {
        var timestamp = currentTimestamp
        while (timestamp <= lastTimeMillis) {
            timestamp = System.currentTimeMillis()
        }
        return timestamp
    }
}