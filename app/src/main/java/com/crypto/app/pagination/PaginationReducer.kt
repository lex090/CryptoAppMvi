package com.crypto.app.pagination

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Reducer
import com.crypto.app.pagination.PaginationStore.Message
import com.crypto.app.pagination.PaginationStore.State

class PaginationReducer<T : Any> : Reducer<State<T>, Message<T>> {
    override fun State<T>.reduce(msg: Message<T>): State<T> {
        Log.i("myDebug", "reduce: state $this")
        Log.i("myDebug", "reduce: msg $msg")
        return when (msg) {
            is Message.InitializationError -> {
                State.Error(message = msg.message)
            }

            is Message.InitializationSuccess -> {
                State.Loaded(
                    pages = listOf(msg.page),
                    perPage = msg.perPage,
                    currentPage = msg.currentPage,
                    nextPage = msg.currentPage + 1
                )
            }

            is Message.PageLoadSuccess -> {
                when (this) {
                    is State.Loaded -> {
                        if (msg.page.items.isEmpty()) {
                            copy(nextPage = null)
                        } else {
                            copy(
                                pages = pages.onlyLoaded() + msg.page,
                                currentPage = nextPage!!,
                                nextPage = nextPage + 1
                            )
                        }
                    }

                    else -> this
                }
            }

            is Message.PageLoadError -> {
                when (this) {
                    is State.Loaded -> copy(
                        pages = pages.onlyLoaded() + Page.Error
                    )

                    else -> this
                }
            }

            is Message.StartPageLoading -> {
                when (this) {
                    is State.Loaded -> {
                        copy(pages = pages.onlyLoaded() + msg.loadingPage)
                    }

                    else -> this
                }
            }
        }
    }
}