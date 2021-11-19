package be.t_ars.xtouch.router

import be.t_ars.xtouch.osc.IOSCListener
import be.t_ars.xtouch.osc.XR18OSCAPI
import be.t_ars.xtouch.session.XTouchSessionState
import be.t_ars.xtouch.util.partial
import be.t_ars.xtouch.xctl.EScribbleColor
import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IXR18Events
import be.t_ars.xtouch.xctl.IXTouchEvents
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import be.t_ars.xtouch.xctl.ScribbleStripEvent
import be.t_ars.xtouch.xctl.XctlUtil
import be.t_ars.xtouch.xctl.setScribbleTrip
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddonBusScribbleStrip(
	private val xr18OSCAPI: XR18OSCAPI,
	private val sessionState: XTouchSessionState
) : AbstractAddon(), IOSCListener {
	private abstract class XR18Config(
		@Volatile
		var name: String? = null,
		@Volatile
		var color: EScribbleColor? = null,
		@Volatile
		var secondLineInverted: Boolean? = null
	) {
		fun updateColor(color: Int) {
			this.color = when (color.rem(8)) {
				0 -> EScribbleColor.WHITE
				1 -> EScribbleColor.RED
				2 -> EScribbleColor.GREEN
				3 -> EScribbleColor.YELLOW
				4 -> EScribbleColor.BLUE
				5 -> EScribbleColor.PINK
				6 -> EScribbleColor.CYAN
				else -> EScribbleColor.WHITE
			}
			this.secondLineInverted = color in 1..8
		}
	}

	private inner class XR18ChannelConfig : XR18Config() {
		fun toXTouchEvent(channel: Int) =
			if (this.color != null)
				ScribbleStripEvent(
					channel,
					this.color ?: EScribbleColor.BLACK,
					this.secondLineInverted ?: false,
					xtouchChannels[channel - 1]?.line1 ?: "",
					this.name ?: ""
				)
			else
				null
	}

	private inner class XR18BusConfig : XR18Config() {
		fun toXTouchEvent(channel: Int) =
			if (this.color != null)
				ScribbleStripEvent(
					channel,
					this.color ?: EScribbleColor.BLACK,
					this.secondLineInverted ?: false,
					this.name ?: xtouchChannels[channel - 1]?.line1 ?: "",
					xtouchChannels[channel - 1]?.line2 ?: ""
				)
			else
				null
	}

	// XR18 state
	private val xr18channels = Array(XR18OSCAPI.CHANNEL_COUNT) {
		XR18ChannelConfig()
	}
	private val xr18buses = Array(XR18OSCAPI.BUS_COUNT) {
		XR18BusConfig()
	}

	// XTouch state
	private val xtouchChannels = Array<ScribbleStripEvent?>(XctlUtil.CHANNEL_COUNT) {
		null
	}
	private var channelKnobPressed: Int? = null

	private val connectionListener = ConnectionListener()
	private val xTouchListener = XTouchistener()
	private val xr18Listener = XR18Listener()

	// Router events
	override fun processConnectionEvent(event: Event<IXctlConnectionListener>) =
		event(connectionListener)

	override fun getNextXTouchEvent(event: Event<IXTouchEvents>) =
		xTouchListener.processEvent(event)

	override fun getNextXR18Event(event: Event<IXR18Events>) =
		xr18Listener.processEvent(event)

	// XR18 events
	override suspend fun channelName(channel: Int, name: String) {
		xr18channels[channel - 1].name = name.ifBlank { null }
	}

	override suspend fun channelColor(channel: Int, color: Int) {
		xr18channels[channel - 1].updateColor(color)
	}

	override suspend fun busName(bus: Int, name: String) {
		xr18buses[bus - 1].name = name.ifBlank { null }
	}

	override suspend fun busColor(bus: Int, color: Int) {
		xr18buses[bus - 1].updateColor(color)
	}

	private fun isInOverrideChannelMode() =
		sessionState.currentOutput != XTouchSessionState.OUTPUT_MAINLR

	private fun isInOverrideBusMode() =
		sessionState.currentEncoder == XTouchSessionState.EEncoder.BUS

	private fun getOverrideConfigCurrentXR18Channel(channel: Int) =
		if (channelKnobPressed != channel) {
			when (sessionState.currentBank) {
				1 -> xr18channels[channel - 1]
				2 -> xr18channels[channel + 8 - 1]
				3 -> when (channel) {
					1 -> xr18channels[XR18OSCAPI.AUX_CHANNEL - 1]
					else -> null
				}
				else -> null
			}
		} else null

	private fun getOverridenChannelConfigEvent(channel: Int) =
		getOverrideConfigCurrentXR18Channel(channel)
			?.toXTouchEvent(channel)

	private fun getOverrideConfigBusChannel(channel: Int) =
				when (channel) {
					in 3..8 -> xr18buses[channel - 3]
					else -> null
				}

	private fun getOverridenBusConfigEvent(channel: Int) =
		getOverrideConfigBusChannel(channel)
			?.toXTouchEvent(channel)

	inner class ConnectionListener : IXctlConnectionListener {
		override fun connected() {
			GlobalScope.launch {
				delay(1000)
				xr18OSCAPI.requestConfigs()
			}
		}
	}

	inner class XTouchistener : AbstractAddonXTouchListener() {
		override fun knobPressed(knob: Int, down: Boolean) {
			if (isInOverrideChannelMode()) {
				channelKnobPressed = if (down) knob else null
				val event = if (down) {
					xtouchChannels[knob - 1]
				} else {
					getOverridenChannelConfigEvent(knob)
				}
				if (event != null) {
					sendToXTouch(partial(event, IXR18Events::setScribbleTrip))
				}
			}
		}
	}

	inner class XR18Listener : AbstractAddonXR18Listener() {
		override fun setScribbleTrips(scribbleStripEvents: Array<ScribbleStripEvent>) {
			if (isInOverrideChannelMode()) {
				val newEvents = scribbleStripEvents.map { event ->
					xtouchChannels[event.channel - 1] = event
					getOverridenChannelConfigEvent(event.channel) ?: event
				}
				nextEvent = { it.setScribbleTrips(newEvents.toTypedArray()) }
			} else if (isInOverrideBusMode()) {
				val newEvents = scribbleStripEvents.map { event ->
					xtouchChannels[event.channel - 1] = event
					getOverridenBusConfigEvent(event.channel) ?: event
				}
				nextEvent = { it.setScribbleTrips(newEvents.toTypedArray()) }
			}
		}
	}
}