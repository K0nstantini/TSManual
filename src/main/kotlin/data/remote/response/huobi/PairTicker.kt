package data.remote.response.huobi

import kotlinx.serialization.Serializable

@Serializable
data class PairTicker(
    val symbol: String,
    val bid: Double,
    val ask: Double,
)
