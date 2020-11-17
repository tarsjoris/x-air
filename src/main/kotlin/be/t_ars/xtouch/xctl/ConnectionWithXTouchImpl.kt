package be.t_ars.xtouch.xctl

import be.t_ars.xtouch.util.printPacket
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicBoolean

class ConnectionWithXTouchImpl(
	private val heartbeatListener: () -> Unit,
	processEvent: EventProcessor<IXTouchEvents>
) {
	private val fromXTouch = FromXTouch(processEvent)
	private val toXTouch = ToXTouch(this::sendToXTouch)

	private var xTouchAddress: InetAddress? = null
	private val running = AtomicBoolean(true)
	private val socket = DatagramSocket(XctlUtil.PORT)

	fun getConnectionToXTouch(): IConnectionToXTouch =
		toXTouch

	fun run() {
		val buffer = ByteArray(256)
		val packet = DatagramPacket(buffer, buffer.size)
		while (running.get()) {
			try {
				socket.receive(packet)
				processPacket(packet)
			} catch (e: Exception) {
				println("Exception while processing message: ${e.message}")
				e.printStackTrace()
			}
		}
	}

	fun stop() {
		running.set(false)
	}

	private fun sendToXTouch(payload: ByteArray) {
		xTouchAddress?.also { address ->
			printPacket("To XTouch ", payload)
			socket.send(DatagramPacket(payload, 0, payload.size, address, XctlUtil.PORT))
		} ?: print("Dropping packet: no connection with XTouch")
	}

	private fun processPacket(packet: DatagramPacket) {
		val data = ByteArray(packet.length) { i ->
			packet.data[packet.offset + i]
		}
		if (XctlUtil.isXTouchHeartbeat(data)) {
			xTouchAddress = packet.address
			heartbeatListener()
		} else {
			printPacket("From XTouch", data)
			fromXTouch.processPacket(data)
		}
	}
}