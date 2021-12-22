package data.remote.util

import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.delay
import util.WebsocketsConst.SEND_PING

private const val PING_INTERVAL = 15_000L

suspend fun DefaultClientWebSocketSession.sendPing() {
    while (true) {
        delay(PING_INTERVAL)
        try {
            send(SEND_PING)
        } catch (e: Exception) {
            return
        }
    }
}