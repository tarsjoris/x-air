package be.t_ars.xtouch.xctl

import be.t_ars.xtouch.util.partial
import be.t_ars.xtouch.util.unprocessedPacket

internal class FromXTouch(private val processEvent: EventProcessor<IXTouchEvents>) {
	fun processPacket(packet: ByteArray) {
		if (packet.isNotEmpty()) {
			when (packet[0]) {
				0x90.toByte() -> processButtonPress(packet)
				0xB0.toByte() -> processKnobRotation(packet)
				in 0xE0.toByte()..0xE8.toByte() -> processFaderMove(packet)
				0xF0.toByte() -> processSystemMessage(packet)
				else -> unprocessedPacket("from XTouch", packet)
			}
		}
	}

	private fun processButtonPress(packet: ByteArray) {
		if (packet.size == 3) {
			val down = packet[2] == 0x7F.toByte()
			when (val note = packet[1]) {
				in 0x00.toByte()..0x07.toByte() ->
					processEvent(partial(note - 0x00 + 1, down, IXTouchEvents::channelRecPressed))
				in 0x08.toByte()..0x0F.toByte() ->
					processEvent(partial(note - 0x08 + 1, down, IXTouchEvents::channelSoloPressed))
				in 0x10.toByte()..0x17.toByte() ->
					processEvent(partial(note - 0x10 + 1, down, IXTouchEvents::channelMutePressed))
				in 0x18.toByte()..0x1F.toByte() ->
					processEvent(partial(note - 0x18 + 1, down, IXTouchEvents::channelSelectPressed))
				in 0x20.toByte()..0x27.toByte() ->
					processEvent(partial(note - 0x20 + 1, down, IXTouchEvents::knobPressed))
				0x28.toByte() ->
					processEvent(partial(down, IXTouchEvents::encoderTrackPressed))
				0x29.toByte() ->
					processEvent(partial(down, IXTouchEvents::encoderSendPressed))
				0x2A.toByte() ->
					processEvent(partial(down, IXTouchEvents::encoderPanPressed))
				0x2B.toByte() ->
					processEvent(partial(down, IXTouchEvents::encoderPluginPressed))
				0x2C.toByte() ->
					processEvent(partial(down, IXTouchEvents::encoderEqPressed))
				0x2D.toByte() ->
					processEvent(partial(down, IXTouchEvents::encoderInstPressed))
				0x2E.toByte() ->
					processEvent(partial(down, IXTouchEvents::previousBankPressed))
				0x2F.toByte() ->
					processEvent(partial(down, IXTouchEvents::nextBankPressed))
				0x30.toByte() ->
					processEvent(partial(down, IXTouchEvents::previousChannelPressed))
				0x31.toByte() ->
					processEvent(partial(down, IXTouchEvents::nextChannelPressed))
				0x32.toByte() ->
					processEvent(partial(down, IXTouchEvents::flipPressed))
				0x33.toByte() ->
					processEvent(partial(down, IXTouchEvents::globalViewPressed))
				0x34.toByte() ->
					processEvent(partial(down, IXTouchEvents::displayPressed))
				0x35.toByte() ->
					processEvent(partial(down, IXTouchEvents::smptePressed))
				in 0x36.toByte()..0x3D.toByte() ->
					processEvent(partial(note - 0x36 + 1, down, IXTouchEvents::functionPressed))
				0x3E.toByte() ->
					processEvent(partial(down, IXTouchEvents::midiTracksPressed))
				0x3F.toByte() ->
					processEvent(partial(down, IXTouchEvents::inputsPressed))
				0x40.toByte() ->
					processEvent(partial(down, IXTouchEvents::audioTracksPressed))
				0x41.toByte() ->
					processEvent(partial(down, IXTouchEvents::audioInstPressed))
				0x42.toByte() ->
					processEvent(partial(down, IXTouchEvents::auxPressed))
				0x43.toByte() ->
					processEvent(partial(down, IXTouchEvents::busesPressed))
				0x44.toByte() ->
					processEvent(partial(down, IXTouchEvents::outputsPressed))
				0x45.toByte() ->
					processEvent(partial(down, IXTouchEvents::userPressed))
				in 0x46.toByte()..0x49.toByte() ->
					processEvent(partial(note - 0x46 + 1, down, IXTouchEvents::modifyPressed))
				in 0x4A.toByte()..0x4F.toByte() ->
					processEvent(partial(note - 0x4A + 1, down, IXTouchEvents::automationPressed))
				in 0x50..0x53 ->
					processEvent(partial(note - 0x50 + 1, down, IXTouchEvents::utiliyPressed))
				in 0x68..0x6F ->
					processEvent(partial(note - 0x68 + 1, down, IXTouchEvents::faderPressed))
				0x70.toByte() ->
					processEvent(partial(down, IXTouchEvents::mainFaderPressed))
				else ->
					unprocessedPacket("from XTouch", packet)
			}
		} else {
			unprocessedPacket("from XTouch", packet)
		}
	}

	private fun processKnobRotation(packet: ByteArray) {
		if (packet.size == 3) {
			val right = packet[2] in 0x01.toByte()..0x40.toByte()
			when (val note = packet[1]) {
				in 0x10.toByte()..0x17.toByte() ->
					processEvent(partial(note - 0x10 + 1, right, IXTouchEvents::knobRotated))
				else ->
					unprocessedPacket("from XTouch", packet)
			}
		} else {
			unprocessedPacket("from XTouch", packet)
		}
	}

	private fun processFaderMove(packet: ByteArray) {
		if (packet.size == 3) {
			val channel = packet[0] - 0xE0.toByte() + 1
			val position = packet[2] * 128 + packet[1]
			if (channel == 9) {
				processEvent(partial(position, IXTouchEvents::mainFaderMoved))
			} else {
				processEvent(partial(channel, position, IXTouchEvents::faderMoved))
			}
		} else {
			unprocessedPacket("from XTouch", packet)
		}
	}

	private fun processSystemMessage(packet: ByteArray) {
		if (packet[packet.size - 1] == 0xF7.toByte()) {
			val message = ByteArray(packet.size - 2) {
				packet[it + 1]
			}
			processEvent(partial(message, IXTouchEvents::systemMessage))
		} else {
			unprocessedPacket("from XTouch", packet)
		}
	}
}