package be.t_ars.xtouch.xctl

interface IXR18Listener : IXR18Events {
	fun setChannelButtonLED(channel: Int, channelButton: EChannelButton, mode: ELEDMode) {}
	fun setButtonLED(button: EButton, mode: ELEDMode) {}
	override fun setButtonLEDs(buttonLEDEvents: Array<AbstractButtonLEDEvent>) {
		buttonLEDEvents.forEach { event ->
			when (event) {
				is ChannelButtonLEDEvent -> setChannelButtonLED(event.channel, event.channelButton, event.mode)
				is ButtonLEDEvent -> setButtonLED(event.button, event.mode)
			}
		}
	}

	fun setLEDRingRaw(channel: Int, left: Boolean, data: Byte) {}
	fun setDigitRaw(index: Int, data: Byte) {}
	override fun setLEDs(ledEvents: Array<AbstractLEDEvent>) {
		ledEvents.forEach { event ->
			when (event) {
				is LEDRingEvent -> setLEDRingRaw(event.channel, event.left, event.data)
				is DigitEvent -> setDigitRaw(event.index, event.data)
			}
		}
	}

	override fun setMeters(meters: Array<Meter>) {}

	fun setChannelFaderPosition(channel: Int, position: Int) {}
	fun setMainFaderPosition(position: Int) {}
	override fun setFaderPositions(faderEvents: Array<AbstractFaderEvent>) {
		faderEvents.forEach { event ->
			when (event) {
				is ChannelFaderEvent -> setChannelFaderPosition(event.channel, event.position)
				is MainFaderEvent -> setMainFaderPosition(event.position)
			}
		}
	}

	fun setScribbleTrip(
		channel: Int,
		color: EScribbleColor,
		secondLineInverted: Boolean,
		line1: String,
		line2: String
	) {
	}

	override fun setScribbleTrips(scribbleStripEvents: Array<ScribbleStripEvent>) {
		scribbleStripEvents.forEach { event ->
			setScribbleTrip(event.channel, event.color, event.secondLineInverted, event.line1, event.line2)
		}
	}

	override fun systemMessage(message: ByteArray) {}
}