package data.remote.response

import enums.OrderSide
import kotlinx.serialization.Serializable

@Serializable
data class PlaceOrderResponse(
    val id: Long,
    val price: Double?,
    val size: Double,
    val market: String,
    val side: OrderSide,
    val createdAt: String,
)