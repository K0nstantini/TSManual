package data.remote

object HttpRoutes {
    const val BASE_URL = "https://ftx.com/"
    const val MARKETS = "$BASE_URL/api/markets"
    const val FUTURES = "$BASE_URL/api/futures"
    const val STATS = "/stats"
    const val ORDERS = "api/orders"
    const val TRIGGER_ORDERS = "api/conditional_orders"
    const val BALANCES = "api/wallet/balances"
    const val ACCOUNT = "api/account"
    const val FILLS = "api/fills"
    const val FUNDING_PAYMENTS = "api/funding_payments"
}