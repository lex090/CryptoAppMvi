package com.crypto.app.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.crypto.app.coinslist.CoinsList
import com.crypto.app.coinslist.RealCoinsList
import com.crypto.app.coinslist.domain.CoinRepository

interface RootFlowComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class CoinsListScreen(val component: CoinsList) : Child
    }
}

class RealRootFlowComponent(
    private val storeFactory: StoreFactory,
    private val repository: CoinRepository,
    componentContext: ComponentContext,
) : RootFlowComponent, ComponentContext by componentContext {

    private val stackNavigation = StackNavigation<Configuration>()

    override val childStack: Value<ChildStack<*, RootFlowComponent.Child>> = childStack(
        source = stackNavigation,
        initialStack = { listOf(Configuration.CoinsListConfig) },
        key = "RealRootFlowComponentChildStack",
        childFactory = ::child
    )


    private fun child(
        configuration: Configuration,
        componentContext: ComponentContext
    ): RootFlowComponent.Child {
        return RootFlowComponent.Child.CoinsListScreen(
            component = RealCoinsList(
                componentContext = componentContext,
                storeFactory = storeFactory,
                repository = repository
            )
        )
    }

    private sealed interface Configuration : Parcelable {
        @Parcelize
        data object CoinsListConfig : Configuration
    }
}