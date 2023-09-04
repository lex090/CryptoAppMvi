package com.crypto.app.utils

sealed interface UiModelText {
    data class PlainText(val text: String) : UiModelText
    data class ResText(val resId: Int) : UiModelText
}