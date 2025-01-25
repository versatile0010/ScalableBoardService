package datainit

import com.common.snowflake.Snowflake
import com.scalable.article.ArticleApplication
import com.scalable.article.entity.Article
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest(
    classes = [ArticleApplication::class],
)
class DataInitializer {
    companion object {
        // INSERT EXECUTE_COUNT * CHUNK_SIZE records
        private const val EXECUTE_COUNT = 8000;
        private const val CHUNK_SIZE = 1000;
    }

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    lateinit var snowflake: Snowflake

    private val latch = CountDownLatch(EXECUTE_COUNT)

    @Test
    fun init() {
        Executors.newFixedThreadPool(10).use { executor ->
            repeat(EXECUTE_COUNT) { count ->
                executor.submit {
                    try {
                        insert()
                        latch.countDown()
                        println("Latch count: ${latch.count}, Task $count completed")
                    } catch (e: Exception) {
                        println("Error in task $count: ${e.message}")
                    }
                }
            }
            latch.await()
        }
        println("All tasks completed.")
    }

    private fun insert() {
        transactionTemplate.executeWithoutResult {
            repeat(CHUNK_SIZE) { index ->
                val article = Article.of(
                    articleId = snowflake.nextId(),
                    title = "title:$index",
                    content = "content:$index",
                    boardId = 1L,
                    writerId = 1L,
                )
                entityManager.persist(article)
            }
        }
    }
}