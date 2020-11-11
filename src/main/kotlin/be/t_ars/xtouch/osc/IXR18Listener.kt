package be.t_ars.xtouch.osc

interface IXR18Listener {
	fun lrMixOn(on: Boolean) {}
	fun busMixOn(bus: Int, on: Boolean) {}
}