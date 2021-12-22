package data.remote.request

import enums.OrderSide
import enums.TriggerOrderType
import kotlinx.serialization.Serializable

@Serializable
data class PlaceTriggerOrderRequest(
    val size: Double,
    val triggerPrice: Double,
    val orderPrice: Double,
    val market: String,
    val side: OrderSide,
    val type: TriggerOrderType = TriggerOrderType.STOP,
    val reduceOnly: Boolean = false,
)