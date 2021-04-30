package be.t_ars.xtouch.webrelay

import be.t_ars.xtouch.osc.IOSCListener
import be.t_ars.xtouch.osc.XR18OSCAPI

private data class BusConfig(
	@Volatile
	var name: String? = null,

	@Volatile
	var color: Int? = null,

	@Volatile
	var level: Float? = null
)

private class Level(
	@Volatile
	var level: Float? = null
)

private class ChannelConfig(
	@Volatile
	var name: String? = null,

	@Volatile
	var color: Int? = null,

	val busLevels: Array<Level> = Array(XR18OSCAPI.BUS_COUNT) {
		Level()
	}
)

class WebRelayState(private val xrR18OSCAPI: XR18OSCAPI) : IOSCListener {
	private val busConfigs: Array<BusConfig> = Array(XR18OSCAPI.BUS_COUNT) {
		BusConfig()
	}

	private val channelConfigs: Array<ChannelConfig> = Array(XR18OSCAPI.CHANNEL_COUNT) {
		ChannelConfig()
	}

	init {
		//loadMockdata()
	}

	private fun loadMockdata()
	{
		busConfigs.forEachIndexed {index, busConfig ->
			busConfig.name = "bus${index + 1}"
			busConfig.color = index
			busConfig.level = 0.2f
		}
		channelConfigs.forEachIndexed { index, channelConfig ->
			channelConfig.name = "channel${index + 1}"
			channelConfig.color = index
			channelConfig.busLevels.forEach { it.level = 0.7f }
		}
	}

	suspend fun sendInitialGeneralState(listener: IOSCListener) {
		busConfigs.forEachIndexed { i, config ->
			val bus = i + 1
			val name = config.name
			if (name != null) {
				listener.busName(bus, name)
			} else {
				xrR18OSCAPI.requestBusName(bus)
			}

			val color = config.color
			if (color != null) {
				listener.busColor(bus, color)
			} else {
				xrR18OSCAPI.requestBusColor(bus)
			}
		}

		channelConfigs.forEachIndexed { i, config ->
			val channel = i + 1
			val name = config.name
			if (name != null) {
				listener.channelName(channel, name)
			} else {
				xrR18OSCAPI.requestChannelName(channel)
			}

			val color = config.color
			if (color != null) {
				listener.channelColor(channel, color)
			} else {
				xrR18OSCAPI.requestChannelColor(channel)
			}
		}
	}

	suspend fun sendInitialFaderLevels(listener: IOSCListener, bus: Int) {
		val busLevel = busConfigs[bus - 1].level
		if (busLevel != null) {
			listener.busLevel(bus, busLevel)
		} else {
			xrR18OSCAPI.requestBusLevel(bus)
		}

		channelConfigs.forEachIndexed { i, config ->
			val channel = i + 1
			val level = config.busLevels[bus - 1].level
			if (level != null) {
				listener.channelBusLevel(channel, bus, level)
			} else {
				xrR18OSCAPI.requestChannelBusLevel(channel, bus)
			}
		}
	}

	override suspend fun busName(bus: Int, name: String) {
		busConfigs[bus - 1].name = name
	}

	override suspend fun busColor(bus: Int, color: Int) {
		busConfigs[bus - 1].color = color
	}

	override suspend fun busLevel(bus: Int, level: Float) {
		busConfigs[bus - 1].level = level
	}

	override suspend fun channelName(channel: Int, name: String) {
		channelConfigs[channel - 1].name = if (name.isEmpty())
			channel.toString().padStart(2, '0')
		else
			name
	}

	override suspend fun channelColor(channel: Int, color: Int) {
		channelConfigs[channel - 1].color = color
	}

	override suspend fun channelBusLevel(channel: Int, bus: Int, level: Float) {
		channelConfigs[channel - 1].busLevels[bus - 1].level = level
	}
}