package data.local

import data.remote.response.FillResponse
import data.remote.response.TriggerOrderResponse

data class Deal(
    val fill: FillResponse,
    var lastDiff: Double = 0.0,
    val trigger: TriggerOrderResponse? = null
)
