package com.jetpack.realtimelifecycleupdate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.jetpack.realtimelifecycleupdate.ui.theme.RealTimeLifecycleUpdateTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

@FlowPreview
class MainActivity : ComponentActivity() {
    private val realTimeLifecycleUpdateViewModel by viewModels<RealTimeLifecycleUpdateViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealTimeLifecycleUpdateTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Real Time LifeCycle Update",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    ) {
                        RealTimeLifecycleUpdate(realTimeLifecycleUpdateViewModel)
                    }
                }
            }
        }
    }
}

@FlowPreview
@Composable
fun RealTimeLifecycleUpdate(viewModel: RealTimeLifecycleUpdateViewModel) {
    val currencyPrices = viewModel.currencyPrices.collectAsState()
    LazyColumn {
        itemsIndexed(currencyPrices.value, { _, item -> item.id }) { index, currencyPrice ->
            RealTimeLifecycleUpdatePriceCard(
                currencyPrice = currencyPrice,
                currencyPriceUpdateFlow = viewModel.provideCurrencyUpdateFlow(),
                onDisposed = { viewModel.onDisposed(index) },
                onCurrencyUpdated = { newPrice -> viewModel.onCurrencyUpdated(newPrice, index) })
        }
    }
}

@Composable
fun RealTimeLifecycleUpdatePriceCard(
    currencyPrice: CurrencyPrice,
    currencyPriceUpdateFlow: Flow<Int>,
    onCurrencyUpdated: (progress: Int) -> Unit,
    onDisposed: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareCurrencyPriceFlow = remember(currencyPriceUpdateFlow, lifecycleOwner) {
        currencyPriceUpdateFlow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }

    LaunchedEffect(Unit) {
        lifecycleAwareCurrencyPriceFlow.collect { progress -> onCurrencyUpdated(progress) }
    }
    DisposableEffect(Unit) { onDispose { onDisposed() } }
    CurrencyCard(currencyPrice.name, "${currencyPrice.price}", currencyPrice.priceFluctuation)
}











