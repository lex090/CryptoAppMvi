package com.crypto.app.coinslist.data

import com.crypto.app.coinslist.domain.CoinId
import com.crypto.app.coinslist.domain.CoinRepository
import com.crypto.app.coinslist.domain.ShortCoin

class CoinRepositoryImpl(
    private val service: CoinsNetworkService
) : CoinRepository {

    override suspend fun loadPage(page: Int, perPage: Int): Result<List<ShortCoin>> {
        return try {
            val data = service.getCoinsMarketsList(
                perPage = perPage,
                page = page
            ).map {
                requireNotNull(it.id)
                requireNotNull(it.symbol)

                ShortCoin(
                    id = CoinId(id = it.id),
                    symbol = it.symbol
                )
            }
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}