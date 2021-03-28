package be.t_ars.xtouch.osc

import be.t_ars.xtouch.util.Reference
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

fun searchXR18(): InetAddress? {
	val semaphore = Object()
	val address = Reference<InetAddress>()

	val socket = DatagramSocket()
	socket.broadcast = true
	val payload = OSCMessage("/info").serialize()
	socket.send(DatagramPacket(payload, payload.size, InetAddress.getByName("255.255.255.255"), XR18OSCAPI.PORT))

	Thread {
		val buffer = ByteArray(256)
		val udpPacket = DatagramPacket(buffer, buffer.size)
		try {
			socket.receive(udpPacket)
			val packet = parsePacket(udpPacket.data, udpPacket.length)
			if (packet is OSCMessage && packet.address == "/info") {
				address.reference = udpPacket.address
			}
		}
		catch (e: Throwable) {
			// ignore
		}
	}.start()
	synchronized(semaphore) {
		semaphore.wait(1000L)
	}

	socket.close()

	return address.reference;
}