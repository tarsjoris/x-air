package be.t_ars.xtouch.webrelay

import be.t_ars.xtouch.osc.IOSCListener
import kotlin.math.roundToInt

private val BUS_REGEX = Regex("[1-6]")

private fun levelFloatToInt(level: Float) =
	(level * 80).roundToInt()

private fun levelIntToFloat(level: Int) =
	level.toFloat() / 80f

class RelayMonitorMixConnection(private val state: WebRelayState, private val send: suspend (String) -> Unit) :
	IOSCListener {

	@Volatile
	private var selectedBus: Int? = null

	suspend fun init() {
		state.sendInitialGeneralState(this)
	}

	suspend fun accept(data: String) {
		val parts = data.split('|')
		if (parts.isNotEmpty()) {
			when (parts[0]) {
				"select" -> acceptSelect(parts)
			}
		}
	}

	private suspend fun acceptSelect(parts: List<String>) {
		if (parts.size == 2 && BUS_REGEX.matches(parts[1])) {
			val bus = parts[1][0] - '0'
			selectedBus = bus
			state.sendInitialFaderLevels(this, bus)
		}
	}

	override suspend fun busName(bus: Int, name: String) {
		send("bus/name|$bus|$name")
	}

	override suspend fun busColor(bus: Int, color: Int) {
		send("bus/color|$bus|$color")
	}

	override suspend fun busLevel(bus: Int, level: Float) {
		if (bus == selectedBus) {
			val levelInt = levelFloatToInt(level)
			send("bus/level|$levelInt")
		}
	}

	override suspend fun channelName(channel: Int, name: String) {
		send("ch/name|$channel|$name")
	}

	override suspend fun channelColor(channel: Int, color: Int) {
		send("ch/color|$channel|$color")
	}

	override suspend fun channelBusLevel(channel: Int, bus: Int, level: Float) {
		if (bus == selectedBus) {
			val levelInt = levelFloatToInt(level)
			send("ch/level|$channel|$levelInt")
		}
	}
}