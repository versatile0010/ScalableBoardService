package com.scalable.article.repository

import com.scalable.article.entity.BoardArticleCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.repository.query.Param

interface BoardArticleCountRepository : JpaRepository<BoardArticleCount, Long> {

    @Query(
        value = """
            update board_artile_count set article_count = article_count + 1 where board_id = :boardId
        """,
        nativeQuery = true,
    )
    @Modifying
    fun increase(@Param("boardId") boardId: Long): Int

    @Query(
        value = """
            update board_artile_count set article_count = article_count - 1 where board_id = :boardId and article_count > 0
        """,
        nativeQuery = true,
    )
    @Modifying
    fun decrease(@Param("boardId") boardId: Long): Int
}

fun BoardArticleCountRepository.increaseIfNotExistsThenInit(boardId: Long): BoardArticleCount {
    val articleCount = findByIdOrNull(boardId)
    return articleCount?.increase() ?: save(BoardArticleCount.init(boardId = boardId, articleCount = 1))
}

fun BoardArticleCountRepository.decreaseIfNotExistsThenInit(boardId: Long): BoardArticleCount {
    val articleCount = findByIdOrNull(boardId)
    return articleCount?.decrease() ?: save(BoardArticleCount.init(boardId = boardId, articleCount = 0))
}