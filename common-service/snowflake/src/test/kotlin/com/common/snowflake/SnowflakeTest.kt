package com.common.snowflake

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.system.measureNanoTime

class SnowflakeTest {
    private val snowflake = Snowflake()

    @Test
    fun `nextId generates unique and ordered IDs`() {
        val repeatCount = 1000 // 테스트 반복 횟수
        val idCount = 1000 // 한 번의 작업에서 생성할 ID 개수
        val executorService = Executors.newFixedThreadPool(10)

        try {
            // 1) repeatCount 만큼 병렬 작업을 실행하여 ID 리스트를 생성
            val futures: List<Future<List<Long>>> = List(repeatCount) {
                executorService.submit<List<Long>> { generateIdList(idCount) }
            }

            // 2) 각 Future 가 반환한 ID 리스트를 꺼내오면서 시간 정렬성, 고유성 테스트
            val result = mutableListOf<Long>()
            for (future in futures) {
                // 각 쓰레드에서 채번한 id
                val idList = future.get() ?: emptyList()

                // 생성된 ID가 이전 값보다 항상 커지는지 확인(밀리초 단위 시간 정렬성 테스트)
                for (i in 1 until idList.size) {
                    assertThat(idList[i]).isGreaterThan(idList[i - 1])
                }
                result.addAll(idList)
            }

            // 최종적으로 중복이 없는지 고유성 테스트
            assertThat(result.distinct().size).isEqualTo(repeatCount * idCount)
        } finally {
            executorService.shutdown()
        }
    }


    @Test
    fun `nextId performance test`() {
        val repeatCount = 1000 // 테스트 반복 횟수
        val idCount = 1000 // 한 번의 작업에서 생성할 ID 개수

        val latch = CountDownLatch(repeatCount)
        val executorService = Executors.newFixedThreadPool(10)

        try {
            // 성능 측정
            val timeInNanoseconds = measureNanoTime {
                // repeatCount 번 병렬 작업을 실행
                repeat(repeatCount) {
                    executorService.submit {
                        generateIdList(idCount)
                        latch.countDown()
                    }
                }
                // 모든 작업이 끝날 때까지 대기
                latch.await()
            }

            println("Execution time = ${timeInNanoseconds / 1_000_000} ms")
        } finally {
            executorService.shutdown()
        }
    }

    private fun generateIdList(count: Int): List<Long> =
        List(count) { snowflake.nextId() }
}