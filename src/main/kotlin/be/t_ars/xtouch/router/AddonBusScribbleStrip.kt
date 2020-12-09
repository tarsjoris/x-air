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
	private data class XR18ChannelConfig(
		var name: String?,
		var color: EScribbleColor?,
		var secondLineInverted: Boolean?
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

		fun toXTouchEvent(channel: Int, line1: String) =
			if (this.color != null)
				ScribbleStripEvent(
					channel,
					this.color ?: EScribbleColor.BLACK,
					this.secondLineInverted ?: false,
					line1,
					this.name ?: ""
				) else null
	}

	// XR18 state
	private val xr18channels = Array(XR18OSCAPI.CHANNEL_COUNT) {
		XR18ChannelConfig(null, null, null)
	}
	private val xr18buses = Array(XR18OSCAPI.BUS_COUNT) {
		XR18ChannelConfig(null, null, null)
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
	override fun channelName(channel: Int, name: String) {
		xr18channels[channel - 1].name = name
	}

	override fun channelColor(channel: Int, color: Int) {
		xr18channels[channel - 1].updateColor(color)
	}

	override fun busName(bus: Int, name: String) {
		xr18buses[bus - 1].name = name
	}

	override fun busColor(bus: Int, color: Int) {
		xr18buses[bus - 1].updateColor(color)
	}

	private fun isInOverrideMode() =
		sessionState.currentOutput != XTouchSessionState.OUTPUT_MAINLR

	private fun getOverrideConfigCurrentXR18Channel(channel: Int) =
		if (channelKnobPressed != channel) {
			when (sessionState.currentBank) {
				1 -> xr18channels[channel - 1]
				2 -> xr18channels[channel + 8 - 1]
				3 -> when (channel) {
					1 -> xr18channels[XR18OSCAPI.AUX_CHANNEL - 1]
					else -> null
				}
				4 -> when (channel) {
					in 1..6 -> xr18buses[channel - 1]
					else -> null
				}
				else -> null
			}
		} else null

	private fun getOverridenConfigEvent(channel: Int) =
		getOverrideConfigCurrentXR18Channel(channel)
			?.toXTouchEvent(channel, xtouchChannels[channel - 1]?.line1 ?: "")

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
			if (isInOverrideMode()) {
				channelKnobPressed = if (down) knob else null
				val event = if (down) {
					xtouchChannels[knob - 1]
				} else {
					getOverridenConfigEvent(knob)
				}
				if (event != null) {
					sendToXTouch(partial(event, IXR18Events::setScribbleTrip))
				}
			}
		}

		override fun nextBankPressed(down: Boolean) =
			bankChanged(down)

		override fun previousBankPressed(down: Boolean) =
			bankChanged(down)

		private fun bankChanged(down: Boolean) {
			if (isInOverrideMode() && !down) {
				val events = (1..XctlUtil.CHANNEL_COUNT).mapNotNull { channel ->
					if (channel != channelKnobPressed) {
						getOverridenConfigEvent(channel)
					} else null
				}.toTypedArray()
				sendToXTouch(partial(events, IXR18Events::setScribbleTrips))
			}
		}
	}

	inner class XR18Listener : AbstractAddonXR18Listener() {
		override fun setScribbleTrips(scribbleStripEvents: Array<ScribbleStripEvent>) {
			if (isInOverrideMode()) {
				val newEvents = scribbleStripEvents.map { event ->
					xtouchChannels[event.channel - 1] = event
					getOverridenConfigEvent(event.channel) ?: event
				}
				nextEvent = { it.setScribbleTrips(newEvents.toTypedArray()) }
			}
		}
	}
}