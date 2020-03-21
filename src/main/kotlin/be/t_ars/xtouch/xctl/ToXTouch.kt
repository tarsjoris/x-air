package be.t_ars.xtouch.xctl

internal class ToXTouch(private val sendPayload: (ByteArray) -> Unit) : IXctlOutput {
	override fun setChannelButtonLED(
		channel: Int,
		channelButton: IXctlOutput.EChannelButton,
		mode: IXctlOutput.ELEDMode
	) {
		sendPayload(
			byteArrayOf(
				0x90.toByte(),
				(when (channelButton) {
					IXctlOutput.EChannelButton.REC -> 0x00
					IXctlOutput.EChannelButton.SOLO -> 0x08
					IXctlOutput.EChannelButton.MUTE -> 0x10
					IXctlOutput.EChannelButton.SELECT -> 0x18
				} + channel - 1).toByte(),
				when (mode) {
					IXctlOutput.ELEDMode.OFF -> 0x00.toByte()
					IXctlOutput.ELEDMode.FLASH -> 0x01.toByte()
					IXctlOutput.ELEDMode.ON -> 0x7F.toByte()
				}
			)
		)
	}

	override fun setButtonLED(button: IXctlOutput.EButton, mode: IXctlOutput.ELEDMode) {
		sendPayload(
			byteArrayOf(
				0x90.toByte(),
				when (button) {
					IXctlOutput.EButton.TRACK -> 0x28.toByte()
					IXctlOutput.EButton.SEND -> 0x29.toByte()
					IXctlOutput.EButton.PAN -> 0x2A.toByte()
					IXctlOutput.EButton.PLUGIN -> 0x2B.toByte()
					IXctlOutput.EButton.EQ -> 0x2C.toByte()
					IXctlOutput.EButton.INST -> 0x2D.toByte()
					IXctlOutput.EButton.PREV_BANK -> 0x2E.toByte()
					IXctlOutput.EButton.NEXT_BANK -> 0x2F.toByte()
					IXctlOutput.EButton.PREV_CHANNEL -> 0x30.toByte()
					IXctlOutput.EButton.NEXT_CHANNEL -> 0x31.toByte()
					IXctlOutput.EButton.FLIP -> 0x32.toByte()
					IXctlOutput.EButton.GLOBAL_VIEW -> 0x33.toByte()
					IXctlOutput.EButton.F1 -> 0x36.toByte()
					IXctlOutput.EButton.F2 -> 0x37.toByte()
					IXctlOutput.EButton.F3 -> 0x38.toByte()
					IXctlOutput.EButton.F4 -> 0x39.toByte()
					IXctlOutput.EButton.F5 -> 0x3A.toByte()
					IXctlOutput.EButton.F6 -> 0x3B.toByte()
					IXctlOutput.EButton.F7 -> 0x3C.toByte()
					IXctlOutput.EButton.F8 -> 0x3D.toByte()
					IXctlOutput.EButton.MIDI_TRACKS -> 0x3E.toByte()
					IXctlOutput.EButton.INPUTS -> 0x3F.toByte()
					IXctlOutput.EButton.AUDIO_TRACKS -> 0x40.toByte()
					IXctlOutput.EButton.AUDIO_INST -> 0x41.toByte()
					IXctlOutput.EButton.AUX -> 0x42.toByte()
					IXctlOutput.EButton.BUSES -> 0x43.toByte()
					IXctlOutput.EButton.OUTPUTS -> 0x44.toByte()
					IXctlOutput.EButton.USER -> 0x45.toByte()
					IXctlOutput.EButton.MODIFY_SHIFT -> 0x46.toByte()
					IXctlOutput.EButton.MODIFY_OPTION -> 0x47.toByte()
					IXctlOutput.EButton.MODIFY_CONTROL -> 0x48.toByte()
					IXctlOutput.EButton.MODIFY_ALT -> 0x49.toByte()
					IXctlOutput.EButton.AUTOMATION_READ -> 0x4A.toByte()
					IXctlOutput.EButton.AUTOMATION_WRITE -> 0x4B.toByte()
					IXctlOutput.EButton.AUTOMATION_TRIM -> 0x4C.toByte()
					IXctlOutput.EButton.AUTOMATION_TOUCH -> 0x4D.toByte()
					IXctlOutput.EButton.AUTOMATION_LATCH -> 0x4E.toByte()
					IXctlOutput.EButton.AUTOMATION_GROUP -> 0x4F.toByte()
					IXctlOutput.EButton.UTILITY_SAVE -> 0x50.toByte()
					IXctlOutput.EButton.UTILITY_UNDO -> 0x51.toByte()
					IXctlOutput.EButton.UTILITY_CANCEL -> 0x52.toByte()
					IXctlOutput.EButton.UTILITY_ENTER -> 0x53.toByte()
					IXctlOutput.EButton.SOLO -> 0x73.toByte()
				},
				when (mode) {
					IXctlOutput.ELEDMode.OFF -> 0x00.toByte()
					IXctlOutput.ELEDMode.FLASH -> 0x01.toByte()
					IXctlOutput.ELEDMode.ON -> 0x7F.toByte()
				}
			)
		)
	}

