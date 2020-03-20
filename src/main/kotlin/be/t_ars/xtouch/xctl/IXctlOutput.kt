package be.t_ars.xtouch.xctl

interface IXctlOutput {
	fun setLEDRing(channel: Int, index: Int?)
	fun setLEDRingWithHalves(channel: Int, index: Int?)
	fun setLEDRingContinuous(channel: Int, index: Int?)
	fun setLEDRingLeftRight(channel: Int, index: Int?)
	fun setLEDRingRaw(channel: Int, left: Byte, right: Byte)
	fun setMeter(channel: Int, value: Int)
	fun setMeters(values: IntArray)
	fun setFaderPosition(channel: Int, position: Float)
	fun setMainFaderPosition(position: Float)
	fun setDigits(number: Int)
}