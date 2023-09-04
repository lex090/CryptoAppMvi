package com.crypto.app.pagination

sealed interface Page<out T : Any> {

    sealed interface Loading : Page<Nothing> {

        data object Simple : Loading

        data class PageWithPlaceholders(
            val placeHolders: List<Unit>
        ) : Loading
    }

    data class Loaded<T : Any>(
        val items: List<T>
    ) : Page<T>

    data object Error : Page<Nothing>
}