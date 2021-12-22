// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import di.AppModule
import io.ktor.util.*
import org.koin.core.context.startKoin
import ui_main.MainUi

@InternalAPI
fun main() = application {

    startKoin {
        modules(AppModule)
    }

    Window(
        title = "TSManual",
        state = WindowState(size = DpSize(1000.dp, 600.dp)),
        onCloseRequest = ::exitApplication
    ) {
        MaterialTheme { MainUi() }
    }
}
