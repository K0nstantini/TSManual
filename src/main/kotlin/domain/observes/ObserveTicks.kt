package domain.observes

import data.remote.response.FtxWebSocketsResponse
import data.remote.response.Ticker
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
class ObserveTicks : ObserveUserCase<ObserveTicks.Params, Ticker>(), KoinComponent {

    private val ws: HttpClient = get()
    private val json = Json { ignoreUnknownKeys = true }

    override fun createObservable(params: Params): Flow<Ticker> {
        return listenTicks(params.market)
    }

    private fun listenTicks(market: String) = flow {
        var ask = 0.0
        var bid = 0.0
        var last = 0.0

        ws.wss(host = WS_HOST) {
            send("$SUBSCRIBE_TICKS\"$market\"}")
            launch { sendPing() }

            try {
                for (message in incoming) {
                    message as? Frame.Text ?: continue
                    val receivedText = message.readText()
                    when {
                        receivedText.startsWith("{\"channel\": \"ticker\"") -> {
                            val data = json.decodeFromString<FtxWebSocketsResponse<Ticker>>(receivedText)
                            if (data.data.ask != ask || data.data.bid != bid || data.data.last != last) {
                                emit(data.data)
                                ask = data.data.ask
                                bid = data.data.bid
                                last = data.data.last
                            }
                        }
                        receivedText == ANSWER_PONG -> continue
                        receivedText.startsWith("{\"type\": \"subscribed\"") -> continue
                        else -> return@wss

                    }
                }
            } catch (e: Exception) {
                return@wss
            }
        }
        ws.close()
    }

    companion object {
        private const val SUBSCRIBE_TICKS = "{\"op\": \"subscribe\", \"channel\": \"ticker\", \"market\": "
    }

    data class Params(val market: String)

}