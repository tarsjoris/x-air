package be.t_ars.xtouch.util

import java.util.*

fun Properties.getBoolean(key: String) =
	getProperty(key, "true") == "true"