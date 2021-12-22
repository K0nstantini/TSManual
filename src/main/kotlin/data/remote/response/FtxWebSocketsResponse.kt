package data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class FtxWebSocketsResponse<T>(
    val channel: String,
    val data: T,
    val type: String,
    val market: String? = null,
)