	override fun setLEDRing(channel: Int, index: Int?) {
		if (index == null || index in 0..12) {
			when (index) {
				0 -> setLEDRingRaw(channel, 0x01.toByte(), 0x00.toByte())
				1 -> setLEDRingRaw(channel, 0x02.toByte(), 0x00.toByte())
				2 -> setLEDRingRaw(channel, 0x04.toByte(), 0x00.toByte())
				3 -> setLEDRingRaw(channel, 0x08.toByte(), 0x00.toByte())
				4 -> setLEDRingRaw(channel, 0x10.toByte(), 0x00.toByte())
				5 -> setLEDRingRaw(channel, 0x20.toByte(), 0x00.toByte())
				6 -> setLEDRingRaw(channel, 0x40.toByte(), 0x00.toByte())
				7 -> setLEDRingRaw(channel, 0x00.toByte(), 0x01.toByte())
				8 -> setLEDRingRaw(channel, 0x00.toByte(), 0x02.toByte())
				9 -> setLEDRingRaw(channel, 0x00.toByte(), 0x04.toByte())
				10 -> setLEDRingRaw(channel, 0x00.toByte(), 0x08.toByte())
				11 -> setLEDRingRaw(channel, 0x00.toByte(), 0x10.toByte())
				12 -> setLEDRingRaw(channel, 0x00.toByte(), 0x20.toByte())
				else -> setLEDRingRaw(channel, 0x00.toByte(), 0x00.toByte())
			}
		}
	}

	override fun setLEDRingWithHalves(channel: Int, index: Int?) {
		if (index == null || index in 0..24) {
			when (index) {
				0 -> setLEDRingRaw(channel, 0x01.toByte(), 0x00.toByte())
				1 -> setLEDRingRaw(channel, 0x03.toByte(), 0x00.toByte())
				2 -> setLEDRingRaw(channel, 0x02.toByte(), 0x00.toByte())
				3 -> setLEDRingRaw(channel, 0x06.toByte(), 0x00.toByte())
				4 -> setLEDRingRaw(channel, 0x04.toByte(), 0x00.toByte())
				5 -> setLEDRingRaw(channel, 0x0C.toByte(), 0x00.toByte())
				6 -> setLEDRingRaw(channel, 0x08.toByte(), 0x00.toByte())
				7 -> setLEDRingRaw(channel, 0x18.toByte(), 0x00.toByte())
				8 -> setLEDRingRaw(channel, 0x10.toByte(), 0x00.toByte())
				9 -> setLEDRingRaw(channel, 0x30.toByte(), 0x00.toByte())
				10 -> setLEDRingRaw(channel, 0x20.toByte(), 0x00.toByte())
				11 -> setLEDRingRaw(channel, 0x60.toByte(), 0x00.toByte())
				12 -> setLEDRingRaw(channel, 0x40.toByte(), 0x00.toByte())
				13 -> setLEDRingRaw(channel, 0x40.toByte(), 0x01.toByte())
				14 -> setLEDRingRaw(channel, 0x00.toByte(), 0x01.toByte())
				15 -> setLEDRingRaw(channel, 0x00.toByte(), 0x03.toByte())
				16 -> setLEDRingRaw(channel, 0x00.toByte(), 0x02.toByte())
				17 -> setLEDRingRaw(channel, 0x00.toByte(), 0x06.toByte())
				18 -> setLEDRingRaw(channel, 0x00.toByte(), 0x04.toByte())
				19 -> setLEDRingRaw(channel, 0x00.toByte(), 0x0C.toByte())
				20 -> setLEDRingRaw(channel, 0x00.toByte(), 0x08.toByte())
				21 -> setLEDRingRaw(channel, 0x00.toByte(), 0x18.toByte())
				22 -> setLEDRingRaw(channel, 0x00.toByte(), 0x10.toByte())
				23 -> setLEDRingRaw(channel, 0x00.toByte(), 0x30.toByte())
				24 -> setLEDRingRaw(channel, 0x00.toByte(), 0x20.toByte())
				else -> setLEDRingRaw(channel, 0x00.toByte(), 0x00.toByte())
			}
		}
	}

