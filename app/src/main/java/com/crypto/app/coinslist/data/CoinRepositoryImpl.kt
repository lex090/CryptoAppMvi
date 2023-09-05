package com.crypto.app.coinslist.data

import com.crypto.app.coinslist.domain.CoinId
import com.crypto.app.coinslist.domain.CoinInfo
import com.crypto.app.coinslist.domain.CoinRepository
import com.crypto.app.coinslist.domain.ShortCoin

class CoinRepositoryImpl(
    private val service: CoinsNetworkService
) : CoinRepository {
    override suspend fun getFullCoinInfo(coinId: CoinId): Result<CoinInfo> {
        return try {
            service.getCoinInfo(id = coinId.id)
                .let {
                    requireNotNull(it.currentPrice)

                    Result.success(
                        CoinInfo(
                            name = it.name ?: "Empty"
                        )
                    )
                }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadPage(page: Int, perPage: Int): Result<List<ShortCoin>> {
        return try {
            service.getCoinsMarketsList(perPage = perPage, page = page)
                .map {
                    requireNotNull(it.id)
                    requireNotNull(it.symbol)

                    ShortCoin(
                        id = CoinId(id = it.id),
                        symbol = it.symbol,
                        counter = 0
                    )
                }
                .let { Result.success(it) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}