package be.t_ars.xtouch.demo

import be.t_ars.xtouch.xctl.IXTouchListener
import be.t_ars.xtouch.xctl.XctlConnectionProxy
import java.net.Inet4Address

private class DebugListener : IXTouchListener {
	override fun channelRecPressedDown(channel: Int) {
		println("Channel $channel rec")
	}

	override fun channelSoloPressedDown(channel: Int) {
		println("Channel $channel solo")
	}

	override fun channelSelectPressedDown(channel: Int) {
		println("Channel $channel select")
	}

	override fun channelMutePressedDown(channel: Int) {
		println("Channel $channel mute")
	}
}

fun main() {
	val xairAddress = Inet4Address.getByName("192.168.0.238")
	val connection = XctlConnectionProxy(xairAddress)
	connection.addXTouchListener(DebugListener())
	connection.run()
}