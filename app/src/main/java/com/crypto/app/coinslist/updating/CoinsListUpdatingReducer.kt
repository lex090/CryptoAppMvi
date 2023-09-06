package com.crypto.app.coinslist.updating

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Reducer
import com.crypto.app.coinslist.domain.CoinId
import com.crypto.app.coinslist.domain.ShortCoin
import com.crypto.app.coinslist.updating.CoinsListUpdatingStore.Message
import com.crypto.app.pagination.Page
import com.crypto.app.pagination.PaginationStore

class CoinsListUpdatingReducer :
    Reducer<PaginationStore.State<ShortCoin>, Message<ShortCoin, CoinId, CoinListUpdatingData>> {

    override fun PaginationStore.State<ShortCoin>.reduce(
        msg: Message<ShortCoin, CoinId, CoinListUpdatingData>
    ): PaginationStore.State<ShortCoin> {
        Log.i("myDebug", "reduce2: state $this")
        Log.i("myDebug", "reduce2: msg $msg")
        return when (msg) {
            is Message.UpdatedData -> {
                when (this) {
                    is PaginationStore.State.Loaded -> {
                        pages.map { page ->
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
                        }.let {
                            copy(pages = it)
                        }
                    }

                    else -> this
                }
            }

            is Message.NewPaginationState -> {
                when (msg.state) {
                    is PaginationStore.State.Loaded -> {
                        when (this) {
                            is PaginationStore.State.Loaded -> {
                                val coinsCounterMap = pages.flatMap {
                                    when (it) {
                                        is Page.Loaded -> it.items
                                        else -> emptyList()
                                    }
                                }.associate { it.id to it.counter }

                                msg.state.pages.map { page ->
                                    if (page is Page.Loaded) {
                                        page.copy(
                                            items = page.items.map { shortCoin ->
                                                coinsCounterMap[shortCoin.id]
                                                    ?.let { shortCoin.copy(counter = it) }
                                                    ?: shortCoin
                                            }
                                        )
                                    } else {
                                        page
                                    }
                                }.let {
                                    copy(pages = it)
                                }
                            }

                            else -> msg.state
                        }
                    }

                    else -> msg.state
                }
            }
        }
    }
}