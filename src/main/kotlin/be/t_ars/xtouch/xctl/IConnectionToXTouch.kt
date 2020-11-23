package be.t_ars.xtouch.xctl

interface IConnectionToXTouch : IXR18Events {
	fun sendHeartbeat()
}