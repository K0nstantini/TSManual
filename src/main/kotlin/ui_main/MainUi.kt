package ui_main

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import enums.OrderSide
import enums.OrderTypeConfiguration
import enums.OrderTypeTrigger
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

    /*
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
        Market(state.order.market) { s -> actioner(MainActions.ChangeMarket(s)) }
        PriceSizePlace(state, actioner)
    }
}

/** ================================================== CONTROL ===================================================== */

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
private fun Market(
    market: String,
    onValueChange: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = market,
            onValueChange = onValueChange,
            label = { Text("Market") },
            singleLine = true,
            modifier = Modifier.padding(end = 8.dp).width(120.dp)
        )
    }
}

@Composable
private fun PriceSizePlace(state: MainViewState, actioner: (MainActions) -> Unit) {
    Column {
        Row {
            DoubleField("Price", state.order.price) { s -> actioner(MainActions.ChangePrice(s)) }
            ButtonLast()
        }
        DoubleField("Size", state.order.size) { s -> actioner(MainActions.ChangeSize(s)) }
        BuySellButton(state.order) { actioner(MainActions.PlaceOrder) }
        Row {
            SideRadio(state.order.side) { s -> actioner(MainActions.ChangeSide(s)) }
            OrderTypeRadio(state.order.type) { t -> actioner(MainActions.ChangeType(t)) }
            OrderTypeTriggerRadio(state.order.typeTrigger) { t -> actioner(MainActions.ChangeTypeTrigger(t)) }
        }
    }
}

@Composable
fun ButtonLast() {
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF8C9677)),
        modifier = Modifier.padding(start = 4.dp).width(60.dp)
    ) {
        Text("Last", fontSize = 10.sp)
    }
}

@Composable
private fun DoubleField(
    label: String,
    value: Double,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.width(120.dp)
    )
}

@Composable
fun BuySellButton(
    order: OrderConfiguration,
    onClick: () -> Unit
) {
    val (text, color) = when (order.side) {
        OrderSide.BUY -> "Buy" to Color(0xFF40572E)
        OrderSide.SELL -> "Sell" to Color(0xFFB04545)
    }
    Button(
        onClick = onClick,
        enabled = order.isOK,
        colors = ButtonDefaults.buttonColors(backgroundColor = color, disabledBackgroundColor = Color(0xFF555E4F)),
        modifier = Modifier.width(184.dp)
    ) {
        Text(text)
    }
}

@Composable
private fun SideRadio(
    side: OrderSide,
    onClick: (OrderSide) -> Unit
) {
    Surface(
        color = Color(0xFFA6C7A9),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(8.dp).width(100.dp)
    ) {
        Column(modifier = Modifier.padding(top = 8.dp, end = 8.dp)) {
            RadioButton("Buy", side == OrderSide.BUY) { onClick(OrderSide.BUY) }
            RadioButton("Sell", side == OrderSide.SELL) { onClick(OrderSide.SELL) }
        }
    }
}

@Composable
private fun OrderTypeRadio(
    type: OrderTypeConfiguration,
    onClick: (OrderTypeConfiguration) -> Unit
) {
    val (limit, trigger) = OrderTypeConfiguration.LIMIT to OrderTypeConfiguration.TRIGGER
    Surface(
        color = Color(0xFFA6C7A9),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(8.dp).width(100.dp)
    ) {
        Column(modifier = Modifier.padding(top = 8.dp, end = 8.dp)) {
            RadioButton("Limit", type == limit) { onClick(limit) }
            RadioButton("Trigger", type == trigger) { onClick(trigger) }
        }
    }
}

@Composable
private fun OrderTypeTriggerRadio(
    type: OrderTypeTrigger,
    onClick: (OrderTypeTrigger) -> Unit
) {
    val (fixed, trailing) = OrderTypeTrigger.FIXED to OrderTypeTrigger.TRAILING
    Surface(
        color = Color(0xFFA6C7A9),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(8.dp).width(100.dp)
    ) {
        Column(modifier = Modifier.padding(top = 8.dp, end = 8.dp)) {
            RadioButton("Fixed", type == fixed) { onClick(fixed) }
            RadioButton("Triling", type == trailing) { onClick(trailing) }
        }
    }
}

/** ================================================== LOG ======================================================= */

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

/** ============================== UTIL ================================================ */

@Composable
private fun RadioButton(
    label: String,
    selected: Boolean,
    onCLick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected,
            onClick = onCLick,
            colors = RadioButtonDefaults.colors(selectedColor = Color.Black),
            modifier = Modifier.size(30.dp)
        )
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}

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