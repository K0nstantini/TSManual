package data.remote.response.huobi

import kotlinx.serialization.Serializable

@Serializable
data class HuobiRestApiResponse<T>(
    val status: String,
    val data: T
)

