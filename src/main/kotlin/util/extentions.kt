package util

import enums.OrderSide

fun OrderSide.opposite() = when (this) {
    OrderSide.BUY -> OrderSide.SELL
    OrderSide.SELL -> OrderSide.BUY
}