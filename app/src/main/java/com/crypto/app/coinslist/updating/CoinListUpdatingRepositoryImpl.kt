package com.crypto.app.coinslist.updating

import com.crypto.app.coinslist.domain.CoinId
import com.crypto.app.coinslist.domain.ShortCoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

class CoinListUpdatingRepositoryImpl(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) : CoinListUpdatingRepository {

    private val delayTime = 1000L
    private var counter: AtomicInteger = AtomicInteger(0)

    override fun subscribeOnCounterUpdating(
        items: List<ShortCoin>
    ): Flow<HashMap<CoinId, CoinListUpdatingData>> {
        return flow {
            val initialMap = items.associate { it.id to CoinListUpdatingData(it.counter) }
            while (true) {
                delay(delayTime)
                initialMap.mapValues {
                    CoinListUpdatingData(counter.get())
                }.also {
                    emit(HashMap(it))
                    counter.set(counter.get() + 1)
                }
            }
        }.flowOn(ioDispatcher)
    }
}