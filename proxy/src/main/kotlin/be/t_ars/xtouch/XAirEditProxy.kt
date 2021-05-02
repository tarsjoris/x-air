package be.t_ars.xtouch

import be.t_ars.xtouch.osc.XR18OSCAPI
import be.t_ars.xtouch.osc.searchXR18
import be.t_ars.xtouch.session.XTouchSessionState
import be.t_ars.xtouch.settings.ISettingsManager
import be.t_ars.xtouch.settings.SettingsManagerImpl
import be.t_ars.xtouch.ui.XAirEditProxyUI
import be.t_ars.xtouch.util.getBoolean
import be.t_ars.xtouch.webrelay.RELAY_PORT
import be.t_ars.xtouch.webrelay.startRelay
import be.t_ars.xtouch.xairedit.IXAirEditInteractor
import be.t_ars.xtouch.xairedit.XAirEditController
import be.t_ars.xtouch.xairedit.XAirEditInteractorImpl
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.net.Inet4Address
import java.util.*
import kotlin.system.exitProcess

private class ConnectionListener(private val connectedListener: (Boolean) -> Unit) :
	IXctlConnectionListener {
	override fun connected() =
		connectedListener(true)

	override fun disconnected() =
		connectedListener(false)
}

private fun connectXAirEdit(interactor: IXAirEditInteractor) {
	runBlocking {
		delay(1000)
		interactor.clickConnect()
		delay(1000)
		interactor.clickMixerPc()
	}
}

private fun createInteractor(
	properties: Properties,
	settingsManager: ISettingsManager,
	sessionState: XTouchSessionState
) : ((Int, Int, Int, Int) -> Unit)? {
	return if (properties.getBoolean("xairedit.interact")) {
		val interactor = XAirEditInteractorImpl(settingsManager)
		val controller = XAirEditController(GlobalScope, interactor)
		sessionState.addListener(controller)

		if (properties.getBoolean("xairedit.connect", "false")) {
			connectXAirEdit(interactor)
		}
		interactor::setCalibration
	} else {
		null
	}
}

private fun createUI(
	properties: Properties,
	xAirEditProxyConnection: XAirEditProxyConnection?,
	xr18OSCAPI: XR18OSCAPI,
	settingsManager: ISettingsManager,
	calibrationSetter: ((Int, Int, Int, Int) -> Unit)?,
	monitorMixLink: String?
) {
	if (properties.getBoolean("ui")) {
		val stop = {
			xAirEditProxyConnection?.stop()
			xr18OSCAPI.stop()
		}

		val searcher = {
			val ipAddress = searchXR18()
			if (ipAddress != null) {
				println("Found XR18 at $ipAddress")
				xAirEditProxyConnection?.setXR18Address(ipAddress)
				xr18OSCAPI.setHost(ipAddress)
			}
		}

		val ui = XAirEditProxyUI(
			settingsManager,
			stop,
			xAirEditProxyConnection != null,
			calibrationSetter,
			searcher,
			monitorMixLink
		)
		xAirEditProxyConnection?.addConnectionListener(ConnectionListener(ui::setConnected))
		ui.isVisible = true
	}
}

fun main() {
	val settingsManager = SettingsManagerImpl()
	val properties = settingsManager.loadProperties("xtouch")
	val xr18Address = properties.getProperty("xr18.ipaddress", "192.168.0.2")
	val proxyAddress = properties.getProperty("address", "192.168.0.4")
	val xr18InetAddress = Inet4Address.getByName(xr18Address)
	println("Use XAiR XR18 device at $xr18Address")

	val xr18OSCAPI = XR18OSCAPI(xr18InetAddress)

	val calibrationSetter: ((Int, Int, Int, Int) -> Unit)?
	val xAirEditProxyConnection: XAirEditProxyConnection?
	if (properties.getBoolean("xtouch.enabled")) {
		val sessionState = XTouchSessionState()
		calibrationSetter = createInteractor(properties, settingsManager, sessionState)
		xAirEditProxyConnection = XAirEditProxyConnection(xr18InetAddress, sessionState, xr18OSCAPI, properties)
	} else {
		calibrationSetter = null
		xAirEditProxyConnection = null
	}

	val monitorMixLink: String?
	if (properties.getBoolean("webrelay.enabled")) {
		monitorMixLink = "http://$proxyAddress:$RELAY_PORT/monitor-mix/index.html"
		startRelay(proxyAddress, xr18OSCAPI)
	} else {
		monitorMixLink = null
	}

	createUI(properties, xAirEditProxyConnection, xr18OSCAPI, settingsManager, calibrationSetter, monitorMixLink)

	if (xAirEditProxyConnection != null) {
		Thread(xr18OSCAPI::run).start()
		xAirEditProxyConnection.start()
	} else {
		xr18OSCAPI.run()
	}

	exitProcess(0)
}