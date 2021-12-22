package data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class FtxRestApiResponse<T>(
    val success: Boolean,
    val result: T
)

