package api

import com.scalable.article.dto.request.ArticleCreateRequest
import com.scalable.article.dto.request.ArticleUpdateRequest
import com.scalable.article.dto.response.ArticleResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClient

class ArticleControllerTest {

    private val restClient = RestClient.create("http://localhost:8080")

    @Test
    fun `should create an article successfully`() {
        // given
        val request = ArticleCreateRequest(
            title = "testTitle",
            content = "testContent",
            writerId = 1L,
            boardId = 1L
        )

        // when
        val response = create(request)

        // then
        println("Created Article: $response")
        assertArticleDetails(response, "testTitle", "testContent")
    }

    @Test
    fun `should read an article successfully`() {
        // given
        val articleId = create(
            ArticleCreateRequest(
                title = "updatedTitle",
                content = "updatedContent",
                writerId = 1L,
                boardId = 1L
            )
        ).articleId

        // when
        val response = read(articleId)

        // then
        assertEquals(articleId, response.articleId)
    }

    @Test
    fun `should update an article successfully`() {
        // given
        val articleId = create(
            ArticleCreateRequest(
                title = "updatedTitle",
                content = "updatedContent",
                writerId = 1L,
                boardId = 1L
            )
        ).articleId

        // when
        val response = update(
            articleId = articleId,
            request = ArticleUpdateRequest(
                title = "updatedTitle",
                content = "updatedContent"
            )
        )

        // then
        assertArticleDetails(response, "updatedTitle", "updatedContent")
    }

    @Test
    fun `should delete an article successfully`() {
        // given
        val articleId = create(
            ArticleCreateRequest(
                title = "updatedTitle",
                content = "updatedContent",
                writerId = 1L,
                boardId = 1L
            )
        ).articleId

        // when
        val response = delete(articleId)

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    private fun create(request: ArticleCreateRequest): ArticleResponse =
        restClient.post()
            .uri("/v1/articles")
            .body(request)
            .retrieve()
            .body(ArticleResponse::class.java)
            ?: throw IllegalStateException("Failed to create article")

    private fun read(articleId: Long): ArticleResponse =
        restClient.get()
            .uri("/v1/articles/{articleId}", articleId)
            .retrieve()
            .body(ArticleResponse::class.java)
            ?: throw IllegalStateException("Failed to read article")

    private fun update(articleId: Long, request: ArticleUpdateRequest): ArticleResponse =
        restClient.put()
            .uri("/v1/articles/{articleId}", articleId)
            .body(request)
            .retrieve()
            .body(ArticleResponse::class.java)
            ?: throw IllegalStateException("Failed to update article")

    private fun delete(articleId: Long): ResponseEntity<Void> =
        restClient.delete()
            .uri("/v1/articles/{articleId}", articleId)
            .retrieve()
            .toBodilessEntity()

    private fun assertArticleDetails(response: ArticleResponse, expectedTitle: String, expectedContent: String) {
        assertEquals(expectedTitle, response.title)
        assertEquals(expectedContent, response.content)
    }
}