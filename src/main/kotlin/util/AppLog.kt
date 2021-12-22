package util

import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalTime

class AppLog {

    val observe = MutableStateFlow(listOf<TypeLog>())

    fun add(message: String, important: Boolean = false) {
        add(message, error = false, important = important)
    }

    fun addError(message: String) {
        add(message, error = true, important = false)
    }

    private fun add(message: String, error: Boolean, important: Boolean) {
        val time = LocalTime.now()
        observe.value = observe.value + TypeLog(time, message, error, important)
    }

    fun clear() {
        observe.value = emptyList()
    }
}

data class TypeLog(
    val time: LocalTime,
    val message: String,
    val error: Boolean = false,
    val important: Boolean = false
)