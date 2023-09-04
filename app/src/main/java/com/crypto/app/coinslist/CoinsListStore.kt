//package com.crypto.app.coinslist
//
//import android.util.Log
//import com.arkivanov.mvikotlin.core.store.Reducer
//import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
//import com.arkivanov.mvikotlin.core.store.Store
//import com.arkivanov.mvikotlin.core.store.StoreFactory
//import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
//import com.crypto.app.coinslist.CoinsListStore.ExecutorImpl
//import com.crypto.app.coinslist.CoinsListStore.Intent
//import com.crypto.app.coinslist.CoinsListStore.Label
//import com.crypto.app.coinslist.CoinsListStore.ReducerImpl
//import com.crypto.app.coinslist.CoinsListStore.State
//import com.crypto.app.coinslist.domain.CoinId
//import com.crypto.app.coinslist.domain.CoinRepository
//import com.crypto.app.coinslist.domain.ShortCoin
//import kotlinx.coroutines.launch
//
//interface CoinsListStore : Store<Intent, State, Label> {
//
//    sealed interface State {
//        data class Loaded(
//            val coins: List<ShortCoin>,
//            val inProgressFullInfo: Set<CoinId>,
//            val checkedCoins: Set<CoinId>,
//            val availablePagesInMemory: Int,
//            val itemsPerPage: Int,
//            val currentPage: Int,
//            val nextPage: Int?,
//            val nextPageLoading: Boolean,
//            val nextPageLoadingError: Boolean,
//            val nextPageLoadingErrorMessage: String,
//            val prevPage: Int?,
//            val prevPageLoading: Boolean,
//            val prevPageLoadingError: Boolean,
//            val prevPageLoadingErrorMessage: String,
//        ) : State
//
//        data object Loading : State
//        data object EmptyList : State
//        data class Error(val message: String) : State
//    }
//
//    sealed interface Message {
//        data class FirstDataLoaded(
//            val list: List<ShortCoin>
//        ) : Message
//
//        data class FirstDataLoadingError(
//            val throwable: Throwable
//        ) : Message
//
//
//        data object ForwardLoading : Message
//
//        data class ForwardDataLoaded(
//            val list: List<ShortCoin>
//        ) : Message
//
//        data class ForwardDataLoadingError(
//            val throwable: Throwable
//        ) : Message
//
//        data class BackwardDataLoaded(
//            val list: List<ShortCoin>
//        ) : Message
//    }
//
//    class ReducerImpl : Reducer<State, Message> {
//        override fun State.reduce(msg: Message): State {
//            return when (msg) {
//                is Message.FirstDataLoaded -> State.Loaded(
//                    coins = msg.list,
//                    inProgressFullInfo = setOf(),
//                    checkedCoins = setOf(),
//                    availablePagesInMemory = 200,
//                    itemsPerPage = 100,
//                    currentPage = 1,
//                    nextPage = 2,
//                    nextPageLoading = false,
//                    nextPageLoadingError = false,
//                    nextPageLoadingErrorMessage = "",
//                    prevPage = null,
//                    prevPageLoading = false,
//                    prevPageLoadingError = false,
//                    prevPageLoadingErrorMessage = ""
//                )
//
//                is Message.ForwardDataLoaded -> {
//                    when (this) {
//                        State.EmptyList -> this
//                        is State.Error -> this
//                        is State.Loaded -> {
//                            if (msg.list.isNotEmpty()) {
//                                var newCoins = this.coins + msg.list
//                                Log.i("myDebug", "reduce: ${newCoins.size}")
//                                Log.i("myDebug", "reduce: ${newCoins}")
//                                if(newCoins.size > availablePagesInMemory) {
//                                    newCoins = newCoins.subList(itemsPerPage, newCoins.size)
//                                }
//                                Log.i("myDebug", "reduce: ${newCoins.size}")
//                                Log.i("myDebug", "reduce: ${newCoins}")
//
//                                copy(
//                                    coins = newCoins,
//                                    currentPage = nextPage!!,
//                                    nextPage = nextPage + 1,
//                                    nextPageLoading = false,
//                                    nextPageLoadingError = false
//                                )
//                            } else {
//                                copy(
//                                    nextPage = null,
//                                    nextPageLoading = false,
//                                    nextPageLoadingError = false
//                                )
//                            }
//                        }
//
//                        State.Loading -> this
//                    }
//                }
//
//                Message.ForwardLoading -> {
//                    when (this) {
//                        State.EmptyList -> this
//                        is State.Error -> this
//                        is State.Loaded -> {
//                            copy(
//                                nextPageLoading = true,
//                                nextPageLoadingError = false
//                            )
//                        }
//
//                        State.Loading -> this
//                    }
//                }
//
//                is Message.FirstDataLoadingError -> {
//                    when (this) {
//                        State.EmptyList -> this
//                        is State.Error -> this
//                        is State.Loaded -> this
//                        State.Loading -> State.Error(message = msg.throwable.message.orEmpty())
//                    }
//                }
//
//                is Message.ForwardDataLoadingError -> {
//                    when (this) {
//                        State.EmptyList -> this
//                        is State.Error -> this
//                        is State.Loaded -> {
//                            copy(
//                                nextPageLoading = false,
//                                nextPageLoadingError = true,
//                                nextPageLoadingErrorMessage = msg.throwable.message.orEmpty()
//                            )
//                        }
//
//                        State.Loading -> this
//                    }
//                }
//
//                is Message.BackwardDataLoaded -> {
//                    when (this) {
//                        State.EmptyList -> this
//                        is State.Error -> this
//                        is State.Loaded -> {
//                            if (msg.list.isNotEmpty()) {
//                                copy(
//                                    coins = msg.list + this.coins,
//                                    currentPage = prevPage!!,
//                                    prevPage = prevPage - 1,
//                                    prevPageLoading = false,
//                                    prevPageLoadingError = false
//                                )
//                            } else {
//                                copy(
//                                    prevPage = null,
//                                    prevPageLoading = false,
//                                    prevPageLoadingError = false
//                                )
//                            }
//                        }
//
//                        State.Loading -> this
//                    }
//                }
//            }
//        }
//    }
//
//    sealed interface Intent {
//        data object OnForwardLoadMoreItems : Intent
//        data object OnBackwardLoadMoreItems : Intent
//    }
//
//    sealed interface Label
//
//    class ExecutorImpl(
//        private val repository: CoinRepository
//    ) : CoroutineExecutor<Intent, Unit, State, Message, Label>() {
//
//        override fun executeAction(action: Unit, getState: () -> State) {
//            scope.launch {
//                val data = repository.getShortCoinsList(
//                    perPage = 100,
//                    page = 1
//                )
//                data.onSuccess {
//                    dispatch(Message.FirstDataLoaded(it))
//                }
//                data.onFailure {
//                    dispatch(Message.FirstDataLoadingError(it))
//                }
//            }
//        }
//
//        override fun executeIntent(intent: Intent, getState: () -> State) {
//            when (intent) {
//                Intent.OnForwardLoadMoreItems -> {
//                    val state = getState()
//
//                    if (state is State.Loaded) {
//                        scope.launch {
//                            val nextPage = state.nextPage
//                            if (nextPage != null) {
//                                dispatch(Message.ForwardLoading)
//                                val data = repository.loadPage(
//                                    perPage = state.itemsPerPage,
//                                    page = nextPage
//                                )
//                                data.onSuccess {
//                                    dispatch(Message.ForwardDataLoaded(it))
//                                }
//                                data.onFailure {
//                                    dispatch(Message.ForwardDataLoadingError(it))
//                                }
//                            } else {
//
//                            }
//                        }
//                    }
//                }
//
//                Intent.OnBackwardLoadMoreItems -> {
//                    val state = getState()
//
//                    if (state is State.Loaded) {
//                        scope.launch {
//                            val prevPage = state.prevPage
//                            if (prevPage != null) {
//                                dispatch(Message.ForwardLoading)
//                                val data = repository.getShortCoinsList(
//                                    perPage = state.itemsPerPage,
//                                    page = prevPage
//                                )
//                                data.onSuccess {
//                                    dispatch(Message.ForwardDataLoaded(it))
//                                }
//                                data.onFailure {
//                                    dispatch(Message.ForwardDataLoadingError(it))
//                                }
//                            } else {
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//class CoinsListStoreFactory(
//    private val storeFactory: StoreFactory,
//    private val repository: CoinRepository
//) {
//    fun create(): CoinsListStore =
//        object : CoinsListStore, Store<Intent, State, Label> by storeFactory
//            .create(
//                name = "CoinsListStore",
//                autoInit = true,
//                initialState = State.Loading,
//                bootstrapper = SimpleBootstrapper(Unit),
//                executorFactory = { ExecutorImpl(repository) },
//                reducer = ReducerImpl()
//            ) {}
//}