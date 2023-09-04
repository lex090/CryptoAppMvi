package com.crypto.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.crypto.app.coinslist.data.CoinRepositoryImpl
import com.crypto.app.coinslist.data.CoinsNetworkService
import com.crypto.app.network.NetworkModule
import com.crypto.app.root.RealRootFlowComponent
import com.crypto.app.root.RootContent
import com.crypto.app.ui.theme.CryptoAppTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootFlowComponent = RealRootFlowComponent(
            storeFactory = DefaultStoreFactory(),
            repository = CoinRepositoryImpl(
                service = NetworkModule.networkServiceGenerator.create(
                    CoinsNetworkService::class.java
                )
            ),
            componentContext = defaultComponentContext()
        )

        setContent {
            CryptoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootContent(rootFlowComponent = rootFlowComponent)
                }
            }
        }
    }
}