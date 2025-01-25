package repository

import com.scalable.article.ArticleApplication
import com.scalable.article.repository.ArticleRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [ArticleApplication::class]
)
class ArticleRepositoryTest {
    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Test
    fun findAll() {
        val articles = articleRepository.findAll(
            boardId = 1L,
            offset = 1598899L,
            limit = 30L
        )
        for (article in articles) {
            println(article)
        }
    }

    @Test
    fun countAll() {
        articleRepository.countAll(
            boardId = 1L,
            limit = 100000L
        ).also {
            println(it)
        }
    }
}