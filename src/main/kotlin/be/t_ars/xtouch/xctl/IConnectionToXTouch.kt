package be.t_ars.xtouch.xctl

interface IConnectionToXTouch : IXR18Events {
	fun sendHeartbeat()

	fun setChannelButtonLED(channel: Int, channelButton: EChannelButton, mode: ELEDMode) {
		setButtonLEDs(arrayOf(ChannelButtonLEDEvent(channel, channelButton, mode)))
	}

	fun setButtonLED(button: EButton, mode: ELEDMode) {
		setButtonLEDs(arrayOf(ButtonLEDEvent(button, mode)))
	}

	fun setLEDRingSingle(channel: Int, index: Int?) {
		if (index == null || index in 0..12) {
			when (index) {
				0 -> setLEDRing(channel, 0x01.toByte(), 0x00.toByte())
				1 -> setLEDRing(channel, 0x02.toByte(), 0x00.toByte())
				2 -> setLEDRing(channel, 0x04.toByte(), 0x00.toByte())
				3 -> setLEDRing(channel, 0x08.toByte(), 0x00.toByte())
				4 -> setLEDRing(channel, 0x10.toByte(), 0x00.toByte())
				5 -> setLEDRing(channel, 0x20.toByte(), 0x00.toByte())
				6 -> setLEDRing(channel, 0x40.toByte(), 0x00.toByte())
				7 -> setLEDRing(channel, 0x00.toByte(), 0x01.toByte())
				8 -> setLEDRing(channel, 0x00.toByte(), 0x02.toByte())
				9 -> setLEDRing(channel, 0x00.toByte(), 0x04.toByte())
				10 -> setLEDRing(channel, 0x00.toByte(), 0x08.toByte())
				11 -> setLEDRing(channel, 0x00.toByte(), 0x10.toByte())
				12 -> setLEDRing(channel, 0x00.toByte(), 0x20.toByte())
				else -> setLEDRing(channel, 0x00.toByte(), 0x00.toByte())
			}
		}
	}

	fun setLEDRingWithHalves(channel: Int, index: Int?) {
		if (index == null || index in 0..24) {
			when (index) {
				0 -> setLEDRing(channel, 0x01.toByte(), 0x00.toByte())
				1 -> setLEDRing(channel, 0x03.toByte(), 0x00.toByte())
				2 -> setLEDRing(channel, 0x02.toByte(), 0x00.toByte())
				3 -> setLEDRing(channel, 0x06.toByte(), 0x00.toByte())
				4 -> setLEDRing(channel, 0x04.toByte(), 0x00.toByte())
				5 -> setLEDRing(channel, 0x0C.toByte(), 0x00.toByte())
				6 -> setLEDRing(channel, 0x08.toByte(), 0x00.toByte())
				7 -> setLEDRing(channel, 0x18.toByte(), 0x00.toByte())
				8 -> setLEDRing(channel, 0x10.toByte(), 0x00.toByte())
				9 -> setLEDRing(channel, 0x30.toByte(), 0x00.toByte())
				10 -> setLEDRing(channel, 0x20.toByte(), 0x00.toByte())
				11 -> setLEDRing(channel, 0x60.toByte(), 0x00.toByte())
				12 -> setLEDRing(channel, 0x40.toByte(), 0x00.toByte())
				13 -> setLEDRing(channel, 0x40.toByte(), 0x01.toByte())
				14 -> setLEDRing(channel, 0x00.toByte(), 0x01.toByte())
				15 -> setLEDRing(channel, 0x00.toByte(), 0x03.toByte())
				16 -> setLEDRing(channel, 0x00.toByte(), 0x02.toByte())
				17 -> setLEDRing(channel, 0x00.toByte(), 0x06.toByte())
				18 -> setLEDRing(channel, 0x00.toByte(), 0x04.toByte())
				19 -> setLEDRing(channel, 0x00.toByte(), 0x0C.toByte())
				20 -> setLEDRing(channel, 0x00.toByte(), 0x08.toByte())
				21 -> setLEDRing(channel, 0x00.toByte(), 0x18.toByte())
				22 -> setLEDRing(channel, 0x00.toByte(), 0x10.toByte())
				23 -> setLEDRing(channel, 0x00.toByte(), 0x30.toByte())
				24 -> setLEDRing(channel, 0x00.toByte(), 0x20.toByte())
				else -> setLEDRing(channel, 0x00.toByte(), 0x00.toByte())
			}
		}
	}

