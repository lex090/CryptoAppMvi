package com.crypto.app.coinslist.domain

data class ShortCoin(
    val id: CoinId,
    val symbol: String,
)

@JvmInline
value class CoinId(
    val id: String
)

