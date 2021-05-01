package be.t_ars.xtouch.webrelay

import be.t_ars.xtouch.osc.IOSCListener
import be.t_ars.xtouch.osc.XR18OSCAPI
import kotlin.math.roundToInt

private val BUS_REGEX = Regex("[1-6]")
private val CHANNEL_REGEX = Regex("[1-9]|1[0-7]")
private val CHANNEL_LEVEL = Regex("[0-9]|[1-7][0-9]|80")

private fun levelFloatToInt(level: Float) =
	(level * 80).roundToInt()

private fun levelIntToFloat(level: Int) =
	level.toFloat() / 80f

class RelayMonitorMixConnection(
	private val state: WebRelayState,
	private val sendToClient: suspend (String) -> Unit,
	private val xr18OSCAPI: XR18OSCAPI
) :	IOSCListener {

	@Volatile
	private var selectedBus: Int? = null
	private var batchMode = false
	private val batchBuffer = StringBuilder()

	suspend fun init() {
		startBatch()
		state.sendInitialGeneralState(this)
		endBatch()
	}

	suspend fun accept(data: String) {
		val parts = data.split('|')
		if (parts.isNotEmpty()) {
			when (parts[0]) {
				"select" -> acceptSelect(parts)
				"channel" -> acceptChannel(parts)
			}
		}
	}

	private suspend fun acceptSelect(parts: List<String>) {
		if (parts.size == 2 && BUS_REGEX.matches(parts[1])) {
			val bus = parts[1].toInt()
			selectedBus = bus
			startBatch()
			state.sendInitialFaderLevels(this, bus)
			endBatch()
		}
	}

	private fun acceptChannel(parts: List<String>) {
		if (parts.size == 3 && CHANNEL_REGEX.matches(parts[1]) && CHANNEL_LEVEL.matches(parts[2])) {
			val channel = parts[1].toInt()
			val level = levelIntToFloat(parts[2].toInt())
			selectedBus?.also { bus ->
				xr18OSCAPI.setChannelBusLevel(channel, bus, level)
			}
		}
	}

	private fun startBatch() {
		synchronized(this) {
			batchMode = true
			batchBuffer.clear()
		}
	}

	private suspend fun endBatch() {
		var message = synchronized(this) {
			val result = batchBuffer.toString()
			batchBuffer.clear()
			batchMode = false
			result
		}
		sendToClient(message)
	}

	override suspend fun busName(bus: Int, name: String) {
		val resolveName = name.ifEmpty { "Bus $bus" }
		scheduleSend("bus/name|$bus|$resolveName")
	}

	override suspend fun busColor(bus: Int, color: Int) {
		scheduleSend("bus/color|$bus|$color")
	}

	override suspend fun busLevel(bus: Int, level: Float) {
		if (bus == selectedBus) {
			val levelInt = levelFloatToInt(level)
			scheduleSend("bus/level|$levelInt")
		}
	}

	override suspend fun channelName(channel: Int, name: String) {
		val resolvedName = name.ifEmpty { channel.toString().padStart(2, '0') }
		scheduleSend("ch/name|$channel|$resolvedName")
	}

	override suspend fun channelColor(channel: Int, color: Int) {
		scheduleSend("ch/color|$channel|$color")
	}

	override suspend fun channelBusLevel(channel: Int, bus: Int, level: Float) {
		if (bus == selectedBus) {
			val levelInt = levelFloatToInt(level)
			scheduleSend("ch/level|$channel|$levelInt")
		}
	}

	private suspend fun scheduleSend(message: String) {
		val sendImmediate = synchronized(this) {
			if (batchMode) {
				if (batchBuffer.isNotEmpty()) {
					batchBuffer.append(";")
				}
				batchBuffer.append(message)
				false
			} else {
				true
			}
		}
		if (sendImmediate) {
			sendToClient(message)
		}
	}
}