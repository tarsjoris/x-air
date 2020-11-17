package be.t_ars.xtouch.osc

interface IOSCListener {
	fun lrMixOn(on: Boolean) {}
	fun busMixOn(bus: Int, on: Boolean) {}
	fun channelName(channel: Int, name: String) {}
	fun channelColor(channel: Int, color: Int) {}
	fun busName(bus: Int, name: String) {}
	fun busColor(bus: Int, color: Int) {}
}