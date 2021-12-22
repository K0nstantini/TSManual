package enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OrderSide {
    @SerialName("buy")
    BUY,

    @SerialName("sell")
    SELL;
}