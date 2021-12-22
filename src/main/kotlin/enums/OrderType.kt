package enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OrderType {
    @SerialName("limit")
    LIMIT,

    @SerialName("market")
    MARKET,

}