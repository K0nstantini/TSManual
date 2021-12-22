package data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class Ticker(
    val bid: Double,
    val ask: Double,
    val bidSize: Double,
    val askSize: Double,
    val last: Double,
)