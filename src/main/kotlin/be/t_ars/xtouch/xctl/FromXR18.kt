package be.t_ars.xtouch.xctl

import be.t_ars.xtouch.util.partial
import be.t_ars.xtouch.util.unprocessedPacket

internal class FromXR18(private val processEvent: EventProcessor<IXR18Events>) {
	fun processPacket(packet: ByteArray) {
		if (packet.isNotEmpty()) {
			when (packet[0]) {
				0x90.toByte() -> processButtonLed(packet)
				0xB0.toByte() -> processLED(packet)
				0xD0.toByte() -> processMeter(packet)
				in 0xE0.toByte()..0xE8.toByte() -> processFaderMove(packet)
				0xF0.toByte() -> processSystemMessage(packet)
				else -> unprocessedPacket("from XR18", packet)
			}
		}
	}

	private fun processButtonLed(packet: ByteArray) {
		val indexToButtonLEDEvent: (Int) -> AbstractButtonLEDEvent? = { index ->
			val ledMode = when (packet[index + 1]) {
				0x00.toByte() -> ELEDMode.OFF
				0x01.toByte() -> ELEDMode.FLASH
				else -> ELEDMode.ON
			}
			when (val note = packet[index]) {
				in 0x00.toByte()..0x1F.toByte() -> processChannelButtonLed(note, ledMode)
				in 0x20.toByte()..0x27.toByte() -> UnusedButtonLEDEvent(note, ledMode)
				0x28.toByte() -> ButtonLEDEvent(EButton.ENCODER_TRACK, ledMode)
				0x29.toByte() -> ButtonLEDEvent(EButton.ENCODER_SEND, ledMode)
				0x2A.toByte() -> ButtonLEDEvent(EButton.ENCODER_PAN, ledMode)
				0x2B.toByte() -> ButtonLEDEvent(EButton.ENCODER_PLUGIN, ledMode)
				0x2C.toByte() -> ButtonLEDEvent(EButton.ENCODER_EQ, ledMode)
				0x2D.toByte() -> ButtonLEDEvent(EButton.ENCODER_INST, ledMode)
				0x2E.toByte() -> ButtonLEDEvent(EButton.PREV_BANK, ledMode)
				0x2F.toByte() -> ButtonLEDEvent(EButton.NEXT_BANK, ledMode)
				0x30.toByte() -> ButtonLEDEvent(EButton.PREV_CHANNEL, ledMode)
				0x31.toByte() -> ButtonLEDEvent(EButton.NEXT_CHANNEL, ledMode)
				0x32.toByte() -> ButtonLEDEvent(EButton.FLIP, ledMode)
				0x33.toByte() -> ButtonLEDEvent(EButton.GLOBAL_VIEW, ledMode)
				in 0x34.toByte()..0x35.toByte() -> UnusedButtonLEDEvent(note, ledMode)
				0x36.toByte() -> ButtonLEDEvent(EButton.F1, ledMode)
				0x37.toByte() -> ButtonLEDEvent(EButton.F2, ledMode)
				0x38.toByte() -> ButtonLEDEvent(EButton.F3, ledMode)
				0x39.toByte() -> ButtonLEDEvent(EButton.F4, ledMode)
				0x3A.toByte() -> ButtonLEDEvent(EButton.F5, ledMode)
				0x3B.toByte() -> ButtonLEDEvent(EButton.F6, ledMode)
				0x3C.toByte() -> ButtonLEDEvent(EButton.F7, ledMode)
				0x3D.toByte() -> ButtonLEDEvent(EButton.F8, ledMode)
				0x3E.toByte() -> ButtonLEDEvent(EButton.MIDI_TRACKS, ledMode)
				0x3F.toByte() -> ButtonLEDEvent(EButton.INPUTS, ledMode)
				0x40.toByte() -> ButtonLEDEvent(EButton.AUDIO_TRACKS, ledMode)
				0x41.toByte() -> ButtonLEDEvent(EButton.AUDIO_INST, ledMode)
				0x42.toByte() -> ButtonLEDEvent(EButton.AUX, ledMode)
				0x43.toByte() -> ButtonLEDEvent(EButton.BUSES, ledMode)
				0x44.toByte() -> ButtonLEDEvent(EButton.OUTPUTS, ledMode)
				0x45.toByte() -> ButtonLEDEvent(EButton.USER, ledMode)
				0x46.toByte() -> ButtonLEDEvent(EButton.MODIFY_SHIFT, ledMode)
				0x47.toByte() -> ButtonLEDEvent(EButton.MODIFY_OPTION, ledMode)
				0x48.toByte() -> ButtonLEDEvent(EButton.MODIFY_CONTROL, ledMode)
				0x49.toByte() -> ButtonLEDEvent(EButton.MODIFY_ALT, ledMode)
				0x4A.toByte() -> ButtonLEDEvent(EButton.AUTOMATION_READ, ledMode)
				0x4B.toByte() -> ButtonLEDEvent(EButton.AUTOMATION_WRITE, ledMode)
				0x4C.toByte() -> ButtonLEDEvent(EButton.AUTOMATION_TRIM, ledMode)
				0x4D.toByte() -> ButtonLEDEvent(EButton.AUTOMATION_TOUCH, ledMode)
				0x4E.toByte() -> ButtonLEDEvent(EButton.AUTOMATION_LATCH, ledMode)
				0x4F.toByte() -> ButtonLEDEvent(EButton.AUTOMATION_GROUP, ledMode)
				0x50.toByte() -> ButtonLEDEvent(EButton.UTILITY_SAVE, ledMode)
				0x51.toByte() -> ButtonLEDEvent(EButton.UTILITY_UNDO, ledMode)
				0x52.toByte() -> ButtonLEDEvent(EButton.UTILITY_CANCEL, ledMode)
				0x53.toByte() -> ButtonLEDEvent(EButton.UTILITY_ENTER, ledMode)
				in 0x54.toByte()..0x72.toByte() -> UnusedButtonLEDEvent(note, ledMode)
				0x73.toByte() -> ButtonLEDEvent(EButton.SOLO, ledMode)
				else -> {
					unprocessedPacket("from XR18", packet)
					null
				}
			}
		}
		val buttonLedEvents = (1 until packet.size step 2).mapNotNull(indexToButtonLEDEvent)
			.toTypedArray()
		processEvent(partial(buttonLedEvents, IXR18Events::setButtonLEDs))
	}

