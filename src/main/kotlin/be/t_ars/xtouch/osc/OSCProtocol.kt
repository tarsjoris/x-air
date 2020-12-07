package be.t_ars.xtouch.osc

private const val BUNDLE_START = "#bundle"
private val BUNDLE_START_BYTES = serializeString(BUNDLE_START)
private const val ZERO = 0x00.toByte()

private fun serializeString(data: String) =
	data.toByteArray(Charsets.UTF_8) +
			ByteArray(1 + padCount(data.length + 1)) { _ ->
				ZERO
			}

private fun padCount(length: Int) =
	(4 - (length.rem(4))).rem(4)

private fun serializeInt32(data: Int) =
	ByteArray(4) { i ->
		((data shr (8 * (3 - i))) and 0x0F).toByte()
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

	fun getString(argumentIndex: Int): String? =
		getArg(argumentIndex)?.let {
			if (it is OSCArgString) it.data else null
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
		BUNDLE_START_BYTES +
				timeTag.serialize() +
				packets.map(IOSCPacket::serialize)
					.map { packetData -> serializeInt32(packetData.size) + packetData }
					.reduce(ByteArray::plus)
}

fun parsePacket(data: ByteArray, length: Int) =
	parsePacket(Payload(data, 0, length))

private fun parsePacket(data: Payload): IOSCPacket {
	if (data.advanceWith(BUNDLE_START_BYTES)) {
		val timeTag = OSCTimeTag(data.readLong64())
		val packets = mutableListOf<IOSCPacket>()
		while (data.hasMoreData()) {
			val packetLength = data.readInt32()
			packets.add(parsePacket(data.extract(packetLength)))
			data.skip(packetLength)
		}
		return OSCBundle(timeTag, packets.toTypedArray())
	} else {
		val address = data.readString()
		if (data.hasMoreData()) {
			val argTypesPart = data.readString()
			if (argTypesPart.isEmpty() || argTypesPart[0] != ',') {
				throw IllegalArgumentException("Argument types should start with a comma")
			}
			val argTypes = argTypesPart.substring(1)
			val arguments = Array<IOSCArg>(argTypes.length) { index ->
				when (argTypes[index]) {
					'T' -> OSCArgBoolean(true)
					'F' -> OSCArgBoolean(false)
					's' -> OSCArgString(data.readString())
					'i' -> OSCArgInt(data.readInt32())
					'h' -> OSCArgLong(data.readLong64())
					'f' -> OSCArgFloat(data.readFloat32())
					else ->
						throw IllegalArgumentException("Unsupported argument type '${argTypes[index]}'")
				}
			}
			return OSCMessage(address, arguments)
		} else {
			return OSCMessage(address)
		}
	}
}

private class Payload(private val data: ByteArray, offset: Int, length: Int) {
	private var index = offset
	private val endIndexExclusive = offset + length

	fun extract(length: Int): Payload {
		if (index + length > endIndexExclusive) {
			throw IndexOutOfBoundsException()
		}
		return Payload(data, index, length)
	}

	fun advanceWith(token: ByteArray): Boolean {
		if (index + token.size > endIndexExclusive) {
			return false
		}
		for (i in token.indices) {
			if (data[index + i] != token[i]) {
				return false
			}
		}
		index += token.size
		return true
	}

	fun skip(length: Int) {
		index += length
		if (index > endIndexExclusive) {
			throw IndexOutOfBoundsException()
		}
	}

	fun hasMoreData() =
		index < endIndexExclusive

	fun readString(): String {
		var pos = index
		while (pos < endIndexExclusive && data[pos] != ZERO) {
			++pos
		}
		val length = pos - index
		val result = String(data, index, length)
		skip(length + 1 + padCount(length + 1))
		return result
	}

	fun readInt32(): Int {
		val start = index
		skip(4)
		var result = 0
		for (i in start..start + 3) {
			result = (result shl 8) + data[i]
		}
		return result
	}

	fun readLong64(): Long {
		val start = index
		skip(8)
		var result = 0L
		for (i in start..start + 3) {
			result = result shl 8 + data[i]
		}
		return result
	}

	fun readFloat32() =
		Float.fromBits(readInt32())
}