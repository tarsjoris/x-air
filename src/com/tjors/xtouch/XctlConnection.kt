package com.tjors.xtouch

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

private const val DEBUG = false

class XctlConnection(private val proxyForXR18: InetAddress?) {
	private class RunnableTimerTask(private val task: () -> Unit) : TimerTask() {
		override fun run() {
			task.invoke()
		}
	}

	private val listeners = mutableListOf<IXctlListener>()
	private var xTouchAddress: InetAddress? = null
	private var running = AtomicBoolean(true)
	private val socket = DatagramSocket(PORT)
	private var timer = Timer()
	private var xTouchConnected = false
	private var lastXTouchHeartbeat: Long = 0
	private var xr18Connected = false
	private var lastXR18Heartbeat: Long = 0

	constructor() : this(null)

	fun addListener(listener: IXctlListener) =
		listeners.add(listener)

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
						}
						processPacket(packet)
						if (proxyForXR18 != null) {
							packet.address = proxyForXR18
							socket.send(packet)
						}
					}
					proxyForXR18 -> {
						if (isXR18Heartbeat(packet)) {
							xr18HeartbeatReceived()
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

	private fun processPacket(packet: DatagramPacket) {
		if (DEBUG) printPacket(packet)
		if (packet.length > 0) {
			when (packet.data[packet.offset]) {
				0x90.toByte() -> processButtonPress(packet)
				0xB0.toByte() -> processKnobRotation(packet)
			}
		}
	}

	private fun processButtonPress(packet: DatagramPacket) {
		if (packet.length == 3 && packet.data[packet.offset + 2] == 0x7F.toByte()) {
			when (val note = packet.data[packet.offset + 1]) {
				in 0x00..0x07 -> broadcast { it.channelRecPressed(note - 0x00 + 1) }
				in 0x08..0x0F -> broadcast { it.channelSoloPressed(note - 0x08 + 1) }
				in 0x10..0x17 -> broadcast { it.channelMutePressed(note - 0x10 + 1) }
				in 0x18..0x1F -> broadcast { it.channelSelectPressed(note - 0x18 + 1) }
				in 0x20..0x27 -> broadcast { it.knobPressed(note - 0x20 + 1) }
				0x28.toByte() -> broadcast(IXctlListener::encoderTrackPressed)
				0x29.toByte() -> broadcast(IXctlListener::encoderSendPressed)
				0x2A.toByte() -> broadcast(IXctlListener::encoderPanPressed)
				0x2B.toByte() -> broadcast(IXctlListener::encoderPluginPressed)
				0x2C.toByte() -> broadcast(IXctlListener::encoderEqPressed)
				0x2D.toByte() -> broadcast(IXctlListener::encoderInstPressed)
				0x2E.toByte() -> broadcast(IXctlListener::previousBankPressed)
				0x2F.toByte() -> broadcast(IXctlListener::nextBankPressed)
				0x30.toByte() -> broadcast(IXctlListener::previousChannelPressed)
				0x31.toByte() -> broadcast(IXctlListener::nextChannelPressed)
				0x32.toByte() -> broadcast(IXctlListener::flipPressed)
				0x33.toByte() -> broadcast(IXctlListener::globalViewPressed)
				0x34.toByte() -> broadcast(IXctlListener::displayPressed)
				0x35.toByte() -> broadcast(IXctlListener::smptePressed)
				in 0x36..0x3C -> broadcast { it.functionPressed(note - 0x36 + 1) }
				0x3E.toByte() -> broadcast(IXctlListener::midiTracksPressed)
				0x3F.toByte() -> broadcast(IXctlListener::inputsPressed)
				0x40.toByte() -> broadcast(IXctlListener::audioTracksPressed)
				0x41.toByte() -> broadcast(IXctlListener::audioInstPressed)
				0x42.toByte() -> broadcast(IXctlListener::auxPressed)
				0x43.toByte() -> broadcast(IXctlListener::busesPressed)
				0x44.toByte() -> broadcast(IXctlListener::outputsPressed)
				0x45.toByte() -> broadcast(IXctlListener::userPressed)
				in 0x46..0x49 -> broadcast { it.modifyPressed(note - 0x46 + 1) }
				in 0x4A..0x4F -> broadcast { it.automationPressed(note - 0x4A + 1) }
			}
		}
	}

	private fun processKnobRotation(packet: DatagramPacket) {
		if (packet.length == 3) {
			val right = packet.data[packet.offset + 2] in 0x01..0x40
			when (val note = packet.data[packet.offset + 1]) {
				in 0x10..0x17 -> broadcast { it.knobRotated(note - 0x10 + 1, right) }
			}
		}
	}

	private fun broadcast(eventSender: (IXctlListener) -> Unit) =
		listeners.forEach(eventSender)

	private fun printPacket(packet: DatagramPacket) {
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
				broadcast(IXctlListener::connected)
			}
		}
	}

	private fun checkConnection() {
		synchronized(this) {
			if (System.currentTimeMillis() - lastXTouchHeartbeat > 8_000) {
				broadcastIfWillDisconnected()
				xTouchConnected = false
			}
			if (proxyForXR18 != null) {
				if (System.currentTimeMillis() - lastXR18Heartbeat > 8_000) {
					broadcastIfWillDisconnected()
					xr18Connected = false
				}
			}
		}
	}

	private fun broadcastIfWillDisconnected() {
		synchronized(this) {
			if (xTouchConnected && (proxyForXR18 == null || xr18Connected)) {
				broadcast(IXctlListener::disconnected)
			}
		}
	}

	private fun sendHeartbeat() {
		xTouchAddress?.also { address ->
			socket.send(DatagramPacket(XR18_HEARTBEAT_PAYLOAD, 0, XR18_HEARTBEAT_PAYLOAD.size, address, PORT))
		}
	}

	companion object {
		private const val PORT = 10111
		private val XTOUCH_HEARTBEAT_PAYLOAD =
			byteArrayOf(0xF0.toByte(), 0x00, 0x20, 0x32, 0x58, 0x54, 0x00, 0xF7.toByte())
		private val XR18_HEARTBEAT_PAYLOAD = byteArrayOf(0xF0.toByte(), 0x00, 0x00, 0x66, 0x14, 0x00, 0XF7.toByte())

		private fun isXTouchHeartbeat(packet: DatagramPacket) =
			matchesData(packet, XTOUCH_HEARTBEAT_PAYLOAD)

		private fun isXR18Heartbeat(packet: DatagramPacket) =
			matchesData(packet, XR18_HEARTBEAT_PAYLOAD)

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