	private fun processChannelButtonLed(note: Byte, ledMode: ELEDMode): ChannelButtonLEDEvent {
		val channelButton = when (note) {
			in 0x00.toByte()..0x07.toByte() -> EChannelButton.REC
			in 0x08.toByte()..0x0F.toByte() -> EChannelButton.SOLO
			in 0x10.toByte()..0x17.toByte() -> EChannelButton.MUTE
			else -> EChannelButton.SELECT
		}
		val channel = note - (when (channelButton) {
			EChannelButton.REC -> 0x00
			EChannelButton.SOLO -> 0x08
			EChannelButton.MUTE -> 0x10
			EChannelButton.SELECT -> 0x18
		}) + 1
		return ChannelButtonLEDEvent(channel, channelButton, ledMode)
	}

	private fun processLED(packet: ByteArray) {
		val indexToLEDEvent: (Int) -> AbstractLEDEvent? = { index ->
			val data = packet[index + 1]
			when (val id = packet[index]) {
				in 0x30..0x37 -> LEDRingEvent(id - 0x30 + 1, true, data)
				in 0x38..0x3F -> LEDRingEvent(id - 0x38 + 1, false, data)
				in 0x60..0xFF -> DigitEvent(id - 0x60 + 1, data)
				else -> {
					unprocessedPacket("from XR18", packet)
					null
				}
			}
		}
		val ledEvents = (1 until packet.size step 2).mapNotNull(indexToLEDEvent)
			.toTypedArray()
		processEvent(partial(ledEvents, IXR18Events::setLEDs))
	}

	private fun processMeter(packet: ByteArray) {
		val meters = Array(packet.size - 1) { i ->
			val byte = packet[1 + i]
			Meter(byte.div(16) + 1, byte.rem(16))
		}
		processEvent(partial(meters, IXR18Events::setMeters))
	}

	private fun processFaderMove(packet: ByteArray) {
		val indexToFaderEvent: (Int) -> AbstractFaderEvent? = { index ->
			val channel = packet[index] - 0xE0.toByte() + 1
			val position = packet[index + 1] + packet[index + 2] * 128
			if (channel == 9) {
				MainFaderEvent(position)
			} else {
				ChannelFaderEvent(channel, position)
			}
		}
		val faderEvents = (packet.indices step 3).mapNotNull(indexToFaderEvent)
			.toTypedArray()
		processEvent(partial(faderEvents, IXR18Events::setFaderPositions))
	}

	private fun processSystemMessage(packet: ByteArray) {
		if (packet[packet.size - 1] == 0xF7.toByte()) {
			if (packet.size >= 22 && packet[1] == 0x00.toByte() && packet[2] == 0x00.toByte() && packet[3] == 0x66.toByte() && packet[4] == 0x58.toByte()) {
				processScribbleStrip(packet)
			} else {
				val message = ByteArray(packet.size - 2) {
					packet[it + 1]
				}
				processEvent(partial(message, IXR18Events::systemMessage))
			}
		} else {
			unprocessedPacket("from XR18", packet)
		}
	}

	private fun processScribbleStrip(packet: ByteArray) {
		val indexToScribbleStripEvent: (Int) -> ScribbleStripEvent? = { index ->
			val channel = packet[index + 5] - 0x20 + 1
			val color = when (packet[index + 6].rem(0x40)) {
				0x01 -> EScribbleColor.RED
				0x02 -> EScribbleColor.GREEN
				0x03 -> EScribbleColor.YELLOW
				0x04 -> EScribbleColor.BLUE
				0x05 -> EScribbleColor.PINK
				0x06 -> EScribbleColor.CYAN
				else -> EScribbleColor.WHITE
			}
			val secondLineInverted = packet[index + 6] >= 0x40
			val line1 = String(packet, index + 7, 7)
			val line2 = String(packet, index + 14, 7)
			ScribbleStripEvent(channel, color, secondLineInverted, line1, line2)
		}
		val scribbleStripEvents = (packet.indices step 22).mapNotNull(indexToScribbleStripEvent)
			.toTypedArray()
		processEvent(partial(scribbleStripEvents, IXR18Events::setScribbleTrips))
	}
}