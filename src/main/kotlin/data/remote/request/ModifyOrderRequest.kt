package data.remote.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ModifyOrderRequest(
    val price: Double,
    val size: Double
) {

    @Transient
    val isOk = price > 0 && size > 0
}
