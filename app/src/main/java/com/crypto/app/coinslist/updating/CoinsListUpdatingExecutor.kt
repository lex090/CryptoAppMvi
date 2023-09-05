package com.crypto.app.coinslist.updating

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.crypto.app.coinslist.domain.CoinId
import com.crypto.app.coinslist.domain.ShortCoin
import com.crypto.app.coinslist.updating.CoinsListUpdatingStore.Intent
import com.crypto.app.coinslist.updating.CoinsListUpdatingStore.Label
import com.crypto.app.coinslist.updating.CoinsListUpdatingStore.Message
import com.crypto.app.pagination.PaginationStore
import com.crypto.app.pagination.onlyLoaded
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class CoinsListUpdatingExecutor :
    CoroutineExecutor<Intent<ShortCoin>, Unit, PaginationStore.State<ShortCoin>, Message<ShortCoin, CoinId, CoinListUpdatingData>, Label>() {

    private var counter = 0
    private var updating: Job? = null
    override fun executeIntent(
        intent: Intent<ShortCoin>,
        getState: () -> PaginationStore.State<ShortCoin>
    ) {
        when (intent) {
            is Intent.OnNewPaginationState -> {
                dispatch(
                    Message.NewPaginationState(
                        state = intent.state
                    )
                )
                updating?.cancel()
                updating = scope.launch {
                    when (val state = getState()) {
                        is PaginationStore.State.Loaded -> {
                            while (true) {
                                delay(1000)
                                HashMap(state
                                    .pages
                                    .onlyLoaded()
                                    .flatMap { it.items }
                                    .associate {
                                        it.id to CoinListUpdatingData(counter)
                                    }
                                ).also {
                                    dispatch(Message.UpdatedData(items = it))
                                    counter++
                                }
                                yield()
                            }
                        }

                        else -> TODO()
                    }
                }
            }

            Intent.Subscribe -> TODO()
            Intent.Unsubscribe -> TODO()
        }
    }
}