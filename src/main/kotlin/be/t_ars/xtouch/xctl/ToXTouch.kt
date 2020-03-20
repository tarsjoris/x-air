package be.t_ars.xtouch.xctl

internal class ToXTouch(private val sendPayload: (ByteArray) -> Unit) : IXctlOutput {
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
		if (channel in 1..8 && position >= 0 && position <= 1) {
//			in 0xE0.toByte()..0xE8.toByte() -> processFaderMove(packet)
//			val channel = packet.data[packet.offset] - 0xE0.toByte() + 1
//			val position = (packet.data[packet.offset + 2] * 128 + packet.data[packet.offset + 1])
//				.toFloat()
//				.div(16380)
			val value = (position * 16380F).toInt()
			sendPayload(byteArrayOf((0xE0 + channel - 1).toByte(), value.rem(128).toByte(), value.div(128).toByte()))
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