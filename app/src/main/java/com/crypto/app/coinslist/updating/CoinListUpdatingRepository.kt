package com.crypto.app.coinslist.updating

import com.crypto.app.coinslist.domain.CoinId
import com.crypto.app.coinslist.domain.ShortCoin
import kotlinx.coroutines.flow.Flow

interface CoinListUpdatingRepository {

    fun subscribeOnCounterUpdating(
        items: List<ShortCoin>
    ): Flow<HashMap<CoinId, CoinListUpdatingData>>
}