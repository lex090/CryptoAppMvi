package com.crypto.app.coinslist.domain

import com.crypto.app.pagination.PaginationRepository

interface CoinRepository : PaginationRepository<ShortCoin> {

    suspend fun getFullCoinInfo(id: CoinId): Result<CoinInfo>
}