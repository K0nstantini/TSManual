package util

const val KTOR_LOGGING = false

const val HOURS_IN_DAY = 24
const val DAYS_IN_YEAR = 365
const val HOURS_IN_YEAR = HOURS_IN_DAY * DAYS_IN_YEAR

const val MILLI_IN_SECOND = 1000
const val SECOND_IN_MINUTE = 60
const val MINUTES_IN_HOUR = 60
const val MILLI_IN_MINUTE = MILLI_IN_SECOND * SECOND_IN_MINUTE
const val MILLI_IN_HOUR = MILLI_IN_MINUTE * MINUTES_IN_HOUR

object WebsocketsConst {
    const val WS_HOST = "ftx.com/ws"
    const val SEND_PING = "{\"op\":\"ping\"}"
    const val ANSWER_PONG = "{\"type\": \"pong\"}"
}

object MarketsConst {
    const val USD = "/USD"
    const val PERP = "-PERP"
}

object MainParams {
    const val PERCENT_PROFIT_TO_PLACE_TIGGER = 0.002
    const val PERCENT_TRIGGER_PRICE = 0.0016
    const val DIFF_TRIGGER_LIMIT = 0.05
}