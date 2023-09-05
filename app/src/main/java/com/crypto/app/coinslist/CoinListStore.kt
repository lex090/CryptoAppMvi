package com.crypto.app.coinslist

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.crypto.app.coinslist.CoinListStore.CoinListState
import com.crypto.app.coinslist.CoinListStore.Intent
import com.crypto.app.coinslist.CoinListStore.Label
import com.crypto.app.coinslist.domain.CoinId
import com.crypto.app.coinslist.domain.CoinInfo
import com.crypto.app.coinslist.domain.CoinRepository
import com.crypto.app.coinslist.domain.ShortCoin
import com.crypto.app.pagination.PaginationStore.State

sealed interface FullInfoCoin {
    data object Loading : FullInfoCoin
    data class Error(val msg: String) : FullInfoCoin
    data class Loaded(
        val data: CoinInfo
    ) : FullInfoCoin
}

interface CoinListStore : Store<Intent, CoinListState, Label> {

    data class CoinListState(
        val state: State<ShortCoin>,
        val additionCoinInfo: HashMap<CoinId, FullInfoCoin>
    )

    sealed interface Message {

        data class NewStateUpdated(
            val state: State<ShortCoin>
        ) : Message

        data class StartCoinInfoLoading(
            val coinId: CoinId
        ) : Message

        data class LoadCoinInfoSuccess(
            val coinId: CoinId,
            val coinInfo: CoinInfo
        ) : Message

        data class LoadCoinInfoError(
            val coinId: CoinId,
            val message: String
        ) : Message
    }

    sealed interface Intent {

        data class StateWillBeUpdated(
            val state: State<ShortCoin>
        ) : Intent

        data class OnCoinInfoLoad(
            val coinId: CoinId
        ) : Intent
    }

    sealed interface Label
}

class CoinListStoreFactory(
    private val repository: CoinRepository,
    private val storeFactory: StoreFactory
) {
    fun create(initialState: State<ShortCoin>): CoinListStore =
        object : CoinListStore, Store<Intent, CoinListState, Label> by storeFactory
            .create(
                name = "CoinListStore",
                initialState = CoinListState(
                    state = initialState,
                    additionCoinInfo = hashMapOf()
                ),
                bootstrapper = SimpleBootstrapper(Unit),
                executorFactory = { CoinListExecutor(repository) },
                reducer = CoinListReducer()
            ) {}
}
