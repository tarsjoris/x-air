package be.t_ars.xtouch.xctl

interface IXctlOutput {
	fun setLEDRing(channel: Int, index: Int?)
	fun setLEDRingWithHalves(channel: Int, index: Int?)
	fun setLEDRingContinuous(channel: Int, index: Int?)
	fun setLEDRingLeftRight(channel: Int, index: Int?)
	fun setMeter(channel: Int, value: Int)
	fun setMeters(values: IntArray)
	fun setDigits(number: Int)
}