	override fun setLEDRingContinuous(channel: Int, index: Int?) {
		if (index == null || index in 0..12) {
			when (index) {
				0 -> setLEDRingRaw(channel, 0x01.toByte(), 0x00.toByte())
				1 -> setLEDRingRaw(channel, 0x03.toByte(), 0x00.toByte())
				2 -> setLEDRingRaw(channel, 0x07.toByte(), 0x00.toByte())
				3 -> setLEDRingRaw(channel, 0x0F.toByte(), 0x00.toByte())
				4 -> setLEDRingRaw(channel, 0x1F.toByte(), 0x00.toByte())
				5 -> setLEDRingRaw(channel, 0x3F.toByte(), 0x00.toByte())
				6 -> setLEDRingRaw(channel, 0x7F.toByte(), 0x00.toByte())
				7 -> setLEDRingRaw(channel, 0x7F.toByte(), 0x01.toByte())
				8 -> setLEDRingRaw(channel, 0x7F.toByte(), 0x03.toByte())
				9 -> setLEDRingRaw(channel, 0x7F.toByte(), 0x07.toByte())
				10 -> setLEDRingRaw(channel, 0x7F.toByte(), 0x0F.toByte())
				11 -> setLEDRingRaw(channel, 0x7F.toByte(), 0x1F.toByte())
				12 -> setLEDRingRaw(channel, 0x7F.toByte(), 0x3F.toByte())
				else -> setLEDRingRaw(channel, 0x00.toByte(), 0x00.toByte())
			}
		}
	}

	override fun setLEDRingLeftRight(channel: Int, index: Int?) {
		if (index == null || index in -6..6) {
			when (index) {
				-6 -> setLEDRingRaw(channel, 0x7F.toByte(), 0x00.toByte())
				-5 -> setLEDRingRaw(channel, 0x7E.toByte(), 0x00.toByte())
				-4 -> setLEDRingRaw(channel, 0x7C.toByte(), 0x00.toByte())
				-3 -> setLEDRingRaw(channel, 0x78.toByte(), 0x00.toByte())
				-2 -> setLEDRingRaw(channel, 0x70.toByte(), 0x00.toByte())
				-1 -> setLEDRingRaw(channel, 0x60.toByte(), 0x00.toByte())
				0 -> setLEDRingRaw(channel, 0x40.toByte(), 0x00.toByte())
				1 -> setLEDRingRaw(channel, 0x40.toByte(), 0x01.toByte())
				2 -> setLEDRingRaw(channel, 0x40.toByte(), 0x03.toByte())
				3 -> setLEDRingRaw(channel, 0x40.toByte(), 0x07.toByte())
				4 -> setLEDRingRaw(channel, 0x40.toByte(), 0x0F.toByte())
				5 -> setLEDRingRaw(channel, 0x40.toByte(), 0x1F.toByte())
				6 -> setLEDRingRaw(channel, 0x40.toByte(), 0x3F.toByte())
				else -> setLEDRingRaw(channel, 0x00.toByte(), 0x00.toByte())
			}
		}
	}

	override fun setLEDRingRaw(channel: Int, left: Byte, right: Byte) {
		if (channel in 1..8) {
			val channelLeft = (0x30 + channel - 1).toByte()
			val channelRight = (0x38 + channel - 1).toByte()
			sendPayload(byteArrayOf(0xB0.toByte(), channelLeft, left, channelRight, right))
		}
	}

	override fun setDigits(number: Int) =
		sendPayload(
			byteArrayOf(
				0xB0.toByte(),
				0x60.toByte(),
				if (number > 9) DIGITS[number.div(10).rem(10)] else 0x00.toByte(),
				0x61.toByte(),
				DIGITS[number.rem(10)]
			)
		)

