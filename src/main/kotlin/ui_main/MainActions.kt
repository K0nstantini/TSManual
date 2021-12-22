package ui_main

import enums.OrderSide
import enums.OrderTypeConfiguration
import enums.OrderTypeTrigger

sealed class MainActions {
    data class ChangeMarket(val market: String) : MainActions()
    data class ChangePrice(val price: String) : MainActions()
    data class ChangeSize(val size: String) : MainActions()
    data class ChangeSide(val side: OrderSide) : MainActions()
    data class ChangeType(val type: OrderTypeConfiguration) : MainActions()
    data class ChangeTypeTrigger(val type: OrderTypeTrigger) : MainActions()
    data class ClearLogOnStart(val clear: Boolean) : MainActions()
    object Start: MainActions()
    object PlaceOrder: MainActions()
}