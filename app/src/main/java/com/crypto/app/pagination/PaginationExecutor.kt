package com.crypto.app.pagination

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.crypto.app.pagination.PaginationStore.Intent
import com.crypto.app.pagination.PaginationStore.Label
import com.crypto.app.pagination.PaginationStore.Message
import com.crypto.app.pagination.PaginationStore.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PaginationExecutor<T : Any>(
    private val paginationRepository: PaginationRepository<T>
) : CoroutineExecutor<Intent, Unit, State<T>, Message<T>, Label<T>>() {

    override fun executeAction(action: Unit, getState: () -> State<T>) {
        val state = getState()
        scope.launch {
            initializeStore(state, getState)
        }
    }

    private suspend fun initializeStore(
        initializationState: State<T>,
        getState: () -> State<T>
    ) {
        with(initializationState) {
            if (this !is State.Initialization) {
                throw IllegalStateException(
                    "Стартовое состояние PaginationStore != State.Initialization"
                )
            }

            paginationRepository.loadPage(startingPage, perPage)
                .onSuccess {
                    Message.InitializationSuccess(
                        page = Page.Loaded(items = it),
                        perPage = perPage,
                        currentPage = startingPage
                    ).also { msg ->
                        dispatch(msg)
                        publish(Label.PagesWillBeUpdating(getState()))
                    }
                }.onFailure {
                    dispatch(
                        Message.InitializationError(
                            message = it.message.orEmpty()
                        )
                    )
                    publish(Label.PagesWillBeUpdating(getState()))
                }
        }
    }

    private var loadPage: Job? = null
    override fun executeIntent(
        intent: Intent,
        getState: () -> State<T>
    ) {
        when (intent) {
            Intent.OnLoadPage -> {
                when (val state = getState()) {
                    is State.Loaded -> {
                        if (loadPage?.isActive != true) {
                            loadPage = scope.launch {
                                loadPage(
                                    nextPage = state.nextPage,
                                    perPage = state.perPage,
                                    getState = getState
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }

            Intent.Refresh -> TODO()
        }
    }

    private suspend fun loadPage(
        nextPage: Int?,
        perPage: Int,
        getState: () -> State<T>
    ) {
        if (nextPage != null) {
            dispatch(
                Message.StartPageLoading(
                    loadingPage = Page.Loading.PageWithPlaceholders(
                        placeholders = perPage.placeholders { Unit }
                    )
                )
            )
            publish(Label.PagesWillBeUpdating(getState()))

            paginationRepository.loadPage(nextPage, perPage)
                .onSuccess {
                    Message.PageLoadSuccess(page = Page.Loaded(items = it))
                        .also { msg ->
                            dispatch(msg)
                            publish(Label.PagesWillBeUpdating(getState()))
                        }
                }.onFailure {
                    dispatch(Message.PageLoadError)
                    publish(Label.PagesWillBeUpdating(getState()))
                }
        }
    }
}