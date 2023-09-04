package com.crypto.app.coinslist.updating

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Reducer
import com.crypto.app.coinslist.domain.CoinId
import com.crypto.app.coinslist.domain.ShortCoin
import com.crypto.app.coinslist.updating.CoinsListUpdatingStore.Message
import com.crypto.app.coinslist.updating.CoinsListUpdatingStore.State
import com.crypto.app.pagination.Page
import com.crypto.app.pagination.PaginationStore

class CoinsListUpdatingReducer :
    Reducer<State<ShortCoin>, Message<ShortCoin, CoinId, CoinListUpdatingData>> {

    override fun State<ShortCoin>.reduce(msg: Message<ShortCoin, CoinId, CoinListUpdatingData>): State<ShortCoin> {
        Log.i("myDebug", "reduce2: state $this")
        Log.i("myDebug", "reduce2: msg $msg")
        return when (msg) {
            is Message.UpdatedData -> {
                if (this is State.PaginationState) {
                    when (this.state) {
                        is PaginationStore.State.Loaded -> {
                            val data = state.pages.map { page ->
                                if (page is Page.Loaded) {
                                    page.copy(
                                        items = page.items.map { shortCoin ->
                                            msg.items[shortCoin.id]
                                                ?.counter
                                                ?.let { shortCoin.copy(counter = it) }
                                                ?: shortCoin
                                        }
                                    )
                                } else {
                                    page
                                }
                            }
                            copy(
                                state = state.copy(
                                    pages = data
                                )
                            )
                        }

                        else -> this
                    }
                } else {
                    this
                }
            }

            is Message.NewPaginationState -> {
                State.PaginationState(
                    state = msg.state
                )
            }
        }
    }
}