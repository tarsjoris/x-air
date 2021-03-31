package be.t_ars.xtouch.util

import java.util.*

fun Properties.getBoolean(key: String, defaultValue: String = "true") =
	getProperty(key, defaultValue) == "true"