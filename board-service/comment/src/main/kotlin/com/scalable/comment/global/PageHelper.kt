package com.scalable.comment.global

object PageHelper {
    fun getSearchablePageCount(
        page: Long,
        pageSize: Long,
        searchablePageCount: Long,
    ): Long {
        return (((page - 1) / searchablePageCount) + 1) * pageSize * searchablePageCount + 1;
    }

    fun getOffset(
        page: Long,
        pageSize: Long,
    ): Long {
        return (page - 1) * pageSize
    }
}