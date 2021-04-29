package be.t_ars.xtouch.webrelay

import be.t_ars.xtouch.osc.XR18OSCAPI
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.origin
import io.ktor.http.cio.websocket.send
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import java.io.File

const val RELAY_PORT: Int = 8080

fun startRelay(proxyAddress: String, xrR18OSCAPI: XR18OSCAPI) {
	val state = WebRelayState(xrR18OSCAPI)
	xrR18OSCAPI.addListener(state)
	embeddedServer(Netty, port = RELAY_PORT, host = proxyAddress) {
		configureRouting(xrR18OSCAPI, state)
	}.start(wait = false)
}

private fun Application.configureRouting(
	xrR18OSCAPI: XR18OSCAPI,
	state: WebRelayState
) {
	println("Starting ktor netty")
	install(WebSockets)
	routing {
		static("/monitor-mix/") {
			staticRootFolder = File("monitor-mix\\build")
			files(".")
			default("index.html")
		}
		webSocket("/relay/monitor-mix") {
			val connection = RelayMonitorMixConnection(state, ::send)
			xrR18OSCAPI.addListener(connection)
			connection.init()
			try {
				for (frame in incoming) {
					val receivedText = String(frame.data)
					println("[${call.request.origin.host}] Received: $receivedText")
					connection.accept(receivedText)
				}
			} finally {
				println("Connection closed")
				xrR18OSCAPI.removeListener(connection)
			}
		}
	}
}