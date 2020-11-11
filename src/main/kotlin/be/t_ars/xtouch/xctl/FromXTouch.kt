package be.t_ars.xtouch.xctl

import be.t_ars.xtouch.util.Listeners
import java.net.DatagramPacket

internal class FromXTouch {
	private val listeners = Listeners<IXTouchListener>()

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
				in 0x00.toByte()..0x07.toByte() -> listeners.broadcastSuspend { it.channelRecPressed(note - 0x00 + 1) }
				in 0x08.toByte()..0x0F.toByte() -> listeners.broadcastSuspend { it.channelSoloPressed(note - 0x08 + 1) }
				in 0x10.toByte()..0x17.toByte() -> listeners.broadcastSuspend { it.channelMutePressed(note - 0x10 + 1) }
				in 0x18.toByte()..0x1F.toByte() -> listeners.broadcastSuspend { it.channelSelectPressed(note - 0x18 + 1) }
				in 0x20.toByte()..0x27.toByte() -> listeners.broadcastSuspend { it.knobPressed(note - 0x20 + 1) }
				0x28.toByte() -> listeners.broadcastSuspend(IXTouchListener::encoderTrackPressed)
				0x29.toByte() -> listeners.broadcastSuspend(IXTouchListener::encoderSendPressed)
				0x2A.toByte() -> listeners.broadcastSuspend(IXTouchListener::encoderPanPressed)
				0x2B.toByte() -> listeners.broadcastSuspend(IXTouchListener::encoderPluginPressed)
				0x2C.toByte() -> listeners.broadcastSuspend(IXTouchListener::encoderEqPressed)
				0x2D.toByte() -> listeners.broadcastSuspend(IXTouchListener::encoderInstPressed)
				0x2E.toByte() -> listeners.broadcastSuspend(IXTouchListener::previousBankPressed)
				0x2F.toByte() -> listeners.broadcastSuspend(IXTouchListener::nextBankPressed)
				0x30.toByte() -> listeners.broadcastSuspend(IXTouchListener::previousChannelPressed)
				0x31.toByte() -> listeners.broadcastSuspend(IXTouchListener::nextChannelPressed)
				0x32.toByte() -> listeners.broadcastSuspend(IXTouchListener::flipPressed)
				0x33.toByte() -> listeners.broadcastSuspend(IXTouchListener::globalViewPressed)
				0x34.toByte() -> listeners.broadcastSuspend(IXTouchListener::displayPressed)
				0x35.toByte() -> listeners.broadcastSuspend(IXTouchListener::smptePressed)
				in 0x36.toByte()..0x3D.toByte() -> listeners.broadcastSuspend { it.functionPressed(note - 0x36 + 1) }
				0x3E.toByte() -> listeners.broadcastSuspend(IXTouchListener::midiTracksPressed)
				0x3F.toByte() -> listeners.broadcastSuspend(IXTouchListener::inputsPressed)
				0x40.toByte() -> listeners.broadcastSuspend(IXTouchListener::audioTracksPressed)
				0x41.toByte() -> listeners.broadcastSuspend(IXTouchListener::audioInstPressed)
				0x42.toByte() -> listeners.broadcastSuspend(IXTouchListener::auxPressed)
				0x43.toByte() -> listeners.broadcastSuspend(IXTouchListener::busesPressed)
				0x44.toByte() -> listeners.broadcastSuspend(IXTouchListener::outputsPressed)
				0x45.toByte() -> listeners.broadcastSuspend(IXTouchListener::userPressed)
				in 0x46.toByte()..0x49.toByte() -> listeners.broadcastSuspend { it.modifyPressed(note - 0x46 + 1) }
				in 0x4A.toByte()..0x4F.toByte() -> listeners.broadcastSuspend { it.automationPressed(note - 0x4A + 1) }
				in 0x50.toByte()..0x53.toByte() -> listeners.broadcastSuspend { it.utiliyPressed(note - 0x50 + 1) }
			}
		}
	}

	private fun processKnobRotation(packet: DatagramPacket) {
		if (packet.length == 3) {
			val right = packet.data[packet.offset + 2] in 0x01.toByte()..0x40.toByte()
			when (val note = packet.data[packet.offset + 1]) {
				in 0x10.toByte()..0x17.toByte() -> listeners.broadcastSuspend { it.knobRotated(note - 0x10 + 1, right) }
			}
		}
	}

	private fun processFaderMove(packet: DatagramPacket) {
		if (packet.length == 3) {
			val channel = packet.data[packet.offset] - 0xE0.toByte() + 1
			val position = (packet.data[packet.offset + 2] * 128 + packet.data[packet.offset + 1])
				.toFloat()
				.div(16380)
			listeners.broadcastSuspend {
				if (channel == 9) {
					it.mainFaderMoved(position)
				} else {
					it.faderMoved(channel, position)
				}
			}
		}
	}
}