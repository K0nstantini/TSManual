package ui_main

import data.local.Deal
import data.remote.Service
import data.remote.request.PlaceTriggerOrderRequest
import data.remote.response.Market
import data.remote.response.Ticker
import domain.observes.ObserveFills
import domain.observes.ObserveTicks
import enums.OrderSide
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import util.MainParams.DIFF_TRIGGER_LIMIT
import util.ResultOf
import util.opposite

@InternalAPI
class MainViewModel : KoinComponent {

    private val service: Service = get()
    private val scope = CoroutineScope(Dispatchers.Default)
    private val pendingActions = MutableSharedFlow<MainActions>()

    private val observeTicks: ObserveTicks = get()
    private val observeFills: ObserveFills = get()

    private var jobs = mutableListOf<Job>()

    private val start = MutableStateFlow(false)
    private val marketName = MutableStateFlow("")
    private val orderSide = MutableStateFlow(OrderSide.BUY)

    private val _market: MutableStateFlow<Market?> = MutableStateFlow(null)
    private val market: Market get() = _market.value ?: throw Exception("Market is null")

    private val deals = MutableStateFlow(listOf<Deal>())

    private var currentTick: Ticker? = null

    val state = combine(start, marketName, orderSide) { start, marketName, side ->
        MainViewState(
            start = start,
            market = marketName,
            orderSide = side
        )
    }

    init {
        scope.launch {
            pendingActions.collect { action ->
                when (action) {
                    MainActions.Start -> start()
                    MainActions.PlaceOrder -> placeOrder()
                    is MainActions.ChangeMarket -> changeMarket(action.market)
                    is MainActions.ChangeOrderSide -> changeOrderSide(action.side)
                }
            }
        }
    }

    /** ======================================== FUNCTIONS ===================================================== */

    private suspend fun start() {
        getMarket()
        start.value = !start.value

        if (start.value) {
            jobs.add(scope.launch { observeFills() })
            jobs.add(scope.launch { observeTicks() })
        } else {
            jobs.forEach { it.cancel() }
            jobs.clear()
        }
    }

    private fun changeMarket(marketName: String) {
        this.marketName.value = marketName
    }

    private fun changeOrderSide(side: OrderSide) {
        orderSide.value = side
    }

    private suspend fun getMarket() {
        when (val result = service.getMarket(marketName.value)) {
            is ResultOf.Success -> _market.value = result.value
            is ResultOf.Failure -> throw Exception("Can't get market ${result.message}")
        }
    }

    private suspend fun placeOrder() {
        /*ticker?.let {
            val order = PlaceOrderRequest(
                market = market.value,
                price = if (orderSide.value == OrderSide.BUY) it.bid else it.ask,
                size = 100.0,
                side = orderSide.value,
                postOnly = true
            )
            println("Placed order:\n$order")
            service.placeOrder(order)
        }*/
    }

    /** ======================================== OBSERVE ===================================================== */

    private suspend fun observeFills() {
        observeFills(Unit)
        observeFills.observe().collect {
            updateDeals(Deal(it))
        }
    }

    private suspend fun observeTicks() {
        observeTicks(ObserveTicks.Params(market.name))
        observeTicks.observe().collect { tick ->
            handleTick(tick)
        }
    }

    /** ======================================== OTHERS ===================================================== */

    private fun handleTick(tick: Ticker) {
        currentTick = tick
        deals.value.forEach { deal ->
            when (deal.fill.side) {
                OrderSide.BUY -> checkBuyDeal(deal, tick.last)
                OrderSide.SELL -> checkSellDeal(tick.last)
            }
        }
    }

    private fun checkBuyDeal(deal: Deal, last: Double) {
        deal.lastDiff = last / deal.fill.price
    }

    private fun checkSellDeal(last: Double) {
        TODO("Not yet implemented")
    }

    private suspend fun placeTriggerOrder(deal: Deal, triggerPrice: Double) {
        cancelTriggerOrder(deal)

        val order = with(deal.fill) {
            PlaceTriggerOrderRequest(
                market = market,
                triggerPrice = triggerPrice,
                orderPrice = getOrderPriceForTriggerOrder(triggerPrice, side.opposite()),
                size = size,
                side = side.opposite(),
                reduceOnly = true
            )
        }
        when (val result = service.placeTriggerOrder(order)) {
            is ResultOf.Success -> {
                updateDeals(deal.copy(trigger = result.value))
                println("Canceled trigger order\n${result.value}")
            }
            is ResultOf.Failure -> throw Exception("Can't place trigger order\n${result.message}")
        }
    }

    private suspend fun cancelTriggerOrder(deal: Deal) {
        deal.trigger?.let { order ->
            when (service.cancelTriggerOrder(order.id)) {
                is ResultOf.Success -> println("Canceled trigger order\n$order")
                is ResultOf.Failure -> throw Exception("Can't cancel trigger order\n$order")
            }
        }
    }

    /** ======================================== UTIL ======================================================== */

    @Synchronized
    private fun updateDeals(deal: Deal) {
        deals.value = (deals.value.filter { it.fill.orderId != deal.fill.orderId } + deal).filter { it.fill.size > 0 }
    }

    private fun getOrderPriceForTriggerOrder(triggerPrice: Double, side: OrderSide): Double {
        return triggerPrice * (1 + if (side == OrderSide.SELL) -DIFF_TRIGGER_LIMIT else DIFF_TRIGGER_LIMIT)
    }

    fun submitAction(action: MainActions) = scope.launch {
        pendingActions.emit(action)
    }
}