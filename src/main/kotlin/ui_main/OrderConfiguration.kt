package ui_main

import enums.OrderSide
import enums.OrderTypeConfiguration
import enums.OrderTypeTrigger

data class OrderConfiguration(
    val price: Double = 0.0,
    val size: Double = 0.0,
    val market: String = "",
    val side: OrderSide = OrderSide.BUY,
    val type: OrderTypeConfiguration = OrderTypeConfiguration.LIMIT,
    val typeTrigger: OrderTypeTrigger = OrderTypeTrigger.FIXED
) {
    val isOK = price > 0 && size > 0

    companion object {
        val empty = OrderConfiguration()
    }
}
