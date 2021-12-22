package ui_main

import androidx.compose.runtime.Immutable
import enums.OrderSide
import util.TypeLog

@Immutable
data class MainViewState(
    val start: Boolean = false,
    val log: List<TypeLog> = emptyList(),
    val clearLogOnStart: Boolean = true,
    val market: String = "",
    val orderSide: OrderSide = OrderSide.BUY
) {
    companion object {
        val Empty = MainViewState()
    }
}