package be.t_ars.xtouch

import be.t_ars.xtouch.router.ProxyRouter
import be.t_ars.xtouch.session.XTouchSessionState
import be.t_ars.xtouch.settings.SettingsManagerImpl
import be.t_ars.xtouch.ui.XAirEditProxyUI
import be.t_ars.xtouch.util.getBoolean
import be.t_ars.xtouch.xairedit.XAirEditController
import be.t_ars.xtouch.xairedit.XAirEditInteractorImpl
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import be.t_ars.xtouch.xctl.XctlConnectionProxy
import kotlinx.coroutines.GlobalScope
import java.net.Inet4Address
import kotlin.system.exitProcess

private class ConnectionListener(private val connectedListener: (Boolean) -> Unit) :
	IXctlConnectionListener {
	override fun connected() =
		connectedListener(true)

	override fun disconnected() =
		connectedListener(false)
}

fun main() {
	val settingsManager = SettingsManagerImpl()
	val properties = settingsManager.loadProperties("xtouch")
	val xr18Address = properties.getProperty("xr18.ipaddress", "192.168.0.3")
	println("Use XAiR XR18 device at $xr18Address")

	val sessionState = XTouchSessionState()

	val calibrationSetter: ((Int, Int, Int, Int) -> Unit)?
	if (properties.getBoolean("xairedit.interact")) {
		val interactor = XAirEditInteractorImpl(settingsManager)
		val controller = XAirEditController(GlobalScope, interactor)
		sessionState.addListener(controller)
		calibrationSetter = interactor::setCalibration
	} else {
		calibrationSetter = null
	}

	val xr18InetAddress = Inet4Address.getByName(xr18Address)
	val connection = XctlConnectionProxy(xr18InetAddress)

	val router = ProxyRouter(
		xr18InetAddress,
		sessionState,
		connection.getConnectionToXTouch(),
		connection.getConnectionToXR18(),
		properties
	)
	connection.addConnectionEventProcessor(router::routeConnectionEvent)
	connection.addXTouchEventProcessor(router::routeEventFromXTouch)
	connection.addXR18EventProcessor(router::routeEventFromXR18)
	router.addXTouchListener(sessionState)

	if (properties.getBoolean("ui")) {
		val ui = XAirEditProxyUI(
			settingsManager,
			connection::stop,
			calibrationSetter
		)
		router.addConnectionListener(ConnectionListener(ui::setConnected))
		ui.isVisible = true
	}

	connection.run()
	exitProcess(0)
}