	override fun setMeter(channel: Int, value: Int) {
		sendPayload(byteArrayOf(0xD0.toByte(), ((channel - 1) * 16 + value).toByte()))
	}

	override fun setMeters(values: IntArray) {
		if (values.size == 8) {
			sendPayload(
				byteArrayOf(0xD0.toByte()) + ByteArray(8) { i ->
					(i * 16 + values[i]).toByte()
				}
			)
		}
	}

	override fun setFaderPosition(channel: Int, position: Float) {
		if (channel in 1..8 && position in 0.0..1.0) {
			val value = (position * 16380F).toInt()
			sendPayload(byteArrayOf((0xE0 + channel - 1).toByte(), value.rem(128).toByte(), value.div(128).toByte()))
		}
	}

	override fun setMainFaderPosition(position: Float) {
		if (position in 0.0..1.0) {
			val value = (position * 16380F).toInt()
			sendPayload(byteArrayOf(0xE8.toByte(), value.rem(128).toByte(), value.div(128).toByte()))
		}
	}

	override fun setScribbleTrip(
		channel: Int,
		color: IXctlOutput.EScribbleColor,
		secondLineInverted: Boolean,
		line1: String,
		line2: String
	) {
		sendPayload(
			byteArrayOf(
				0xF0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x66.toByte(), 0x58.toByte(),
				(0x20 + channel - 1).toByte(),
				(when (color) {
					IXctlOutput.EScribbleColor.RED -> 0x01
					IXctlOutput.EScribbleColor.GREEN -> 0x02
					IXctlOutput.EScribbleColor.YELLOW -> 0x03
					IXctlOutput.EScribbleColor.BLUE -> 0x04
					IXctlOutput.EScribbleColor.PINK -> 0x05
					IXctlOutput.EScribbleColor.CYAN -> 0x06
					IXctlOutput.EScribbleColor.WHITE -> 0x07
				} + if (secondLineInverted) 0x40 else 0x00).toByte()
			) +
					ByteArray(7) { index ->
						if (index < line1.length) {
							line1[index].toByte()
						} else {
							0x20.toByte()
						}
					} +
					ByteArray(7) { index ->
						if (index < line2.length) {
							line2[index].toByte()
						} else {
							0x20.toByte()
						}
					} +
					byteArrayOf(0xF7.toByte())
		)
	}

	companion object {
		private val DIGIT_LINES = byteArrayOf(0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40)
		private val DIGITS = byteArrayOf(
			(DIGIT_LINES[0] + DIGIT_LINES[1] + DIGIT_LINES[2] + DIGIT_LINES[3] + DIGIT_LINES[4] + DIGIT_LINES[5]).toByte(), // 0
			(DIGIT_LINES[1] + DIGIT_LINES[2]).toByte(), // 1
			(DIGIT_LINES[0] + DIGIT_LINES[1] + DIGIT_LINES[3] + DIGIT_LINES[4] + DIGIT_LINES[6]).toByte(), // 2
			(DIGIT_LINES[0] + DIGIT_LINES[1] + DIGIT_LINES[2] + DIGIT_LINES[3] + DIGIT_LINES[6]).toByte(), // 3
			(DIGIT_LINES[1] + DIGIT_LINES[2] + DIGIT_LINES[5] + DIGIT_LINES[6]).toByte(), // 4
			(DIGIT_LINES[0] + DIGIT_LINES[2] + DIGIT_LINES[3] + DIGIT_LINES[5] + DIGIT_LINES[6]).toByte(), // 5
			(DIGIT_LINES[0] + DIGIT_LINES[2] + DIGIT_LINES[3] + DIGIT_LINES[4] + DIGIT_LINES[5] + DIGIT_LINES[6]).toByte(), // 6
			(DIGIT_LINES[0] + DIGIT_LINES[1] + DIGIT_LINES[2]).toByte(), // 7
			(DIGIT_LINES[0] + DIGIT_LINES[1] + DIGIT_LINES[2] + DIGIT_LINES[3] + DIGIT_LINES[4] + DIGIT_LINES[5] + DIGIT_LINES[6]).toByte(), // 8
			(DIGIT_LINES[0] + DIGIT_LINES[1] + DIGIT_LINES[2] + DIGIT_LINES[3] + DIGIT_LINES[5] + DIGIT_LINES[6]).toByte() // 9
		)
	}
}