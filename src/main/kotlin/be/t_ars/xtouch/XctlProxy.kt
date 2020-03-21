package be.t_ars.xtouch

import be.t_ars.xtouch.xctl.IXTouchListener
import be.t_ars.xtouch.xctl.IXctlConnection
import be.t_ars.xtouch.xctl.XctlConnectionImpl
import java.net.Inet4Address

private class DebugListener : IXTouchListener {
	override suspend fun channelRecPressed(channel: Int) {
		println("Channel $channel rec")
	}

	override suspend fun channelSoloPressed(channel: Int) {
		println("Channel $channel solo")
	}

	override suspend fun channelSelectPressed(channel: Int) {
		println("Channel $channel select")
	}

	override suspend fun channelMutePressed(channel: Int) {
		println("Channel $channel mute")
	}
}

fun main() {
	val xairAddress = Inet4Address.getByName("192.168.0.238")
	val connection: IXctlConnection = XctlConnectionImpl(xairAddress)
	connection.addXTouchListener(DebugListener())
	connection.run()
}