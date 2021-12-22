package ui_main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import enums.OrderSide
import io.ktor.util.*
import util.TypeLog
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@InternalAPI
@Composable
fun MainUi() {
    val viewModel = remember { MainViewModel() }

    val viewState = remember { mutableStateOf(viewModel.state) }.value
        .collectAsState(MainViewState.Empty).value

    MainUi(viewState) { action ->
        viewModel.submitAction(action)
    }
}

@Composable
private fun MainUi(
    state: MainViewState,
    actioner: (MainActions) -> Unit
) {
    Surface(
        color = Color(0xFF6D726A),
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            Control(state, actioner)
            Log(state.log, state.clearLogOnStart) { b -> actioner(MainActions.ClearLogOnStart(b)) }
        }
    }

    /*ButtonStart(state.start) { actioner(MainActions.Start) }
    Column {
        MarketWithPlaceOrder(state, actioner)
        BuySell(state.orderSide) { s -> actioner(MainActions.ChangeOrderSide(s)) }
    }*/
}

@Composable
private fun RowScope.Control(state: MainViewState, actioner: (MainActions) -> Unit) {
    Surface(
        color = Color(0xFF6D726A),
        modifier = Modifier.weight(1.5f).fillMaxSize().padding(8.dp)
    ) {
        ButtonStart(state.start) { actioner(MainActions.Start) }
    }
}

@Composable
private fun RowScope.Log(
    log: List<TypeLog>,
    clearLogOnStart: Boolean,
    clearLog: (Boolean) -> Unit
) {
    Surface(
        color = Color(0xFFC4C4C4),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(1f).fillMaxSize().padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("LOG")
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.padding(top = 8.dp, end = 8.dp)
        ) {
            Checkbox("Clear on start", clearLogOnStart, clearLog)
        }
        LazyColumn(
            modifier = Modifier.padding(top = 36.dp, start = 8.dp)
        ) {
            items(log) { message ->
                LogItem(message)
            }
        }
    }
}

@Composable
private fun LogItem(message: TypeLog) {
    Column {
        Row {
            val time = message.time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
            Text(time, color = Color(0xFF4B3434))
            Text(
                text = message.message,
                color = when {
                    message.error -> Color(0xFF9D1616)
                    message.important -> Color(0xFF426B22)
                    else -> Color.Unspecified
                },
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Divider()
    }
}

@Composable
private fun ButtonStart(
    start: Boolean,
    onClick: () -> Unit
) {
    val color = if (start) Color(0xFFB04545) else Color(0xFFA4BF8F)
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = color),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Text(text = if (start) "Stop" else "Start")
        }
    }
}

@Composable
private fun MarketWithPlaceOrder(
    state: MainViewState,
    actioner: (MainActions) -> Unit
) {
    Row {
        TextField(
            value = state.market,
            onValueChange = { actioner(MainActions.ChangeMarket(it)) },
            label = { Text("Coin") },
            singleLine = true,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Button(onClick = { actioner(MainActions.PlaceOrder) }) {
            Text(if (state.orderSide == OrderSide.BUY) "Buy" else "Sell")
        }
    }
}

@Composable
private fun BuySell(
    side: OrderSide,
    onCLick: (OrderSide) -> Unit
) {
    Surface(
        color = Color.LightGray,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(8.dp).BlackBorder()
    ) {
        Column(modifier = Modifier.padding(top = 8.dp, end = 8.dp)) {
            RadioButton("Buy", side == OrderSide.BUY) { onCLick(OrderSide.BUY) }
            RadioButton("Sell", side == OrderSide.SELL) { onCLick(OrderSide.SELL) }
        }
    }
}

@Composable
private fun RadioButton(
    label: String,
    selected: Boolean,
    onCLick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected,
            onClick = onCLick
        )
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}

/** ============================== UTIL ================================================ */

@Composable
fun Checkbox(
    label: String,
    checked: Boolean,
    onCLick: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCLick,
            colors = CheckboxDefaults.colors(checkedColor = Color.Black),
            modifier = Modifier.size(16.dp)
        )
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun Modifier.BlackBorder(): Modifier {
    return border(1.dp, color = Color.Black, RoundedCornerShape(10.dp))
}

@Preview
@Composable
fun MainUiPreview() {

    val log = listOf(
        TypeLog(LocalTime.now(), "Start"),
        TypeLog(LocalTime.now(), "Error", error = true),
        TypeLog(LocalTime.now(), "Important", important = true),
    )
    val state = MainViewState(
        log = log
    )
    MainUi(state) {}
}