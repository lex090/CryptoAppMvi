package com.crypto.app.coinslist

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.crypto.app.coinslist.CoinListStore.CoinListState
import com.crypto.app.coinslist.CoinListStore.Intent
import com.crypto.app.coinslist.CoinListStore.Label
import com.crypto.app.coinslist.CoinListStore.Message
import com.crypto.app.coinslist.domain.CoinRepository
import kotlinx.coroutines.launch

class CoinListExecutor(
    private val repository: CoinRepository,
) : CoroutineExecutor<Intent, Unit, CoinListState, Message, Label>() {

    override fun executeIntent(intent: Intent, getState: () -> CoinListState) {
        when (intent) {
            is Intent.OnCoinInfoLoad -> {
                scope.launch {
                    dispatch(Message.StartCoinInfoLoading(intent.coinId))

                    repository.getFullCoinInfo(intent.coinId)
                        .onSuccess {
                            dispatch(Message.LoadCoinInfoSuccess(intent.coinId, it))
                        }.onFailure {
                            dispatch(Message.LoadCoinInfoError(intent.coinId, it.message.orEmpty()))
                        }
                }
            }

            is Intent.StateWillBeUpdated -> {
                dispatch(Message.NewStateUpdated(intent.state))
            }
        }
    }
}