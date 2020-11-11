package be.t_ars.xtouch.xctl

interface IXctlConnection {
	fun addConnectionListener(listener: IXctlConnectionListener)
	fun addXTouchListener(listener: IXTouchListener)
	fun getOutput(): IXctlOutput
}