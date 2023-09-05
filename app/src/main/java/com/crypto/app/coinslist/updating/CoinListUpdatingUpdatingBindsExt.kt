package com.crypto.app.coinslist.updating

import com.crypto.app.coinslist.domain.ShortCoin
import com.crypto.app.pagination.PaginationStore

val labelToIntent: (
    PaginationStore.Label<ShortCoin>,
) -> CoinsListUpdatingStore.Intent<ShortCoin> = { label ->
    when (label) {
        is PaginationStore.Label.PagesWillBeUpdating -> {
            CoinsListUpdatingStore.Intent.OnNewPaginationState(state = label.state)
        }
    }
}
