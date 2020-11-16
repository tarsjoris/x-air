package be.t_ars.xtouch.util

import be.t_ars.xtouch.xctl.DEBUG

fun matchesData(actual: ByteArray, expected: ByteArray): Boolean {
	if (actual.size != expected.size) {
		return false
	}
	for (i in expected.indices) {
		if (actual[i] != expected[i]) {
			return false
		}
	}
	return true
}

fun printPacket(from: String, packet: ByteArray) {
	if (DEBUG) {
		doPrintPacket(from, packet)
	}
}

fun unprocessedPacket(label: String, packet: ByteArray) {
	doPrintPacket("Unprocessed packet $label", packet)
}

private fun doPrintPacket(label: String, packet: ByteArray) {
	print("$label: ")
	for (i in packet.indices) {
		val entry = packet[i]
		val entryInt = entry.toUByte().toInt()
		val entryHex = String.format("%02X", entry)
		print("$entryInt(0x$entryHex) ")
	}
	println()
}