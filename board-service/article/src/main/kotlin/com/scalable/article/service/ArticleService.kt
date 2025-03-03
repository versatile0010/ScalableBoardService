package com.scalable.article.service

import com.common.snowflake.Snowflake
import com.scalable.article.dto.request.ArticleCreateRequest
import com.scalable.article.dto.request.ArticleUpdateRequest
import com.scalable.article.dto.response.ArticlePageResponse
import com.scalable.article.dto.response.ArticleResponse
import com.scalable.article.dto.response.BoardArticleCountResponse
import com.scalable.article.entity.Article
import com.scalable.article.global.PageHelper
import com.scalable.article.repository.ArticleRepository
import com.scalable.article.repository.BoardArticleCountRepository
import com.scalable.article.repository.decreaseIfNotExistsThenInit
import com.scalable.article.repository.findByIdOrThrow
import com.scalable.article.repository.increaseIfNotExistsThenInit
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val snowflake: Snowflake,
    private val articleRepository: ArticleRepository,
    private val boardArticleCountRepository: BoardArticleCountRepository,
) {

    @Transactional
    fun create(request: ArticleCreateRequest): ArticleResponse {
        val savedArticle = articleRepository.save(
            Article.of(
                snowflake.nextId(),
                request.title,
                request.content,
                request.boardId,
                request.writerId,
            )
        )
        boardArticleCountRepository.increaseIfNotExistsThenInit(request.boardId)
        return ArticleResponse.from(savedArticle)
    }

    @Transactional
    fun update(articleId: Long, request: ArticleUpdateRequest): ArticleResponse {
        val article = articleRepository.findByIdOrThrow(articleId)
            .update(title = request.title, content = request.content)
        return ArticleResponse.from(article)
    }

    fun read(articleId: Long): ArticleResponse {
        return ArticleResponse.from(
            article = articleRepository.findByIdOrThrow(articleId),
        )
    }

    @Transactional
    fun delete(articleId: Long) {
        val article = articleRepository.findByIdOrThrow(articleId)
        articleRepository.delete(article)
        boardArticleCountRepository.decreaseIfNotExistsThenInit(article.boardId)
    }

    fun readAll(
        boardId: Long,
        page: Long,
        pageSize: Long,
    ): ArticlePageResponse {

        val articles = articleRepository.findAll(
            boardId = boardId,
            offset = PageHelper.getOffset(
                page = page,
                pageSize = pageSize,
            ),
            limit = pageSize,
        )

        val articleCount = PageHelper.getSearchablePageCount(
            page = page,
            pageSize = pageSize,
            searchablePageCount = 10,
        )

        return ArticlePageResponse.of(
            articles = articles,
            articleCount = articleCount,
        )
    }

    fun readAll(
        boardId: Long,
        pageSize: Long,
        lastArticleId: Long?,
    ): ArticlePageResponse {
        val articles = lastArticleId?.let {
            return@let articleRepository.findAllByBoardIdAndLastArticleId(
                boardId = boardId,
                lastArticleId = it,
                limit = pageSize,
            )
        } ?: articleRepository.findAllByBoardId(
            boardId = boardId,
            limit = pageSize,
        )

        return ArticlePageResponse.from(articles)
    }

    fun count(boardId: Long): BoardArticleCountResponse {
        val boardArticleCount = boardArticleCountRepository.findByIdOrNull(boardId)
        return if (boardArticleCount == null) {
            BoardArticleCountResponse(0)
        } else {
            BoardArticleCountResponse.from(boardArticleCount)
        }
    }
}