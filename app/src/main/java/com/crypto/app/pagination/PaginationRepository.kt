package com.crypto.app.pagination


interface PaginationRepository<T : Any> {

    suspend fun loadPage(
        page: Int,
        perPage: Int
    ): Result<List<T>>
}