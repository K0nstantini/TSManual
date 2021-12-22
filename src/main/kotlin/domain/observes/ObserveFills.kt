package domain.observes

import data.remote.Authentication
import data.remote.response.FillResponse
import data.remote.response.FtxWebSocketsResponse
import data.remote.util.sendPing
import domain.ObserveUserCase
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import util.WebsocketsConst.ANSWER_PONG
import util.WebsocketsConst.WS_HOST

@InternalAPI
class ObserveFills : ObserveUserCase<Unit, FillResponse>(), KoinComponent {

    private val ws: HttpClient = get()
    private val authentication: Authentication = get()
    private val json = Json { ignoreUnknownKeys = true }

    override fun createObservable(params: Unit): Flow<FillResponse> {
        val auth = authentication.getWSAuthentication()
        return listenOrders(auth)
    }

    private fun listenOrders(auth: String) = flow {
        ws.wss(host = WS_HOST) {
            send(auth)
            send(SUBSCRIBE_FILLS)
            launch { sendPing() }

            try {
                for (message in incoming) {
                    message as? Frame.Text ?: continue
                    when (val receivedText = message.readText()) {
                        ANSWER_PONG -> continue
                        ANSWER_FILLS -> continue
                        else -> {
                            json.decodeFromString<FtxWebSocketsResponse<FillResponse>>(receivedText)
                                .also {
                                    emit(it.data)
                                }
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error while receiving fills: " + e.message)
            }
        }
        ws.close()
    }

    companion object {
        private const val SUBSCRIBE_FILLS = "{\"op\": \"subscribe\", \"channel\": \"fills\"}"
        private const val ANSWER_FILLS = "{\"type\": \"subscribed\", \"channel\": \"fills\"}"
    }

}