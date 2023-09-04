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
        initializeStore(getState)
    }

    private var loadPage: Job? = null
    override fun executeIntent(
        intent: Intent,
        getState: () -> State<T>
    ) {
        when (intent) {
            Intent.OnPageLoad -> {
                when (val state = getState()) {
                    is State.Loaded -> {
                        if (loadPage?.isActive != true) {
                            val nextPage = state.nextPage
                            if (nextPage != null) {
                                dispatch(
                                    Message.StartPageLoading(
                                        loadingPage = Page.Loading.PageWithPlaceholders(
                                            placeHolders = state.perPage.placeholders { Unit }
                                        )
                                    )
                                )
                                loadPage = scope.launch {
                                    val page = paginationRepository.loadPage(
                                        nextPage,
                                        state.perPage
                                    )
                                    page
                                        .onSuccess {
                                            val message = Message.PageLoadSuccess(
                                                page = Page.Loaded(items = it),
                                            )
                                            dispatch(message)
                                        }
                                        .onFailure {
                                            dispatch(Message.PageLoadError)
                                        }
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }

            Intent.Refresh -> TODO()
        }
    }

    private fun initializeStore(getState: () -> State<T>) {
        scope.launch {
            val initializationState = getState()

            if (initializationState !is State.Initialization) {
                throw IllegalStateException(
                    "Стартовое состояние PaginationStore != State.Initialization"
                )
            }

            paginationRepository.loadPage(
                initializationState.startingPage,
                initializationState.perPage
            )
                .onSuccess {
                    val message = Message.InitializationSuccess(
                        page = Page.Loaded(items = it),
                        perPage = initializationState.perPage,
                        currentPage = initializationState.startingPage
                    )
                    dispatch(message)
                }.onFailure {
                    dispatch(
                        Message.InitializationError(
                            message = it.message.orEmpty()
                        )
                    )
                }
        }
    }
}