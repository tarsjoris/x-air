package be.t_ars.xtouch.xctl

import be.t_ars.xtouch.util.matchesData

internal typealias Event<ListenerType> = (ListenerType) -> Unit

internal typealias EventProcessor<ListenerType> = (Event<ListenerType>) -> Unit

class XctlUtil private constructor() {
	companion object {
		val CHANNEL_COUNT = 8
		val FADER_POSIION_RANGE = 0..16380

		fun toFaderPercentage(position: Int) =
			position.toFloat()
				.div(FADER_POSIION_RANGE.last)

		internal const val DEBUG = false

		internal const val PORT = 10111
		internal const val HEARTBEAT_INTERVAL = 6_000L
		internal const val HEARTBEAT_TIMEOUT = 8_000L

		internal val XTOUCH_HEARTBEAT_PAYLOAD =
			byteArrayOf(0xF0.toByte(), 0x00, 0x20, 0x32, 0x58, 0x54, 0x00, 0xF7.toByte())

		internal fun isXTouchHeartbeat(packet: ByteArray) =
			matchesData(
				packet,
				XTOUCH_HEARTBEAT_PAYLOAD
			)

		internal val XR18_HEARTBEAT_PAYLOAD =
			byteArrayOf(0xF0.toByte(), 0x00, 0x00, 0x66, 0x14, 0x00, 0XF7.toByte())

		internal fun isXR18Heartbeat(packet: ByteArray) =
			matchesData(
				packet,
				XR18_HEARTBEAT_PAYLOAD
			)

		internal fun validateChannel(channel: Int) =
			assert(channel in 1..8)

		internal fun validateFunction(function: Int) =
			assert(function in 1..8)

		internal fun validateModify(modify: Int) =
			assert(modify in 1..4)

		internal fun validateAutomation(automation: Int) =
			assert(automation in 1..6)

		internal fun validateUtility(utility: Int) =
			assert(utility in 1..4)

		internal fun validateFaderPosition(position: Int) =
			assert(position in FADER_POSIION_RANGE)
	}
}