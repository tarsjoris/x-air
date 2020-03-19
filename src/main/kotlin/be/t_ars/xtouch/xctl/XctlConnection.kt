package be.t_ars.xtouch.xctl

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

private const val DEBUG = false

private class FromXTouch {
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
				in 0x36.toByte()..0x3C.toByte() -> broadcast { it.functionPressed(note - 0x36 + 1) }
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

	private fun broadcast(eventSender: (IXTouchListener) -> Unit) =
		listeners.forEach(eventSender)
}

private class ToXTouch(private val sendPayload: (ByteArray) -> Unit) {
	fun setLEDRing(channel: Int, index: Int?) {
		if (channel in 1..8 && (index == null || index in -6..6)) {
			val channelLeft = (0x30 + channel - 1).toByte()
			val channelRight = (0x38 + channel - 1).toByte()
			sendPayload(
				when (index) {
					null -> byteArrayOf(0xB0.toByte(), channelLeft, 0x00.toByte(), channelRight, 0x00.toByte())
					in -6..0 -> byteArrayOf(
						0xB0.toByte(),
						channelLeft,
						LED_RING[index + 6],
						channelRight,
						0x00.toByte()
					)
					else -> byteArrayOf(0xB0.toByte(), channelLeft, 0x00.toByte(), channelRight, LED_RING[index - 1])
				}
			)
		}
	}

	fun setMeters(values: IntArray) {
		if (values.size == 8) {
			sendPayload(
				byteArrayOf(0xD0.toByte()) + ByteArray(8) { i ->
					(i * 16 + values[i]).toByte()
				}
			)
		}
	}

	fun setDigits(number: Int) =
		sendPayload(
			byteArrayOf(0xB0.toByte()) +
					if (number > 9) {
						byteArrayOf(0x60.toByte(), DIGITS[number.div(10).rem(10)])
					} else {
						byteArrayOf()
					} +
					byteArrayOf(0x61, DIGITS[number.rem(10)])
		)

