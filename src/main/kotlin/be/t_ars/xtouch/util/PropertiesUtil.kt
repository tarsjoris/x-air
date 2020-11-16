package be.t_ars.xtouch.util

import java.util.*

fun Properties.getBoolean(key: String) =
	this.getProperty(key, "false") == "true"