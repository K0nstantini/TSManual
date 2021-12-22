package ui_main

import androidx.compose.runtime.Immutable
import enums.OrderSide

@Immutable
data class MainViewState(
    val start: Boolean = false,
    val market: String = "",
    val orderSide: OrderSide = OrderSide.BUY
) {
    companion object {
        val Empty = MainViewState()
    }
}