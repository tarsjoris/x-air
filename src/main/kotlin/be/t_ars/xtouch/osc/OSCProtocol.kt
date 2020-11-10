package be.t_ars.xtouch.osc

private val BUNDLE_START = "#bundle"

private fun serializeString(data: String) =
	data.toByteArray(Charsets.UTF_8) +
			byteArrayOf(0x00.toByte()) +
			ByteArray(padCount(data.length + 1)) { _ ->
				0X00.toByte()
			}

private fun padCount(length: Int) =
	(4 - (length.rem(4))).rem(4)

private fun serializeInt32(data: Int) =
	ByteArray(4) { i ->
		((data shr (8 * i)) and 0x0F).toByte()
	}

private fun serializeLong64(data: Long) =
	ByteArray(8) { i ->
		((data shr (8 * i)) and 0x0F).toByte()
	}

private fun serializeFloat32(data: Float) =
	serializeInt32(data.toRawBits())

interface IOSCArg {
	fun getType(): String
	fun serialize(): ByteArray
}

class OSCArgBoolean(val data: Boolean) : IOSCArg {
	override fun getType() =
		if (data) {
			"T"
		} else {
			"F"
		}

	override fun serialize() =
		byteArrayOf()
}

class OSCArgString(val data: String) : IOSCArg {
	override fun getType() =
		"s"

	override fun serialize() =
		serializeString(data)
}

class OSCArgInt(val data: Int) : IOSCArg {
	override fun getType() =
		"i"

	override fun serialize() =
		serializeInt32(data)
}

class OSCArgLong(val data: Long) : IOSCArg {
	override fun getType() =
		"h"

	override fun serialize() =
		serializeLong64(data)
}

class OSCArgFloat(val data: Float) : IOSCArg {
	override fun getType() =
		"f"

	override fun serialize() =
		serializeFloat32(data)
}

interface IOSCPacket {
	fun serialize(): ByteArray
}

class OSCMessage(val address: String, val arguments: Array<out IOSCArg> = emptyArray()) : IOSCPacket {
	fun getInt(argumentIndex: Int): Int? =
		getArg(argumentIndex)?.let {
			if (it is OSCArgInt) it.data else null
		}

	private fun getArg(argumentIndex: Int): IOSCArg? =
		if (argumentIndex in arguments.indices) {
			arguments[argumentIndex]
		} else {
			null
		}

	override fun serialize(): ByteArray {
		val addressBytes = serializeString(address)
		return if (arguments.isEmpty())
			addressBytes
		else addressBytes +
				serializeString("," + arguments.joinToString(transform = IOSCArg::getType)) +
				arguments.map(IOSCArg::serialize)
					.reduce(ByteArray::plus)
	}
}

class OSCTimeTag(val ntpTime: Long) {
	fun serialize() =
		serializeLong64(ntpTime)
}

class OSCBundle(val timeTag: OSCTimeTag, val packets: Array<IOSCPacket>) : IOSCPacket {
	override fun serialize() =
		serializeString(BUNDLE_START) +
				timeTag.serialize() +
				packets.map(IOSCPacket::serialize)
					.map { packetData -> serializeInt32(packetData.size) + packetData }
					.reduce(ByteArray::plus)
}

fun parsePacket(data: ByteArray, length: Int): IOSCPacket {
	return OSCMessage("")
}