package be.t_ars.xtouch.settings

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class SettingsManagerImpl : ISettingsManager {
	private val settingsDirectory = File(File(System.getProperty("user.home")), "xtouch")

	override fun loadProperties(name: String): Properties {
		val properties = Properties()
		File(settingsDirectory, "$name.properties")
			.takeIf(File::exists)
			?.also { file ->
				BufferedInputStream(FileInputStream(file)).use(properties::load)
			}
		return properties
	}

	override fun saveProperties(name: String, properties: Properties) {
		settingsDirectory.mkdirs()
		BufferedOutputStream(FileOutputStream(File(settingsDirectory, "$name.properties"))).use {
			properties.store(it, "XAir Edit Proxy UI settings")
		}
	}
}