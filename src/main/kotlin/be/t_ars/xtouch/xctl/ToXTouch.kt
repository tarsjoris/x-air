package be.t_ars.xtouch.xctl

internal class ToXTouch(private val sendPayload: (ByteArray) -> Unit) : IConnectionToXTouch {
	override fun sendHeartbeat() {
		sendPayload(XctlUtil.XR18_HEARTBEAT_PAYLOAD)
	}

	override fun setButtonLEDs(buttonLEDEvents: Array<AbstractButtonLEDEvent>) {
		sendPayload(ByteArray(buttonLEDEvents.size * 2 + 1) { index ->
			if (index == 0) {
				0x90.toByte()
			} else {
				val event = buttonLEDEvents[(index - 1).div(2)]
				if (index.rem(2) == 1) {
					when (event) {
						is ChannelButtonLEDEvent -> getChannelButtonByte(event.channel, event.channelButton)
						is ButtonLEDEvent -> getButtonByte(event.button)
						is UnusedButtonLEDEvent -> event.id
					}
				} else {
					when (event.mode) {
						ELEDMode.OFF -> 0x00.toByte()
						ELEDMode.FLASH -> 0x01.toByte()
						ELEDMode.ON -> 0x02.toByte()
					}
				}
			}
		})
	}

	private fun getChannelButtonByte(channel: Int, channelButton: EChannelButton) =
		(when (channelButton) {
			EChannelButton.REC -> 0x00
			EChannelButton.SOLO -> 0x08
			EChannelButton.MUTE -> 0x10
			EChannelButton.SELECT -> 0x18
		} + channel - 1).toByte()

	private fun getButtonByte(button: EButton) =
		when (button) {
			EButton.ENCODER_TRACK -> 0x28.toByte()
			EButton.ENCODER_SEND -> 0x29.toByte()
			EButton.ENCODER_PAN -> 0x2A.toByte()
			EButton.ENCODER_PLUGIN -> 0x2B.toByte()
			EButton.ENCODER_EQ -> 0x2C.toByte()
			EButton.ENCODER_INST -> 0x2D.toByte()
			EButton.PREV_BANK -> 0x2E.toByte()
			EButton.NEXT_BANK -> 0x2F.toByte()
			EButton.PREV_CHANNEL -> 0x30.toByte()
			EButton.NEXT_CHANNEL -> 0x31.toByte()
			EButton.FLIP -> 0x32.toByte()
			EButton.GLOBAL_VIEW -> 0x33.toByte()
			EButton.F1 -> 0x36.toByte()
			EButton.F2 -> 0x37.toByte()
			EButton.F3 -> 0x38.toByte()
			EButton.F4 -> 0x39.toByte()
			EButton.F5 -> 0x3A.toByte()
			EButton.F6 -> 0x3B.toByte()
			EButton.F7 -> 0x3C.toByte()
			EButton.F8 -> 0x3D.toByte()
			EButton.MIDI_TRACKS -> 0x3E.toByte()
			EButton.INPUTS -> 0x3F.toByte()
			EButton.AUDIO_TRACKS -> 0x40.toByte()
			EButton.AUDIO_INST -> 0x41.toByte()
			EButton.AUX -> 0x42.toByte()
			EButton.BUSES -> 0x43.toByte()
			EButton.OUTPUTS -> 0x44.toByte()
			EButton.USER -> 0x45.toByte()
			EButton.MODIFY_SHIFT -> 0x46.toByte()
			EButton.MODIFY_OPTION -> 0x47.toByte()
			EButton.MODIFY_CONTROL -> 0x48.toByte()
			EButton.MODIFY_ALT -> 0x49.toByte()
			EButton.AUTOMATION_READ -> 0x4A.toByte()
			EButton.AUTOMATION_WRITE -> 0x4B.toByte()
			EButton.AUTOMATION_TRIM -> 0x4C.toByte()
			EButton.AUTOMATION_TOUCH -> 0x4D.toByte()
			EButton.AUTOMATION_LATCH -> 0x4E.toByte()
			EButton.AUTOMATION_GROUP -> 0x4F.toByte()
			EButton.UTILITY_SAVE -> 0x50.toByte()
			EButton.UTILITY_UNDO -> 0x51.toByte()
			EButton.UTILITY_CANCEL -> 0x52.toByte()
			EButton.UTILITY_ENTER -> 0x53.toByte()
			EButton.SOLO -> 0x73.toByte()
		}

	override fun setLEDs(ledEvents: Array<AbstractLEDEvent>) {
		sendPayload(ByteArray(ledEvents.size * 2 + 1) { index ->
			if (index == 0) {
				0xB0.toByte()
			} else {
				val event = ledEvents[(index - 1).div(2)]
				if (index.rem(2) == 1) {
					when (event) {
						is LEDRingEvent ->
							((if (event.left) 0x30 else 0x38) + event.channel - 1).toByte()
						is DigitEvent ->
							(0x60 + event.index - 1).toByte()
					}
				} else {
					event.data
				}
			}
		})
	}

	override fun setMeter(channel: Int, value: Int) {
		setMeters(arrayOf(Meter(channel, value)))
	}

	override fun setMeters(meters: Array<Meter>) {
		sendPayload(byteArrayOf(0xD0.toByte()) + ByteArray(meters.size) { i ->
			((meters[i].channel - 1) * 16 + meters[i].value).toByte()
		})
	}

	override fun setFaderPositions(faderEvents: Array<AbstractFaderEvent>) {
		sendPayload(ByteArray(faderEvents.size * 3) { index ->
			val event = faderEvents[index.div(3)]
			when (index.rem(3)) {
				0 -> when (event) {
					is ChannelFaderEvent -> (0xE0 + event.channel - 1).toByte()
					is MainFaderEvent -> 0xE8.toByte()
				}
				1 -> event.position.rem(128).toByte()
				else -> event.position.div(128).toByte()
			}
		})
	}

	override fun setScribbleTrips(scribbleStripEvents: Array<ScribbleStripEvent>) {
		sendPayload(ByteArray(scribbleStripEvents.size * 22) { index ->
			val event = scribbleStripEvents[index.div(22)]
			when (val offset = index.rem(22)) {
				0 -> 0xF0.toByte()
				1 -> 0x00.toByte()
				2 -> 0x00.toByte()
				3 -> 0x66.toByte()
				4 -> 0x58.toByte()
				5 -> (0x20 + event.channel - 1).toByte()
				6 ->
					(event.color.ordinal + if (event.secondLineInverted) 0x40 else 0x00).toByte()
				in 7..13 -> {
					val charIndex = offset - 7
					if (charIndex < event.line1.length) {
						event.line1[charIndex].toByte()
					} else {
						0x00.toByte()
					}
				}
				in 14..20 -> {
					val charIndex = offset - 14
					if (charIndex < event.line2.length) {
						event.line2[charIndex].toByte()
					} else {
						0x00.toByte()
					}
				}
				else -> 0xF7.toByte()
			}
		})
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