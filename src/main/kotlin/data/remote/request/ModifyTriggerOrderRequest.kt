package data.remote.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ModifyTriggerOrderRequest(
    val triggerPrice: Double,
    val size: Double
) {

    @Transient
    val isOk = triggerPrice > 0 && size > 0
}
