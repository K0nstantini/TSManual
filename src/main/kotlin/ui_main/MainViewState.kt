package ui_main

import androidx.compose.runtime.Immutable
import util.TypeLog

@Immutable
data class MainViewState(
    val start: Boolean = false,
    val log: List<TypeLog> = emptyList(),
    val clearLogOnStart: Boolean = true,
    val order: OrderConfiguration = OrderConfiguration.empty,
) {
    companion object {
        val Empty = MainViewState()
    }
}