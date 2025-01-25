package util

import com.scalable.article.global.PageHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PageHelperTest {

    /**
     *  특정 페이지와 페이지 크기, 검색 가능한 최대 페이지 개수에 따라,
     *  전체 검색 가능한 항목 수(전체 페이지 수 * 페이지 크기)가 올바르게 계산되는지 검증.
     */
    @Test
    fun `should count correct number of pages considering searchable page limit policy`() {
        listOf(
            TestCase(page = 1L, pageSize = 30L, searchablePageCount = 10L, expected = 301L),
            TestCase(page = 7L, pageSize = 30L, searchablePageCount = 10L, expected = 301L),
            TestCase(page = 12L, pageSize = 30L, searchablePageCount = 10L, expected = 601L)
        ).forEach { testCase ->
            val actual = PageHelper.getSearchablePageCount(
                page = testCase.page,
                pageSize = testCase.pageSize,
                searchablePageCount = testCase.searchablePageCount
            )
            assertThat(actual).isEqualTo(testCase.expected)
        }
    }

    private data class TestCase(
        val page: Long,
        val pageSize: Long,
        val searchablePageCount: Long,
        val expected: Long
    )
}