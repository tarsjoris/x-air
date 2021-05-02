package be.t_ars.xtouch.webrelay

import be.t_ars.xtouch.osc.XR18OSCAPI
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.cio.websocket.send
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket

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
			resources("monitor-mix")
		}
		webSocket("/relay/monitor-mix") {
			println("Webrelay connection established")
			val connection = RelayMonitorMixConnection(state, ::send, xrR18OSCAPI)
			xrR18OSCAPI.addListener(connection)
			connection.init()
			try {
				for (frame in incoming) {
					val receivedText = String(frame.data)
					//println("[${call.request.origin.host}] Received: $receivedText")
					connection.accept(receivedText)
				}
			} finally {
				println("WebRelay connection closed")
				xrR18OSCAPI.removeListener(connection)
			}
		}
	}
}