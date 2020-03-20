package be.t_ars.xtouch

import be.t_ars.xtouch.settings.SettingsManagerImpl
import be.t_ars.xtouch.xairedit.XAirEditController
import be.t_ars.xtouch.xairedit.XAirEditInteractorImpl
import be.t_ars.xtouch.xctl.IXctlConnection
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import be.t_ars.xtouch.xctl.XctlConnectionImpl
import java.awt.Color
import java.net.Inet4Address
import javax.swing.UIManager
import kotlin.system.exitProcess

private class ConnectionListener(private val connectedListener: (Boolean) -> Unit) :
	IXctlConnectionListener {
	override fun connected() =
		connectedListener.invoke(true)

	override fun disconnected() =
		connectedListener.invoke(false)
}

fun main() {
	UIManager.put("control", Color(33, 33, 33));
	UIManager.put("nimbusBase", Color(47, 47, 47));
	UIManager.getInstalledLookAndFeels()
		.firstOrNull { it.name == "Nimbus" }
		?.let { UIManager.setLookAndFeel(it.className) }

	val settingsManager = SettingsManagerImpl()
	val properties = settingsManager.loadProperties("xr18")
	val ipAddress = properties.getProperty("xr18.ipaddress", "192.168.0.3")
	println("Use XAiR XR18 device at $ipAddress")

	val interactor = XAirEditInteractorImpl(settingsManager)
	val controller = XAirEditController(interactor)

	val session = XTouchSession()
	session.addListener(controller)

	val connection: IXctlConnection = XctlConnectionImpl(Inet4Address.getByName(ipAddress))
	connection.addXTouchListener(session)

	val ui = XAirEditProxyUI(
		settingsManager,
		connection::stop,
		interactor::setCalibration
	)
	connection.addConnectionListener(ConnectionListener(ui::setConnected))
	ui.isVisible = true

	connection.run()
	exitProcess(0)
}