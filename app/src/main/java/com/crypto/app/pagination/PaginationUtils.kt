package com.crypto.app.pagination

fun <T : Any> List<Page<T>>.onlyLoaded(): List<Page.Loaded<T>> =
    this.filterIsInstance<Page.Loaded<T>>()

fun <T : Any> Int.placeholders(
    itemFactory: () -> T
): List<T> =
    buildList {
        repeat(this@placeholders) {
            add(itemFactory())
        }
    }