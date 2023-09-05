package com.crypto.app.coinslist

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Reducer
import com.crypto.app.coinslist.CoinListStore.CoinListState
import com.crypto.app.coinslist.CoinListStore.Message

class CoinListReducer : Reducer<CoinListState, Message> {

    override fun CoinListState.reduce(msg: Message): CoinListState {
        Log.i("myDebug2", "reduce3: state $this")
        Log.i("myDebug2", "reduce3: msg $msg")
        return when (msg) {
            is Message.StartCoinInfoLoading -> {
                val map = additionCoinInfo.toMutableMap()
                map[msg.coinId] = FullInfoCoin.Loading
                copy(additionCoinInfo = HashMap(map))
            }

            is Message.LoadCoinInfoError -> {
                val map = additionCoinInfo.toMutableMap()
                map[msg.coinId] = FullInfoCoin.Error(msg.message)
                copy(additionCoinInfo = HashMap(map))
            }
            is Message.LoadCoinInfoSuccess -> {
                val map = additionCoinInfo.toMutableMap()
                map[msg.coinId] = FullInfoCoin.Loaded(msg.coinInfo)
                copy(additionCoinInfo = HashMap(map))
            }

            is Message.NewStateUpdated -> {
                copy(state = msg.state)
            }
        }
    }
}

//        val state = when (msg) {
//            is Message.NewStateUpdated -> {
//                when (msg.state) {
//                    is State.Loaded -> {
//                        when (this.state) {
//                            is State.Loaded -> {
//                                msg.state.copy(
//                                    pages = msg.state.pages.mapIndexed { index, page ->
//                                        state.pages.getOrNull(index)
//                                            ?.let { it update page }
//                                            ?: page
//                                    }
//                                )
//                            }
//
//                            else -> msg.state
//                        }
//                    }
//
//                    else -> msg.state
//                }
//            }
//
//            is Message.LoadCoinInfoError -> TODO()
//            is Message.LoadCoinInfoSuccess -> TODO()
//        }


//private infix fun Page<ShortCoin>.update(newPage: Page<ShortCoin>): Page<ShortCoin> {
//    return when (newPage) {
//        is Page.Loaded -> {
//            newPage.copy(
//                items = newPage.items.map { shortCoin ->
//                    when (this) {
//                        is Page.Loaded -> {
//                            this.items
//                                .find { it.id == shortCoin.id }
//                                ?.let {
//                                    shortCoin.copy(
//                                        isLoading = it.isLoading,
//                                        coinInfo = it.coinInfo
//                                    )
//                                } ?: shortCoin
//                        }
//
//                        else -> shortCoin
//                    }
//                }
//            )
//        }
//
//        else -> newPage
//    }
//}