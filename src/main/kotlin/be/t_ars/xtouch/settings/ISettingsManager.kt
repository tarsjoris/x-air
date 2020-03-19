package be.t_ars.xtouch.settings

import java.util.*

interface ISettingsManager {
	fun loadProperties(name: String): Properties
	fun saveProperties(name: String, properties: Properties)
}