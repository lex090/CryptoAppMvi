package com.crypto.app.coinslist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.crypto.app.coinslist.domain.CoinRepository
import com.crypto.app.coinslist.domain.ShortCoin
import com.crypto.app.coinslist.updating.CoinsListUpdatingStore
import com.crypto.app.coinslist.updating.CoinsListUpdatingStoreFactory
import com.crypto.app.coinslist.updating.labelToIntent
import com.crypto.app.pagination.PaginationStore
import com.crypto.app.pagination.PaginationStoreFactory
import com.crypto.app.utils.asValue
import kotlinx.coroutines.flow.map

interface CoinsList {

    val state: Value<PaginationStore.State<ShortCoin>>

    fun onLoadPage()
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
            perPage = 80,
            startingPage = 1
        )
    }

    private val updatingStore = instanceKeeper.getStore {
        CoinsListUpdatingStoreFactory(storeFactory = storeFactory)
            .create(store.state)
    }

    override val state: Value<PaginationStore.State<ShortCoin>> = updatingStore.asValue()

    init {
        bind(lifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            store.labels.map(labelToIntent) bindTo updatingStore::accept
        }

        lifecycle.doOnStart {
            updatingStore.accept(CoinsListUpdatingStore.Intent.Subscribe)
        }

        lifecycle.doOnStop {
            updatingStore.accept(CoinsListUpdatingStore.Intent.Unsubscribe)
        }
    }

    override fun onLoadPage() {
        store.accept(PaginationStore.Intent.OnLoadPage)
    }
}