package be.t_ars.xtouch.osc

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

private const val PORT = 10024

private const val OUTPUT_COUNT = 6
private const val CHANNEL_COUNT = 17;
private const val AUX_CHANNEL = 17;
private const val DEBUG = false

class XR18API(private val host: InetAddress) {
	private val socket = DatagramSocket()

	fun startListening(isRunning: () -> Boolean, listener: IXR18Listener) {
		val buffer = ByteArray(256)
		val udpPacket = DatagramPacket(buffer, buffer.size)
		while (isRunning()) {
			socket.receive(udpPacket)
			printPacket(udpPacket.data)
			val packet = parsePacket(udpPacket.data, udpPacket.length)
			processPacket(packet, listener)
		}
	}

	private fun processPacket(packet: IOSCPacket, listener: IXR18Listener) {
		when (packet) {
			is OSCBundle ->
				packet.packets.forEach { processPacket(it, listener) }
			is OSCMessage ->
				processMessage(packet, listener)
		}
	}

	private fun processMessage(message: OSCMessage, listener: IXR18Listener) {
		when (message.address) {
			"lr/mix/on" -> {
				listener.lrMixOn(message.getInt(0) == 1)
			}
		}
	}

	fun startRequestChanges() {
		GlobalScope.launch {
			while (socket.isBound) {
				send("/xremote")
				delay(7500)
			}
		}
	}

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