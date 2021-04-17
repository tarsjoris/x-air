package be.t_ars.xtouch.webrelay

import be.t_ars.xtouch.osc.XR18OSCAPI
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.origin
import io.ktor.http.cio.websocket.send
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket


fun startRelay(xrR18OSCAPI: XR18OSCAPI) {
	val state = WebRelayState(xrR18OSCAPI)
	xrR18OSCAPI.addListener(state)
	embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
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