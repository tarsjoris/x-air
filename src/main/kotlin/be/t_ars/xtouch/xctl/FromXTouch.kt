package be.t_ars.xtouch.xctl

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.DatagramPacket

internal class FromXTouch {
	private val listeners = mutableListOf<IXTouchListener>()

	fun addListener(listener: IXTouchListener) =
		listeners.add(listener)

	fun processPacket(packet: DatagramPacket) {
		if (packet.length > 0) {
			when (packet.data[packet.offset]) {
				0x90.toByte() -> processButtonPress(packet)
				0xB0.toByte() -> processKnobRotation(packet)
				in 0xE0.toByte()..0xE8.toByte() -> processFaderMove(packet)
			}
		}
	}

	private fun processButtonPress(packet: DatagramPacket) {
		if (packet.length == 3 && packet.data[packet.offset + 2] == 0x7F.toByte()) {
			when (val note = packet.data[packet.offset + 1]) {
				in 0x00.toByte()..0x07.toByte() -> broadcast { it.channelRecPressed(note - 0x00 + 1) }
				in 0x08.toByte()..0x0F.toByte() -> broadcast { it.channelSoloPressed(note - 0x08 + 1) }
				in 0x10.toByte()..0x17.toByte() -> broadcast { it.channelMutePressed(note - 0x10 + 1) }
				in 0x18.toByte()..0x1F.toByte() -> broadcast { it.channelSelectPressed(note - 0x18 + 1) }
				in 0x20.toByte()..0x27.toByte() -> broadcast { it.knobPressed(note - 0x20 + 1) }
				0x28.toByte() -> broadcast(IXTouchListener::encoderTrackPressed)
				0x29.toByte() -> broadcast(IXTouchListener::encoderSendPressed)
				0x2A.toByte() -> broadcast(IXTouchListener::encoderPanPressed)
				0x2B.toByte() -> broadcast(IXTouchListener::encoderPluginPressed)
				0x2C.toByte() -> broadcast(IXTouchListener::encoderEqPressed)
				0x2D.toByte() -> broadcast(IXTouchListener::encoderInstPressed)
				0x2E.toByte() -> broadcast(IXTouchListener::previousBankPressed)
				0x2F.toByte() -> broadcast(IXTouchListener::nextBankPressed)
				0x30.toByte() -> broadcast(IXTouchListener::previousChannelPressed)
				0x31.toByte() -> broadcast(IXTouchListener::nextChannelPressed)
				0x32.toByte() -> broadcast(IXTouchListener::flipPressed)
				0x33.toByte() -> broadcast(IXTouchListener::globalViewPressed)
				0x34.toByte() -> broadcast(IXTouchListener::displayPressed)
				0x35.toByte() -> broadcast(IXTouchListener::smptePressed)
				in 0x36.toByte()..0x3D.toByte() -> broadcast { it.functionPressed(note - 0x36 + 1) }
				0x3E.toByte() -> broadcast(IXTouchListener::midiTracksPressed)
				0x3F.toByte() -> broadcast(IXTouchListener::inputsPressed)
				0x40.toByte() -> broadcast(IXTouchListener::audioTracksPressed)
				0x41.toByte() -> broadcast(IXTouchListener::audioInstPressed)
				0x42.toByte() -> broadcast(IXTouchListener::auxPressed)
				0x43.toByte() -> broadcast(IXTouchListener::busesPressed)
				0x44.toByte() -> broadcast(IXTouchListener::outputsPressed)
				0x45.toByte() -> broadcast(IXTouchListener::userPressed)
				in 0x46.toByte()..0x49.toByte() -> broadcast { it.modifyPressed(note - 0x46 + 1) }
				in 0x4A.toByte()..0x4F.toByte() -> broadcast { it.automationPressed(note - 0x4A + 1) }
				in 0x50.toByte()..0x53.toByte() -> broadcast { it.utiliyPressed(note - 0x50 + 1) }
			}
		}
	}

	private fun processKnobRotation(packet: DatagramPacket) {
		if (packet.length == 3) {
			val right = packet.data[packet.offset + 2] in 0x01.toByte()..0x40.toByte()
			when (val note = packet.data[packet.offset + 1]) {
				in 0x10.toByte()..0x17.toByte() -> broadcast { it.knobRotated(note - 0x10 + 1, right) }
			}
		}
	}

	private fun processFaderMove(packet: DatagramPacket) {
		if (packet.length == 3) {
			val channel = packet.data[packet.offset] - 0xE0.toByte() + 1
			val position = (packet.data[packet.offset + 2] * 128 + packet.data[packet.offset + 1])
				.toFloat()
				.div(16380)
			broadcast {
				if (channel == 9) {
					it.mainFaderMoved(position)
				} else {
					it.faderMoved(channel, position)
				}
			}
		}
	}

	private fun broadcast(eventSender: suspend (IXTouchListener) -> Unit) {
		GlobalScope.launch {
			for (l in listeners) {
				eventSender.invoke(l)
			}
		}
	}
}