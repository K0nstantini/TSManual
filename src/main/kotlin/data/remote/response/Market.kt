package data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class Market(
    val name: String,
    val sizeIncrement: Double,
)

