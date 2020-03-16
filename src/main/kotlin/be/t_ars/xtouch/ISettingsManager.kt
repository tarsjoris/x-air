package be.t_ars.xtouch

import java.util.*

interface ISettingsManager {
	fun loadProperties(name: String): Properties
	fun saveProperties(name: String, properties: Properties)
}