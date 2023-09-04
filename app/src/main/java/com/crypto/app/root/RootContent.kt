package com.crypto.app.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.crypto.app.coinslist.CoinsListContent

@Composable
fun RootContent(
    rootFlowComponent: RootFlowComponent,
    modifier: Modifier = Modifier
) {
    Children(
        stack = rootFlowComponent.childStack,
        modifier = modifier
    ) {
        when (val child = it.instance) {
            is RootFlowComponent.Child.CoinsListScreen ->
                CoinsListContent(component = child.component)
        }
    }
}