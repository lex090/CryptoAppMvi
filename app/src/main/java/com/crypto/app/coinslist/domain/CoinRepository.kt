package com.crypto.app.coinslist.domain

import com.crypto.app.pagination.PaginationRepository

interface CoinRepository : PaginationRepository<ShortCoin>