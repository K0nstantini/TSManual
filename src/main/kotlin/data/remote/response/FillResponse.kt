package data.remote.response

import enums.OrderSide
import kotlinx.serialization.Serializable

@Serializable
data class FillResponse(
    val fee: Double,
    val market: String,
    val orderId: Long,
    val price: Double,
    val side: OrderSide,
    val size: Double,
)