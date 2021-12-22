package data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class WSAuthenticationArgs(
    val key: String,
    val sign: String,
    val time: Long
)