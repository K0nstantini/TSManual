package data.remote

import data.remote.request.WSAuthentication
import data.remote.request.WSAuthenticationArgs
import enums.HttpMethod
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import java.math.BigInteger
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@InternalAPI
class Authentication : KoinComponent {

    private val api: Api
    private val hashMaker: Mac
    private val json = Json { encodeDefaults = true }

    init {
        val key = System.getenv("API_KEY") ?: throw Exception("Can't get API_KEY")
        val secret = System.getenv("API_SECRET") ?: throw Exception("Can't get API_SECRET")
        api =  Api(key, secret)
        hashMaker = Mac.getInstance(HMACSHA256).apply { init(SecretKeySpec(api.secret.toByteArray(), HMACSHA256)) }
    }

    fun getHeaders(
        hb: HeadersBuilder,
        url: String,
        method: HttpMethod = HttpMethod.GET,
        body: String = ""
    ): HeadersBuilder {
        val milli = System.currentTimeMillis().toString()
        return hb.apply {
            append(FTX_KEY, api.key)
            append(FTX_TS, milli)
            append(FTX_SIGN, createSignature("$milli$method/$url$body"))
            append(FTX_SUBACCOUNT, "Trade")
        }
    }

    fun getWSAuthentication(): String {
        val milli = System.currentTimeMillis()
        val args = WSAuthenticationArgs(
            key = api.key,
            sign = createSignature("$milli" + "websocket_login"),
            time = milli
        )
        return json.encodeToString(WSAuthentication(args))
    }

    private fun createSignature(str: String): String {
        val hash = hashMaker.doFinal(str.toByteArray(charset("UTF8")))
        return String.format("%032x", BigInteger(1, hash))
    }

    companion object {
        private const val FTX_KEY = "FTX-KEY"
        private const val FTX_TS = "FTX-TS"
        private const val FTX_SIGN = "FTX-SIGN"
        private const val FTX_SUBACCOUNT = "FTX-SUBACCOUNT"

        private const val HMACSHA256 = "HmacSHA256"
    }

    data class Api(
        val key: String,
        val secret: String,
    )
}