package be.t_ars.xtouch

import java.net.Inet4Address
import kotlin.system.exitProcess

private class XctlListener(private val connectedListener: (Boolean) -> Unit) : IXctlListener {
	override fun connected() =
		connectedListener.invoke(true)

	override fun disconnected() =
		connectedListener.invoke(false)
}

fun main() {
	val settingsManager = SettingsManagerImpl()
	val properties = settingsManager.loadProperties("xr18")
	val ipAddress = properties.getProperty("xr18.ipaddress", "192.168.0.3")
	println("Use XAiR XR18 device at $ipAddress")

	val interactor = XAirEditInteractorImpl(settingsManager)
	val controller = XAirEditController(interactor)

	val session = XTouchSession()
	session.addListener(controller)

	val connection = XctlConnection(Inet4Address.getByName(ipAddress))
	connection.addListener(session)

	val ui = XAirEditProxyUI(
		settingsManager,
		connection::stop,
		interactor::setCalibration
	)
	connection.addListener(XctlListener(ui::setConnected))
	ui.isVisible = true

	connection.run()
	exitProcess(0)
}