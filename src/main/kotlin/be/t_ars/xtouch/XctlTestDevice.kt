package be.t_ars.xtouch

import be.t_ars.xtouch.xctl.IXTouchListener
import be.t_ars.xtouch.xctl.XctlConnection

private class Listener(val connection: XctlConnection) : IXTouchListener {
	override fun channelSelectPressed(channel: Int) {
		connection.setDigits(channel)
		connection.setMeters(IntArray(8) { i ->
			if (i + 1 == channel) {
				8
			} else {
				0
			}
		})
	}

	override fun faderMoved(channel: Int, position: Float) {
		println("Fader $channel at pos $position")
		connection.setLEDRing(channel, (position * 13).toInt() - 6)
	}

	override fun mainFaderMoved(position: Float) {
		println("Main Fader at pos $position")
	}
}

fun main() {
	val connection = XctlConnection()
	connection.addXTouchListener(Listener(connection))
	connection.run()
}