	companion object {
		private val LED_RING = byteArrayOf(
			0x01.toByte(),
			0x02.toByte(),
			0x04.toByte(),
			0x08.toByte(),
			0x10.toByte(),
			0x20.toByte(),
			0x40.toByte()
		)

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

class XctlConnection(private val proxyForXR18: InetAddress?) {
	private class RunnableTimerTask(private val task: () -> Unit) : TimerTask() {
		override fun run() {
			task.invoke()
		}
	}

	private val listeners = mutableListOf<IXctlConnectionListener>()
	private val fromXTouch = FromXTouch()
	private val toXTouch = ToXTouch(this::sendToXTouch)

	private var xTouchAddress: InetAddress? = null
	private var running = AtomicBoolean(true)
	private val socket = DatagramSocket(PORT)
	private var timer = Timer()
	private var xTouchConnected = false
	private var lastXTouchHeartbeat: Long = 0
	private var xr18Connected = false
	private var lastXR18Heartbeat: Long = 0

	constructor() : this(null)

	fun addConnectionListener(listener: IXctlConnectionListener) =
		listeners.add(listener)

	fun addXTouchListener(listener: IXTouchListener) =
		fromXTouch.addListener(listener)

	fun run() {
		timer.schedule(RunnableTimerTask(this::checkConnection), 0, 2_000)
		if (proxyForXR18 == null) {
			timer.schedule(RunnableTimerTask(this::sendHeartbeat), 0, 6_000)
		}

		val buffer = ByteArray(256)
		val packet = DatagramPacket(buffer, buffer.size)
		while (running.get()) {
			socket.receive(packet)
			synchronized(this) {
				if (xTouchAddress == null && isXTouchHeartbeat(packet)) {
					xTouchAddress = packet.address
				}
				when (packet.address) {
					xTouchAddress -> {
						if (isXTouchHeartbeat(packet)) {
							xTouchHeartbeatReceived()
						} else {
							if (DEBUG) printPacket("XTouch", packet)
						}
						fromXTouch.processPacket(packet)
						if (proxyForXR18 != null) {
							packet.address = proxyForXR18
							socket.send(packet)
						}
					}
					proxyForXR18 -> {
						if (isXR18Heartbeat(packet)) {
							xr18HeartbeatReceived()
						} else {
							if (DEBUG) printPacket("XR18", packet)
						}
						if (xTouchAddress != null) {
							packet.address = xTouchAddress
							socket.send(packet)
						}
					}
				}
			}
		}
		timer.cancel()
	}

	fun stop() {
		running.set(false)
	}

	fun setLEDRing(channel: Int, index: Int) =
		toXTouch.setLEDRing(channel, index)

	fun setMeters(values: IntArray) =
		toXTouch.setMeters(values)

	fun setDigits(number: Int) =
		toXTouch.setDigits(number)

	private fun sendToXTouch(payload: ByteArray) {
		val packet = DatagramPacket(payload, 0, payload.size, xTouchAddress, PORT)
		//printPacket("me ", packet)
		socket.send(packet)
	}

	private fun printPacket(from: String, packet: DatagramPacket) {
		print("From $from ")
		for (i in packet.offset until packet.offset + packet.length) {
			val entry = packet.data[i]
			val entryInt = entry.toInt()
			val entryHex = String.format("%02X", entry)
			print("$entryInt($entryHex) ")
		}
		println()
	}

	private fun xTouchHeartbeatReceived() {
		synchronized(this) {
			lastXTouchHeartbeat = System.currentTimeMillis()
			if (!xTouchConnected) {
				xTouchConnected = true
				println("XTouch connected from address $xTouchAddress")
				broadcastIfConnected()
			}
		}
	}

	private fun xr18HeartbeatReceived() {
		synchronized(this) {
			lastXR18Heartbeat = System.currentTimeMillis()
			if (!xr18Connected) {
				xr18Connected = true
				println("XR18 connected from address $proxyForXR18")
				broadcastIfConnected()
			}
		}
	}

	private fun broadcastIfConnected() {
		synchronized(this) {
			if (xTouchConnected && (proxyForXR18 == null || xr18Connected)) {
				broadcast(IXctlConnectionListener::connected)
			}
		}
	}

	private fun checkConnection() {
		synchronized(this) {
			if (System.currentTimeMillis() - lastXTouchHeartbeat > 8_000) {
				broadcastIfWillDisconnect()
				xTouchConnected = false
			}
			if (proxyForXR18 != null) {
				if (System.currentTimeMillis() - lastXR18Heartbeat > 8_000) {
					broadcastIfWillDisconnect()
					xr18Connected = false
				}
			}
		}
	}

	private fun broadcastIfWillDisconnect() {
		synchronized(this) {
			if (xTouchConnected && (proxyForXR18 == null || xr18Connected)) {
				broadcast(IXctlConnectionListener::disconnected)
			}
		}
	}

	private fun broadcast(eventSender: (IXctlConnectionListener) -> Unit) =
		listeners.forEach(eventSender)

	private fun sendHeartbeat() {
		xTouchAddress?.also { address ->
			socket.send(
				DatagramPacket(XR18_HEARTBEAT_PAYLOAD, 0, XR18_HEARTBEAT_PAYLOAD.size, address, PORT)
			)
		}
	}

	companion object {
		private const val PORT = 10111
		private val XTOUCH_HEARTBEAT_PAYLOAD =
			byteArrayOf(0xF0.toByte(), 0x00, 0x20, 0x32, 0x58, 0x54, 0x00, 0xF7.toByte())
		private val XR18_HEARTBEAT_PAYLOAD = byteArrayOf(0xF0.toByte(), 0x00, 0x00, 0x66, 0x14, 0x00, 0XF7.toByte())

		private fun isXTouchHeartbeat(packet: DatagramPacket) =
			matchesData(
				packet,
				XTOUCH_HEARTBEAT_PAYLOAD
			)

		private fun isXR18Heartbeat(packet: DatagramPacket) =
			matchesData(
				packet,
				XR18_HEARTBEAT_PAYLOAD
			)

		private fun matchesData(packet: DatagramPacket, data: ByteArray): Boolean {
			if (packet.length != data.size) {
				return false
			}
			for (i in data.indices) {
				if (packet.data[packet.offset + i] != data[i]) {
					return false
				}
			}
			return true
		}
	}
}