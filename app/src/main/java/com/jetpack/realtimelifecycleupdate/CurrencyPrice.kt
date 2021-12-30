package com.jetpack.realtimelifecycleupdate

import androidx.compose.runtime.Immutable

@Immutable
data class CurrencyPrice(
    val id: Int,
    val name: String,
    val price: Int,
    val priceFluctuation: PriceFluctuation
)

enum class PriceFluctuation {
    UNKNOWN,
    UP,
    DOWN
}
