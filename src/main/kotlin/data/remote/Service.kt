package data.remote

import data.remote.HttpRoutes.BASE_URL
import data.remote.HttpRoutes.MARKETS
import data.remote.HttpRoutes.ORDERS
import data.remote.HttpRoutes.TRIGGER_ORDERS
import data.remote.request.ModifyOrderRequest
import data.remote.request.ModifyTriggerOrderRequest
import data.remote.request.PlaceOrderRequest
import data.remote.request.PlaceTriggerOrderRequest
import data.remote.response.FtxRestApiResponse
import data.remote.response.Market
import data.remote.response.PlaceOrderResponse
import data.remote.response.TriggerOrderResponse
import enums.HttpMethod
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import util.ResultOf

@InternalAPI
class Service : KoinComponent {

    private val client: HttpClient = get()
    private val headersProvider: Authentication = get()
    private val json = Json { encodeDefaults = true }

    suspend fun getMarkets(): ResultOf<List<Market>> {
        return try {
            val response: FtxRestApiResponse<List<Market>> = client.get { url(MARKETS) }
            when {
                response.result.isEmpty() -> ResultOf.Failure("Received list of markets is empty")
                else -> ResultOf.Success(response.result)
            }
        } catch (e: Exception) {
            ResultOf.Failure("Can't get markets. " + e.message)
        }
    }

    suspend fun getMarket(market: String): ResultOf<Market> {
        return try {
            val response: FtxRestApiResponse<Market> = client.get { url("$MARKETS/$market") }
            ResultOf.Success(response.result)
        } catch (e: Exception) {
            ResultOf.Failure("Can't get market $market. " + e.message)
        }
    }

    suspend fun placeOrder(order: PlaceOrderRequest): ResultOf<PlaceOrderResponse> {
        return try {
            val response = client.post<FtxRestApiResponse<PlaceOrderResponse>> {
                url(BASE_URL + ORDERS)
                contentType(ContentType.Application.Json)
                headers {
                    headersProvider.getHeaders(
                        hb = this,
                        url = ORDERS,
                        method = HttpMethod.POST,
                        body = json.encodeToString(order)
                    )
                }
                body = order
            }
            ResultOf.Success(response.result)
        } catch (e: Exception) {
            ResultOf.Failure("Can't place order. " + e.message)
        }
    }

    suspend fun placeTriggerOrder(order: PlaceTriggerOrderRequest): ResultOf<TriggerOrderResponse> {
        return try {
            val response = client.post<FtxRestApiResponse<TriggerOrderResponse>> {
                url(BASE_URL + TRIGGER_ORDERS)
                contentType(ContentType.Application.Json)
                headers {
                    headersProvider.getHeaders(
                        hb = this,
                        url = TRIGGER_ORDERS,
                        method = HttpMethod.POST,
                        body = json.encodeToString(order)
                    )
                }
                body = order
            }
            ResultOf.Success(response.result)
        } catch (e: Exception) {
            ResultOf.Failure("Can't place trigger order. " + e.message)
        }
    }

    suspend fun modifyOrder(id: Long, modify: ModifyOrderRequest): ResultOf<PlaceOrderResponse> {
        return try {
            val response = client.post<FtxRestApiResponse<PlaceOrderResponse>> {
                url("$BASE_URL$ORDERS/$id/modify")
                contentType(ContentType.Application.Json)
                headers {
                    headersProvider.getHeaders(
                        hb = this,
                        url = "$ORDERS/$id/modify",
                        method = HttpMethod.POST,
                        body = json.encodeToString(modify)
                    )
                }
                body = modify
            }
            ResultOf.Success(response.result)
        } catch (e: Exception) {
            ResultOf.Failure("Can't modify order $id. " + e.message)
        }
    }

    suspend fun modifyTriggerOrder(
        id: Long,
        modify: ModifyTriggerOrderRequest
    ): ResultOf<TriggerOrderResponse> {
        return try {
            val response = client.post<FtxRestApiResponse<TriggerOrderResponse>> {
                url("$BASE_URL$TRIGGER_ORDERS/$id/modify")
                contentType(ContentType.Application.Json)
                headers {
                    headersProvider.getHeaders(
                        hb = this,
                        url = "$TRIGGER_ORDERS/$id/modify",
                        method = HttpMethod.POST,
                        body = json.encodeToString(modify)
                    )
                }
                body = modify
            }
            ResultOf.Success(response.result)
        } catch (e: Exception) {
            ResultOf.Failure("Can't modify trigger order $id. " + e.message)
        }
    }

    suspend fun cancelOrder(id: Long): ResultOf<Boolean> {
        return try {
            val response = client.delete<FtxRestApiResponse<String>> {
                url("$BASE_URL$ORDERS/$id")
                headers {
                    headersProvider.getHeaders(
                        hb = this,
                        url = "$ORDERS/$id",
                        method = HttpMethod.DELETE
                    ) }
            }
            if (response.success) ResultOf.Success(true) else ResultOf.Failure(response.result)
        } catch (e: Exception) {
            ResultOf.Failure("Can't cancel order $id. " + e.message)
        }
    }

    suspend fun cancelTriggerOrder(id: Long): ResultOf<Boolean> {
        return try {
            val response = client.delete<FtxRestApiResponse<String>> {
                url("$$BASE_URL$TRIGGER_ORDERS/$id")
                headers {
                    headersProvider.getHeaders(
                        hb = this,
                        url = "$TRIGGER_ORDERS/$id",
                        method = HttpMethod.DELETE
                    ) }
            }
            if (response.success) ResultOf.Success(true) else ResultOf.Failure(response.result)
        } catch (e: Exception) {
            ResultOf.Failure("Can't cancel trigger order $id. " + e.message)
        }
    }

}
