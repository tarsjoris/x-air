package be.t_ars.xtouch.xctl

import be.t_ars.xtouch.util.printPacket
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicBoolean

internal class ConnectionWithXR18Impl(
	private val xr18Address: InetAddress,
	private val heartbeatListener: () -> Unit,
	processEvent: EventProcessor<IXR18Events>
) {
	private val toXR18 = ToXR18(this::sendToXR18)

	private val running = AtomicBoolean(true)
	private val fromXR18 = FromXR18(processEvent)
	private val socket = DatagramSocket()

	fun getConnectionToXR18(): IConnectionToXR18 =
		toXR18

	fun run() {
		val buffer = ByteArray(256)
		val packet = DatagramPacket(buffer, buffer.size)
		while (running.get()) {
			try {
				socket.receive(packet)
				val data = ByteArray(packet.length) { i ->
					packet.data[packet.offset + i]
				}
				processPacket(data)
			} catch (e: Exception) {
				println("Exception while processing message: ${e.message}")
				e.printStackTrace()
			}
		}
	}

	fun stop() {
		running.set(false)
	}

	private fun sendToXR18(payload: ByteArray) {
		printPacket("To XR18 ", payload)
		socket.send(DatagramPacket(payload, 0, payload.size, xr18Address, PORT))
	}

	private fun processPacket(packet: ByteArray) {
		if (isXR18Heartbeat(packet)) {
			heartbeatListener()
		} else {
			printPacket("From XR18", packet)
			fromXR18.processPacket(packet)
		}
	}
}