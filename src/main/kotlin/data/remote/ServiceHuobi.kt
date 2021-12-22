package data.remote

import data.remote.response.huobi.HuobiRestApiResponse
import data.remote.response.huobi.PairTicker
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import util.ResultOf

@InternalAPI
class ServiceHuobi : KoinComponent {

    private val client: HttpClient = get()
    private val json = Json { encodeDefaults = true }

    suspend fun getLatestTickersForAllPairs(): ResultOf<List<PairTicker>> {
        return try {
            val response: HuobiRestApiResponse<List<PairTicker>> =
                client.get { url("https://api.huobi.pro/market/tickers") }
            ResultOf.Success(response.data)
        } catch (e: Exception) {
            ResultOf.Failure("Can't get markets. " + e.message)
        }
    }

}