package di

import data.remote.Authentication
import data.remote.Service
import data.remote.ServiceHuobi
import domain.observes.ObserveFills
import domain.observes.ObserveTicks
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import io.ktor.util.*
import org.koin.dsl.module
import util.AppLog
import util.KTOR_LOGGING
import kotlinx.serialization.json.Json as KotlinJson

@InternalAPI
val AppModule = module {

    /** KTor */
    single {
        HttpClient(Java) {
            if (KTOR_LOGGING) {
                install(Logging) {
                    level = LogLevel.ALL
                }
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    KotlinJson {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                    }
                )
            }
            install(WebSockets)
        }
    }

    single { AppLog() }
    single { Authentication() }
    single { Service() }
    single { ServiceHuobi() }

    // Observe
    single { ObserveTicks() }
    single { ObserveFills() }

}
