package com.tjors.xtouch

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.net.Inet4Address
import java.util.*
import kotlin.system.exitProcess

fun main() {
	val props = Properties()
	File("xr18.properties")
		.takeIf(File::exists)
		?.also { file ->
			BufferedInputStream(FileInputStream(file)).use(props::load)
		}
	val ipAddress = props.getProperty("xr18.ipaddress", "192.168.0.3")
	println("Use XAiR XR18 device at $ipAddress")
	val controller = XAirEditController(XAirEditInteractor())
	val connection = XctlConnection(Inet4Address.getByName(ipAddress))
	connection.addListener(controller)

	XAirEditProxyUI(connection).isVisible = true

	connection.run()
	exitProcess(0);
}