package service

import com.common.snowflake.Snowflake
import com.scalable.comment.CommentApplication
import com.scalable.comment.entity.Comment
import com.scalable.comment.repository.CommentRepository
import com.scalable.comment.service.CommentService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(
    classes = [CommentApplication::class],
)
class CommentServiceIntegrationTest {

    @Autowired
    lateinit var commentService: CommentService

    @Autowired
    lateinit var commentRepository: CommentRepository

    @Autowired
    lateinit var snowflake: Snowflake

    @BeforeEach
    fun clear() {
        commentRepository.deleteAll()
        // 필요 시 초기 데이터 넣기
    }

    @Test
    fun `삭제할 댓글이 자식이 있다면, soft delete 한다`() {
        // given
        val parentId = 100L

        val parent = commentRepository.save(
            createComment(
                commentId = parentId,
                parentId = parentId,
                content = "부모댓글",
                isDeleted = false,
            )
        )
        val child = commentRepository.save(
            createComment(
                commentId = 101L,
                parentId = parentId,
                content = "자식댓글",
                isDeleted = false,
            )
        )

        // when
        commentService.delete(parentId)

        // then
        val foundParent = commentRepository.findById(parent.commentId).get()
        val foundChild = commentRepository.findById(child.commentId).get()

        assertTrue(foundParent.isDeleted, "부모 댓글은 soft-delete 되어야 함")
        assertFalse(foundChild.isDeleted, "자식 댓글은 그대로 존재해야 함")
    }

    @Test
    fun `하위 댓글이 삭제되고, 삭제되지 않은 부모면 하위 댓글만 삭제한다`() {
        // given
        val parentId = 200L
        val childId = 201L
        val parent = commentRepository.save(
            createComment(
                commentId = parentId,
                parentId = parentId,
                content = "부모댓글",
                isDeleted = false,
            )
        )
        val child = commentRepository.save(
            createComment(
                commentId = childId,
                parentId = parentId,
                content = "자식댓글",
                isDeleted = false,
            )
        )

        // when
        commentService.delete(childId)

        // then
        val deletedChild = commentRepository.findById(childId)
        assertFalse(deletedChild.isPresent, "자식 댓글은 DB에서 완전히 삭제되어야 함")

        val foundParent = commentRepository.findById(parentId).get()
        assertFalse(foundParent.isDeleted, "부모 댓글은 여전히 deleted = false 이어야 함")
    }

    @Test
    fun `하위 댓글이 삭제되고, 이미 soft delete된 부모면 재귀적으로 모두 삭제한다`() {
        // given
        val parentId = 300L
        val childId = 301L
        val parent = commentRepository.save(
            createComment(
                commentId = parentId,
                parentId = parentId,
                content = "부모댓글(softDeleted)",
                isDeleted = true,
            )
        )
        commentRepository.save(
            createComment(
                commentId = childId,
                parentId = parentId,
                content = "자식댓글",
                isDeleted = false,
            )
        )

        // when
        commentService.delete(childId)

        // then
        val deletedChild = commentRepository.findById(childId)
        assertFalse(deletedChild.isPresent, "자식 댓글은 물리적으로 삭제되어야 함")

        val deletedParent = commentRepository.findById(parentId)
        assertFalse(deletedParent.isPresent, "부모 댓글도 재귀적으로 물리삭제되어야 함")
    }

    private fun createComment(
        commentId: Long,
        parentId: Long?,
        content: String,
        isDeleted: Boolean,
    ): Comment {
        return Comment(
            commentId = commentId,
            content = content,
            parentCommentId = parentId ?: commentId,
            articleId = 999L,
            writerId = 11L,
            isDeleted = isDeleted,
        )
    }
}