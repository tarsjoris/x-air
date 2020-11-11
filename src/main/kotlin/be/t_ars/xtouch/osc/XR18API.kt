package be.t_ars.xtouch.osc

import be.t_ars.xtouch.util.Listeners
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicBoolean

private const val PORT = 10024

private const val OUTPUT_COUNT = 6
private const val CHANNEL_COUNT = 17;
private const val AUX_CHANNEL = 17;
private const val DEBUG = false

class XR18API(private val host: InetAddress) {
	private val socket = DatagramSocket()
	private val running = AtomicBoolean(true)
	private val listeners = Listeners<IXR18Listener>()

	fun addListener(listener: IXR18Listener) {
		listeners.add(listener)
	}

	fun run() {
		startRequestChanges()

		val buffer = ByteArray(256)
		val udpPacket = DatagramPacket(buffer, buffer.size)
		while (running.get()) {
			socket.receive(udpPacket)
			printPacket(udpPacket.data)
			try {
				val packet = parsePacket(udpPacket.data, udpPacket.length)
				processPacket(packet)
			} catch (e: Exception) {
				val data = String(udpPacket.data, 0, udpPacket.length)
				println("Could not process packet '$data': ${e.message}")
				e.printStackTrace()
			}
		}
	}

	private fun processPacket(packet: IOSCPacket) {
		when (packet) {
			is OSCBundle ->
				packet.packets.forEach { processPacket(it) }
			is OSCMessage ->
				processMessage(packet)
		}
	}

	private fun processMessage(message: OSCMessage) {
		val parts = message.address.split('/')
		when (parts[0]) {
			"" -> {
				when (parts[1]) {
					"lr" -> {
						when (parts[2]) {
							"mix" -> {
								when (parts[3]) {
									"on" -> {
										val on = message.getInt(0) == 1
										listeners.broadcast { it.lrMixOn(on) }
									}
								}
							}
						}
					}
					"bus" -> {
						val bus = parts[2][0] - '0'
						if (bus in 1..6) {
							when (parts[3]) {
								"mix" -> {
									when (parts[4]) {
										"on" -> {
											val on = message.getInt(0) == 1
											listeners.broadcast { it.busMixOn(bus, on) }
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private fun startRequestChanges() {
		GlobalScope.launch {
			while (running.get()) {
				send("/xremote")
				delay(7500)
			}
		}
	}

	fun requestBusMixOn(bus: Int) =
		send("/bus/$bus/mix/on")

	fun setBusMixOn(bus: Int, on: Boolean) =
		send("/bus/$bus/mix/on", OSCArgInt(if (on) 1 else 0))

	fun requestLRMixOn() =
		send("/lr/mix/on")

	fun setLRMixOn(on: Boolean) =
		send("/lr/mix/on", OSCArgInt(if (on) 1 else 0))

	private fun send(address: String, vararg args: IOSCArg) =
		send(OSCMessage(address, args))

	private fun send(packet: IOSCPacket) {
		val payload = packet.serialize()
		printPacket(payload)
		socket.send(DatagramPacket(payload, payload.size, host, PORT))
	}

	private fun pad(number: Int) =
		number.toString().padStart(2, '0')

	private fun printPacket(data: ByteArray) {
		if (DEBUG) {
			println(String(data))
			println(data.joinToString(separator = ",", transform = { it.toString() }))
		}
	}
}