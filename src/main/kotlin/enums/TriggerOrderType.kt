package enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TriggerOrderType {
    @SerialName("stop")
    STOP,

    @SerialName("trailingStop")
    TRAILING_STOP,

    @SerialName("takeProfit")
    TAKE_PROFIT,
}