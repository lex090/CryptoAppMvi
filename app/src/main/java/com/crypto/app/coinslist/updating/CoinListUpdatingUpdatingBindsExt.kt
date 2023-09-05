package com.crypto.app.coinslist.updating

import com.crypto.app.coinslist.CoinListStore
import com.crypto.app.coinslist.domain.ShortCoin
import com.crypto.app.pagination.PaginationStore

fun labelToIntent(
    label: PaginationStore.Label<ShortCoin>,
): CoinsListUpdatingStore.Intent<ShortCoin> =
    when (label) {
        is PaginationStore.Label.PagesWillBeUpdating -> {
            CoinsListUpdatingStore.Intent.OnNewPaginationState(state = label.state)
        }
    }

fun labelToIntent(
    label: CoinsListUpdatingStore.Label
): CoinListStore.Intent =
    when (label) {
        is CoinsListUpdatingStore.Label.StateWillBeUpdated -> {
            CoinListStore.Intent.StateWillBeUpdated(label.state)
        }
    }
