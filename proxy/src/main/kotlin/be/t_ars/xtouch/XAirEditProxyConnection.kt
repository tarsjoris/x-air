package be.t_ars.xtouch

import be.t_ars.xtouch.osc.XR18OSCAPI
import be.t_ars.xtouch.router.ProxyRouter
import be.t_ars.xtouch.session.XTouchSessionState
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import be.t_ars.xtouch.xctl.XctlConnectionProxy
import java.net.InetAddress
import java.util.*

class XAirEditProxyConnection(
	xr18InetAddress: InetAddress,
	sessionState: XTouchSessionState,
	xr18OSCAPI: XR18OSCAPI,
	properties: Properties)
{
	private val connection: XctlConnectionProxy
	private val router: ProxyRouter

	init {
		connection = XctlConnectionProxy(xr18InetAddress)

		router = ProxyRouter(
			xr18InetAddress,
			sessionState,
			connection.getConnectionToXTouch(),
			connection.getConnectionToXR18(),
			xr18OSCAPI,
			properties
		)
		connection.addConnectionEventProcessor(router::routeConnectionEvent)
		connection.addXTouchEventProcessor(router::routeEventFromXTouch)
		connection.addXR18EventProcessor(router::routeEventFromXR18)
		router.addXTouchListener(sessionState)
	}

	fun addConnectionListener(listener: IXctlConnectionListener) =
		connection.addConnectionListener(listener)

	fun setXR18Address(xr18Address: InetAddress) {
		connection.setXR18Address(xr18Address)
		router.xr18Address = xr18Address
	}

	fun start() =
		connection.run()

	fun stop() {
		connection.stop()
	}
}