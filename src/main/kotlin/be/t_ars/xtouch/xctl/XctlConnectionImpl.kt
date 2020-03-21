package be.t_ars.xtouch.xctl

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

private const val DEBUG = false

class XctlConnectionImpl(private val proxyForXR18: InetAddress?) : IXctlConnection {
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

	override fun addConnectionListener(listener: IXctlConnectionListener) {
		listeners.add(listener)
	}

	override fun addXTouchListener(listener: IXTouchListener) {
		fromXTouch.addListener(listener)
	}

	override fun getOutput(): IXctlOutput =
		toXTouch

	override fun run() {
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
					if (proxyForXR18 == null) {
						sendHeartbeat()
					}
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

	override fun stop() {
		running.set(false)
	}

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