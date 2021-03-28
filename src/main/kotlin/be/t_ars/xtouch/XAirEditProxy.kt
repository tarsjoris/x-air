package be.t_ars.xtouch

import be.t_ars.xtouch.osc.searchXR18
import be.t_ars.xtouch.session.XTouchSessionState
import be.t_ars.xtouch.settings.SettingsManagerImpl
import be.t_ars.xtouch.ui.XAirEditProxyUI
import be.t_ars.xtouch.util.getBoolean
import be.t_ars.xtouch.xairedit.XAirEditController
import be.t_ars.xtouch.xairedit.XAirEditInteractorImpl
import be.t_ars.xtouch.xctl.IXctlConnectionListener
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
	val xAirEditProxyConnection = XAirEditProxyConnection(xr18InetAddress, sessionState, properties)

	val searcher = {
		val ipAddress = searchXR18()
		if (ipAddress != null) {
			xAirEditProxyConnection.setXR18Address(ipAddress)
		}
	}

	if (properties.getBoolean("ui")) {
		val ui = XAirEditProxyUI(
			settingsManager,
			xAirEditProxyConnection::stop,
			calibrationSetter,
			searcher
		)
		xAirEditProxyConnection.addConnectionListener(ConnectionListener(ui::setConnected))
		ui.isVisible = true
	}

	xAirEditProxyConnection.start()
	exitProcess(0)
}