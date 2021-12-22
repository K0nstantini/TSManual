package data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class WSAuthentication(
    val args: WSAuthenticationArgs,
    val op: String = "login"
)

