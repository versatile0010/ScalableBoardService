package api

import com.scalable.article.ArticleApplication
import com.scalable.article.dto.request.ArticleCreateRequest
import com.scalable.article.dto.request.ArticleUpdateRequest
import com.scalable.article.dto.response.ArticlePageResponse
import com.scalable.article.dto.response.ArticleResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClient

@SpringBootTest(
    classes = [ArticleApplication::class],
    webEnvironment = WebEnvironment.RANDOM_PORT,
)
class ArticleApiSimpleTest {

    @LocalServerPort
    private var port: Int = 0

    private lateinit var client: RestClient

    @BeforeEach
    fun init() {
        client = RestClient.create("http://localhost:$port")
    }

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
        assertArticleDetails(
            response = response,
            expectedTitle = "testTitle",
            expectedContent = "testContent",
        )
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
                content = "updatedContent",
            )
        )

        // then
        assertArticleDetails(
            response = response,
            expectedTitle = "updatedTitle",
            expectedContent = "updatedContent",
        )
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

    @Test
    fun `should read all articles successfully`() {
        // when
        val response = readAll(
            boardId = 1L,
            page = 599999L,
            pageSize = 30L
        )

        // then
        for(article in response.articles) {
            println("Article: $article")
        }

    }

    private fun create(request: ArticleCreateRequest): ArticleResponse =
        client.post()
            .uri("/v1/articles")
            .body(request)
            .retrieve()
            .body(ArticleResponse::class.java)
            ?: throw IllegalStateException("Failed to create article")

    private fun read(articleId: Long): ArticleResponse =
        client.get()
            .uri("/v1/articles/{articleId}", articleId)
            .retrieve()
            .body(ArticleResponse::class.java)
            ?: throw IllegalStateException("Failed to read article")

    private fun readAll(
        boardId: Long,
        page: Long,
        pageSize: Long,
    ): ArticlePageResponse {
        return client.get()
            .uri("/v1/articles?boardId=$boardId&page=$page&pageSize=$pageSize")
            .retrieve()
            .body(ArticlePageResponse::class.java)
            ?: throw IllegalStateException("Failed to read articles")
    }

    private fun update(articleId: Long, request: ArticleUpdateRequest): ArticleResponse =
        client.put()
            .uri("/v1/articles/{articleId}", articleId)
            .body(request)
            .retrieve()
            .body(ArticleResponse::class.java)
            ?: throw IllegalStateException("Failed to update article")

    private fun delete(articleId: Long): ResponseEntity<Void> =
        client.delete()
            .uri("/v1/articles/{articleId}", articleId)
            .retrieve()
            .toBodilessEntity()

    private fun assertArticleDetails(response: ArticleResponse, expectedTitle: String, expectedContent: String) {
        assertEquals(expectedTitle, response.title)
        assertEquals(expectedContent, response.content)
    }
}