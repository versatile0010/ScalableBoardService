package repository

import com.scalable.view.ViewApplication
import com.scalable.view.entity.ArticleViewCount
import com.scalable.view.repository.ArticleViewCountBackUpRepository
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(
    classes = [ViewApplication::class],
)
class ArticleViewCountBackUpRepositoryTest {

    @Autowired
    lateinit var articleViewCountBackUpRepository: ArticleViewCountBackUpRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @Test
    @Transactional
    fun updateViewCountTest() {
        // given
        articleViewCountBackUpRepository.save(
            ArticleViewCount.init(
                articleId = 1L,
                viewCount = 1L
            )
        )
        entityManager.flush()
        entityManager.clear()

        // when
        articleViewCountBackUpRepository.updateViewCount(
            articleId = 1L,
            viewCount = 100L
        )
        articleViewCountBackUpRepository.updateViewCount(
            articleId = 1L,
            viewCount = 200L
        )
        articleViewCountBackUpRepository.updateViewCount(
            articleId = 1L,
            viewCount = 300L
        )

        // then
        val result = articleViewCountBackUpRepository.findById(1L).get()
        assertThat(result.viewCount).isEqualTo(300L)
    }
}