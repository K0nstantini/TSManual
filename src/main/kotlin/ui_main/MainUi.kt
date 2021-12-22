package ui_main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
    ButtonStart(state.start) { actioner(MainActions.Start) }
    Column {
        MarketWithPlaceOrder(state, actioner)
        BuySell(state.orderSide) { s -> actioner(MainActions.ChangeOrderSide(s)) }
    }
}

@Composable
private fun ButtonStart(
    start: Boolean,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = if (start) Color.Red else Color.Green),
            modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
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
private fun Modifier.BlackBorder(): Modifier {
    return border(1.dp, color = Color.Black, RoundedCornerShape(10.dp))
}

@Preview
@Composable
fun MainUiPreview() {
    val state = MainViewState.Empty
    MainUi(state) {}
}