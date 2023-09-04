package com.crypto.app.coinslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.crypto.app.pagination.Page
import com.crypto.app.pagination.PaginationStore
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun CoinsListContent(
    component: CoinsList,
    modifier: Modifier = Modifier
) {
    val state = component.state.subscribeAsState()

    when (val valueState = state.value) {
        is PaginationStore.State.Empty -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "EmptyList")
            }
        }

        is PaginationStore.State.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: ${valueState.message}")
            }
        }

        is PaginationStore.State.Loaded -> {

            val lazyColumnState = rememberLazyListState()

            LaunchedEffect(lazyColumnState) {
                snapshotFlow {
                    (lazyColumnState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                        ?: 0) >= lazyColumnState.layoutInfo.totalItemsCount - 4
                }.distinctUntilChanged()
                    .collect {
                        if (it) {
                            component.onNeedLoadForwardItems()
                        }
                    }
            }

            Box(modifier = modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyColumnState
                ) {
                    valueState.pages.forEachIndexed { parentIndex, page ->
                        when (page) {
                            is Page.Error -> {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(64.dp)
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Error while loading")
                                    }
                                }
                            }

                            is Page.Loaded -> {
                                itemsIndexed(page.items) { index, item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(64.dp)
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = item.symbol)
                                        Text(text = (parentIndex + index).toString())
                                    }
                                }
                            }

                            is Page.Loading.Simple -> {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(64.dp)
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = parentIndex.toString())
                                    }
                                }
                            }

                            is Page.Loading.PageWithPlaceholders -> {
                                itemsIndexed(page.placeholders) { index, _ ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(64.dp)
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = (index + parentIndex).toString())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        is PaginationStore.State.Initialization, is PaginationStore.State.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
