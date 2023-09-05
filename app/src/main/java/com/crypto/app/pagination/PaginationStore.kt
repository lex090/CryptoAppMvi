package com.crypto.app.pagination

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.crypto.app.pagination.PaginationStore.Intent
import com.crypto.app.pagination.PaginationStore.Label
import com.crypto.app.pagination.PaginationStore.State

interface PaginationStore<T : Any> : Store<Intent, State<T>, Label<T>> {

    sealed interface State<out T : Any> {

        data class Initialization(
            val perPage: Int,
            val startingPage: Int
        ) : State<Nothing>

        data class Loaded<T : Any>(
            val pages: List<Page<T>>,
            val perPage: Int,
            val currentPage: Int,
            val nextPage: Int?
        ) : State<T>

        data object Loading : State<Nothing>

        data object Empty : State<Nothing>

        data class Error(
            val message: String
        ) : State<Nothing>
    }

    sealed interface Message<out T : Any> {

        data class InitializationSuccess<T : Any>(
            val page: Page.Loaded<T>,
            val perPage: Int,
            val currentPage: Int
        ) : Message<T>

        data class InitializationError(
            val message: String
        ) : Message<Nothing>

        data class PageLoadSuccess<T : Any>(
            val page: Page.Loaded<T>
        ) : Message<T>

        data object PageLoadError : Message<Nothing>

        data class StartPageLoading(
            val loadingPage: Page.Loading
        ) : Message<Nothing>
    }

    sealed interface Intent {

        data object Refresh : Intent

        data object OnLoadPage : Intent
    }

    sealed interface Label<T : Any> {
        data class PagesWillBeUpdating<T : Any>(
            val state: State<T>
        ) : Label<T>
    }
}

class PaginationStoreFactory<T : Any>(
    private val paginationRepository: PaginationRepository<T>,
    private val storeFactory: StoreFactory
) {
    fun create(
        perPage: Int,
        startingPage: Int
    ): PaginationStore<T> = object : PaginationStore<T>,
        Store<Intent, State<T>, Label<T>> by storeFactory.create(
            name = "PaginationStore${hashCode()}",
            initialState = State.Initialization(
                perPage = perPage,
                startingPage = startingPage
            ),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = { PaginationExecutor(paginationRepository) },
            reducer = PaginationReducer()
        ) {}
}