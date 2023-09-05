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
import kotlinx.coroutines.launch

class CoinsListUpdatingExecutor(
    private val repository: CoinListUpdatingRepository
) : CoroutineExecutor<Intent<ShortCoin>, Unit, PaginationStore.State<ShortCoin>, Message<ShortCoin, CoinId, CoinListUpdatingData>, Label>() {

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
                publish(Label.StateWillBeUpdated(getState()))

                updating?.cancel()
                updating = scope.launch {
                    subscribeOnUpdating(
                        intent.state,
                        getState
                    )
                }
            }

            Intent.Subscribe -> {
                if (updating?.isActive != true) {
                    updating = scope.launch {
                        subscribeOnUpdating(
                            getState(),
                            getState
                        )
                    }
                }
            }

            Intent.Unsubscribe -> {
                updating?.cancel()
            }
        }
    }

    private suspend fun subscribeOnUpdating(
        state: PaginationStore.State<ShortCoin>,
        getState: () -> PaginationStore.State<ShortCoin>
    ) {
        when (state) {
            is PaginationStore.State.Loaded -> {
                state.pages
                    .onlyLoaded()
                    .flatMap { it.items }
                    .also { coinsList ->
                        repository.subscribeOnCounterUpdating(coinsList)
                            .collect {
                                dispatch(Message.UpdatedData(items = it))
                                publish(Label.StateWillBeUpdated(getState()))
                            }
                    }
            }

            else -> {}
        }
    }
}