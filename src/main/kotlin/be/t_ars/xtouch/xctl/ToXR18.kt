package be.t_ars.xtouch.xctl

internal class ToXR18(private val sendPayload: (ByteArray) -> Unit) : IConnectionToXR18 {
	override fun sendHeartbeat() {
		sendPayload(XTOUCH_HEARTBEAT_PAYLOAD)
	}

	override fun channelRecPressed(channel: Int, down: Boolean) {
		validateChannel(channel)
		sendPayload(byteArrayOf(0x90.toByte(), (channel - 1 + 0x00).toByte(), (if (down) 0x7F else 0x00).toByte()))
	}

	override fun channelSoloPressed(channel: Int, down: Boolean) {
		validateChannel(channel)
		sendPayload(byteArrayOf(0x90.toByte(), (channel - 1 + 0x08).toByte(), (if (down) 0x7F else 0x00).toByte()))
	}

	override fun channelMutePressed(channel: Int, down: Boolean) {
		validateChannel(channel)
		sendPayload(byteArrayOf(0x90.toByte(), (channel - 1 + 0x10).toByte(), (if (down) 0x7F else 0x00).toByte()))
	}

	override fun channelSelectPressed(channel: Int, down: Boolean) {
		validateChannel(channel)
		sendPayload(byteArrayOf(0x90.toByte(), (channel - 1 + 0x18).toByte(), (if (down) 0x7F else 0x00).toByte()))
	}

	override fun knobPressed(knob: Int, down: Boolean) {
		validateChannel(knob)
		sendPayload(byteArrayOf(0x90.toByte(), (knob - 1 + 0x20).toByte(), (if (down) 0x7F else 0x00).toByte()))
	}

	override fun encoderTrackPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x28.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun encoderSendPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x29.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun encoderPanPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x2A.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun encoderPluginPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x2B.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun encoderEqPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x2C.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun encoderInstPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x2D.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun previousBankPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x2E.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun nextBankPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x2F.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun previousChannelPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x30.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun nextChannelPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x31.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun flipPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x32.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun globalViewPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x33.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun displayPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x34.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun smptePressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x35.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun functionPressed(function: Int, down: Boolean) {
		validateFunction(function)
		sendPayload(byteArrayOf(0x90.toByte(), (function - 1 + 0x36).toByte(), (if (down) 0x7F else 0x00).toByte()))
	}

	override fun midiTracksPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x3E.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun inputsPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x3F.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun audioTracksPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x40.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun audioInstPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x41.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun auxPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x42.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun busesPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x43.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun outputsPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x44.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun userPressed(down: Boolean) =
		sendPayload(byteArrayOf(0x90.toByte(), 0x45.toByte(), (if (down) 0x7F else 0x00).toByte()))

	override fun modifyPressed(modify: Int, down: Boolean) {
		validateModify(modify)
		sendPayload(byteArrayOf(0x90.toByte(), (modify - 1 + 0x46).toByte(), (if (down) 0x7F else 0x00).toByte()))
	}

	override fun automationPressed(automation: Int, down: Boolean) {
		validateAutomation(automation)
		sendPayload(byteArrayOf(0x90.toByte(), (automation - 1 + 0x4A).toByte(), (if (down) 0x7F else 0x00).toByte()))
	}

	override fun utiliyPressed(utility: Int, down: Boolean) {
		validateUtility(utility)
		sendPayload(byteArrayOf(0x90.toByte(), (utility - 1 + 0x50).toByte(), (if (down) 0x7F else 0x00).toByte()))
	}

	override fun faderPressed(channel: Int, down: Boolean) {
		validateChannel(channel)
		sendPayload(byteArrayOf(0x90.toByte(), (channel - 1 + 0x68).toByte(), (if (down) 0x7F else 0x00).toByte()))
	}

	override fun mainFaderPressed(down: Boolean) {
		sendPayload(byteArrayOf(0x90.toByte(), 0x70.toByte(), (if (down) 0x7F else 0x00).toByte()))
	}

	override fun knobRotated(knob: Int, right: Boolean) {
		validateChannel(knob)
		sendPayload(byteArrayOf(0xB0.toByte(), (knob - 1 + 0x10).toByte(), if (right) 0x01.toByte() else 0x41.toByte()))
	}

	override fun faderMoved(channel: Int, position: Int) {
		validateChannel(channel)
		faderMovedInternal(channel, position)
	}

	override fun mainFaderMoved(position: Int) {
		faderMovedInternal(9, position)
	}

	private fun faderMovedInternal(channel: Int, position: Int) {
		validateFaderPosition(position)
		sendPayload(byteArrayOf((channel - 1 + 0xE0).toByte(), position.rem(128).toByte(), position.div(128).toByte()))
	}

	override fun systemMessage(message: ByteArray) {
		sendPayload(ByteArray(message.size + 2) { i ->
			when (i) {
				0 -> 0xF0.toByte()
				message.size + 1 -> 0xF7.toByte()
				else -> message[i - 1]
			}
		})
	}
}