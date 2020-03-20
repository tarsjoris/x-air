package be.t_ars.xtouch

import be.t_ars.xtouch.xctl.IXTouchListener
import be.t_ars.xtouch.xctl.IXctlConnection
import be.t_ars.xtouch.xctl.IXctlOutput
import be.t_ars.xtouch.xctl.XctlConnectionImpl

private class Listener(val output: IXctlOutput) : IXTouchListener {
	override fun channelSelectPressed(channel: Int) {
		output.setDigits(channel)
		output.setMeter(channel, 8)
	}

	override fun faderMoved(channel: Int, position: Float) {
		when (channel) {
			in 1..2 -> output.setLEDRing(channel, (position * 12).toInt())
			in 3..4 -> output.setLEDRingWithHalves(channel, (position * 24).toInt())
			in 5..6 -> output.setLEDRingContinuous(channel, (position * 12).toInt())
			in 7..8 -> output.setLEDRingLeftRight(channel, (position * 12).toInt() - 6)
		}
	}

	override fun mainFaderMoved(position: Float) {
		output.setFaderPosition(1, position)
	}
}

fun main() {
	val connection: IXctlConnection = XctlConnectionImpl()
	connection.addXTouchListener(Listener(connection.getOutput()))
	connection.run()
}