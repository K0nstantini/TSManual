package data.remote.response

import enums.OrderSide
import kotlinx.serialization.Serializable

@Serializable
data class TriggerOrderResponse(
    val id: Long,
    val triggerPrice: Double,
    val size: Double,
    val market: String,
    val side: OrderSide,
    val createdAt: String,
)