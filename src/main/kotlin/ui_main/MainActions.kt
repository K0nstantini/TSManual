package ui_main

import enums.OrderSide

sealed class MainActions {
    data class ChangeMarket(val market: String) : MainActions()
    data class ChangeOrderSide(val side: OrderSide) : MainActions()
    object Start: MainActions()
    object PlaceOrder: MainActions()
}