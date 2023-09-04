package com.crypto.app.coinslist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.crypto.app.coinslist.domain.CoinRepository
import com.crypto.app.coinslist.domain.ShortCoin
import com.crypto.app.pagination.PaginationStore
import com.crypto.app.pagination.PaginationStoreFactory
import com.crypto.app.utils.asValue

interface CoinsList {

    val state: Value<PaginationStore.State<ShortCoin>>

    fun onNeedLoadForwardItems()
}

class RealCoinsList(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    repository: CoinRepository
) : CoinsList, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        PaginationStoreFactory(
            paginationRepository = repository,
            storeFactory = storeFactory,
        ).create(
            perPage = 40,
            startingPage = 1
        )
    }

    override val state: Value<PaginationStore.State<ShortCoin>> = store.asValue()

    override fun onNeedLoadForwardItems() {
        store.accept(PaginationStore.Intent.OnPageLoad)
    }
}