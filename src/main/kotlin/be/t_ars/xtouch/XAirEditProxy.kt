package be.t_ars.xtouch

import be.t_ars.xtouch.addon.XTouchAddon
import be.t_ars.xtouch.osc.XR18API
import be.t_ars.xtouch.session.XTouchSession
import be.t_ars.xtouch.settings.SettingsManagerImpl
import be.t_ars.xtouch.ui.XAirEditProxyUI
import be.t_ars.xtouch.xairedit.XAirEditController
import be.t_ars.xtouch.xairedit.XAirEditInteractorImpl
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import be.t_ars.xtouch.xctl.XctlConnectionImpl
import java.net.Inet4Address
import javax.swing.JFrame
import kotlin.system.exitProcess

private class ConnectionListener(private val connectedListener: (Boolean) -> Unit) :
	IXctlConnectionListener {
	override fun connected() =
		connectedListener(true)

	override fun disconnected() =
		connectedListener(false)
}

fun main() {
	JFrame.setDefaultLookAndFeelDecorated(true)

	val settingsManager = SettingsManagerImpl()
	val properties = settingsManager.loadProperties("xr18")
	val xr18Address = properties.getProperty("xr18.ipaddress", "192.168.0.3")
	val addonEnabled = properties.getProperty("addon.enable", "false") == "true"
	println("Use XAiR XR18 device at $xr18Address")

	val interactor = XAirEditInteractorImpl(settingsManager)
	val controller = XAirEditController(interactor)

	val session = XTouchSession()
	session.addListener(controller)

	val xr18InetAddress = Inet4Address.getByName(xr18Address)
	val connection = XctlConnectionImpl(xr18InetAddress)
	connection.addXTouchListener(session)

	val ui = XAirEditProxyUI(
		settingsManager,
		connection::stop,
		interactor::setCalibration
	)
	connection.addConnectionListener(ConnectionListener(ui::setConnected))

	if (addonEnabled) {
		val xr18API = XR18API(xr18InetAddress)
		val xTouchAddon = XTouchAddon(xr18API, connection.getOutput())
		connection.addXTouchListener(xTouchAddon)
		connection.addConnectionListener(xTouchAddon)
		xr18API.addListener(xTouchAddon)
		Thread(xr18API::run).start()
	}
	ui.isVisible = true

	connection.run()
	exitProcess(0)
}