	fun setLEDRingContinuous(channel: Int, index: Int?) {
		if (index == null || index in 0..12) {
			when (index) {
				0 -> setLEDRing(channel, 0x01.toByte(), 0x00.toByte())
				1 -> setLEDRing(channel, 0x03.toByte(), 0x00.toByte())
				2 -> setLEDRing(channel, 0x07.toByte(), 0x00.toByte())
				3 -> setLEDRing(channel, 0x0F.toByte(), 0x00.toByte())
				4 -> setLEDRing(channel, 0x1F.toByte(), 0x00.toByte())
				5 -> setLEDRing(channel, 0x3F.toByte(), 0x00.toByte())
				6 -> setLEDRing(channel, 0x7F.toByte(), 0x00.toByte())
				7 -> setLEDRing(channel, 0x7F.toByte(), 0x01.toByte())
				8 -> setLEDRing(channel, 0x7F.toByte(), 0x03.toByte())
				9 -> setLEDRing(channel, 0x7F.toByte(), 0x07.toByte())
				10 -> setLEDRing(channel, 0x7F.toByte(), 0x0F.toByte())
				11 -> setLEDRing(channel, 0x7F.toByte(), 0x1F.toByte())
				12 -> setLEDRing(channel, 0x7F.toByte(), 0x3F.toByte())
				else -> setLEDRing(channel, 0x00.toByte(), 0x00.toByte())
			}
		}
	}

	fun setLEDRingLeftRight(channel: Int, index: Int?) {
		if (index == null || index in -6..6) {
			when (index) {
				-6 -> setLEDRing(channel, 0x7F.toByte(), 0x00.toByte())
				-5 -> setLEDRing(channel, 0x7E.toByte(), 0x00.toByte())
				-4 -> setLEDRing(channel, 0x7C.toByte(), 0x00.toByte())
				-3 -> setLEDRing(channel, 0x78.toByte(), 0x00.toByte())
				-2 -> setLEDRing(channel, 0x70.toByte(), 0x00.toByte())
				-1 -> setLEDRing(channel, 0x60.toByte(), 0x00.toByte())
				0 -> setLEDRing(channel, 0x40.toByte(), 0x00.toByte())
				1 -> setLEDRing(channel, 0x40.toByte(), 0x01.toByte())
				2 -> setLEDRing(channel, 0x40.toByte(), 0x03.toByte())
				3 -> setLEDRing(channel, 0x40.toByte(), 0x07.toByte())
				4 -> setLEDRing(channel, 0x40.toByte(), 0x0F.toByte())
				5 -> setLEDRing(channel, 0x40.toByte(), 0x1F.toByte())
				6 -> setLEDRing(channel, 0x40.toByte(), 0x3F.toByte())
				else -> setLEDRing(channel, 0x00.toByte(), 0x00.toByte())
			}
		}
	}

	fun setLEDRing(channel: Int, left: Byte, right: Byte) {
		XctlUtil.validateChannel(channel)
		setLEDs(arrayOf(LEDRingEvent(channel, true, left), LEDRingEvent(channel, false, right)))
	}

	fun setLEDRingRaw(channel: Int, left: Boolean, data: Byte) {
		setLEDs(arrayOf(LEDRingEvent(channel, left, data)))
	}

	fun setDigits(number: Int) {
		setLEDs(
			arrayOf(
				DigitEvent(0, if (number > 9) DIGITS[number.div(10).rem(10)] else 0x00.toByte()),
				DigitEvent(1, DIGITS[number.rem(10)]),
			)
		)
	}

	fun setDigitRaw(index: Int, data: Byte) {
		setLEDs(arrayOf(DigitEvent(index, data)))
	}

	fun setMeter(channel: Int, value: Int)

	fun setChannelFaderPosition(channel: Int, position: Int) {
		setFaderPositions(arrayOf(ChannelFaderEvent(channel, position)))
	}

	fun setMainFaderPosition(position: Int) {
		setFaderPositions(arrayOf(MainFaderEvent(position)))
	}

	fun setScribbleTrip(
		channel: Int,
		color: EScribbleColor,
		secondLineInverted: Boolean,
		line1: String,
		line2: String
	) {
		setScribbleTrip(ScribbleStripEvent(channel, color, secondLineInverted, line1, line2))
	}

	fun setScribbleTrip(event: ScribbleStripEvent) {
		setScribbleTrips(arrayOf(event))
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