package data.remote.request

import enums.OrderSide
import enums.OrderType
import kotlinx.serialization.Serializable

@Serializable
data class PlaceOrderRequest(
    val market: String,
    val side: OrderSide,
    val price: Double?,
    val type: OrderType = OrderType.LIMIT,
    val size: Double,
    val reduceOnly: Boolean = false,
    val ioc: Boolean = false,
    val postOnly: Boolean = false,
)