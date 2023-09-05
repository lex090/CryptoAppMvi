package com.crypto.app.coinslist.updating

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.crypto.app.coinslist.domain.ShortCoin
import com.crypto.app.coinslist.updating.CoinsListUpdatingStore.Intent
import com.crypto.app.coinslist.updating.CoinsListUpdatingStore.Label
import com.crypto.app.pagination.PaginationStore

interface CoinsListUpdatingStore<T : Any> : Store<Intent<T>, PaginationStore.State<T>, Label> {

    sealed interface Message<out T : Any, out K : Any, out V : Any> {

        data class NewPaginationState<T : Any>(
            val state: PaginationStore.State<T>,
        ) : Message<T, Nothing, Nothing>

        data class UpdatedData<K : Any, V : Any>(
            val items: HashMap<K, V>
        ) : Message<Nothing, K, V>
    }


    sealed interface Intent<out T : Any> {
        data class OnNewPaginationState<T : Any>(
            val state: PaginationStore.State<T>
        ) : Intent<T>

        data object Subscribe : Intent<Nothing>
        data object Unsubscribe : Intent<Nothing>
    }

    sealed interface Label {
        data class FailedUpdate(val message: String) : Label
    }
}

class CoinsListUpdatingStoreFactory(
    private val storeFactory: StoreFactory
) {
    fun create(
        initialState: PaginationStore.State<ShortCoin>
    ): CoinsListUpdatingStore<ShortCoin> =
        object : CoinsListUpdatingStore<ShortCoin>,
            Store<Intent<ShortCoin>, PaginationStore.State<ShortCoin>, Label> by storeFactory.create(
                name = "CoinsListUpdatingStore${hashCode()}",
                initialState = initialState,
                bootstrapper = SimpleBootstrapper(Unit),
                executorFactory = {
                    CoinsListUpdatingExecutor(CoinListUpdatingRepositoryImpl())
                },
                reducer = CoinsListUpdatingReducer()
            ) {}
}