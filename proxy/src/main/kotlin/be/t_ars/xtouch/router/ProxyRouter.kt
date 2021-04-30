package be.t_ars.xtouch.router

import be.t_ars.xtouch.osc.XR18OSCAPI
import be.t_ars.xtouch.session.XTouchSessionState
import be.t_ars.xtouch.util.Listeners
import be.t_ars.xtouch.util.getBoolean
import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.EventProcessor
import be.t_ars.xtouch.xctl.IConnectionToXR18
import be.t_ars.xtouch.xctl.IConnectionToXTouch
import be.t_ars.xtouch.xctl.IXR18Events
import be.t_ars.xtouch.xctl.IXTouchEvents
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import java.net.InetAddress
import java.util.*

class ProxyRouter(
	var xr18Address: InetAddress,
	sessionState: XTouchSessionState,
	private val connectionToXTouch: IConnectionToXTouch,
	private val connectionToXR18: IConnectionToXR18,
	xr18OSCAPI: XR18OSCAPI,
	properties: Properties
) {
	private val xTouchListeners = Listeners<IXTouchEvents>()

	private val addons: Array<IAddon>
	private val firstXTouchListener: EventProcessor<IXTouchEvents>
	private val firstXR18Listener: EventProcessor<IXR18Events>

	init {
		val addonBuilder = mutableListOf<AbstractAddon>()

		if (properties.getBoolean("router.mutebuttons")) {
			val addonMuteButtons = AddonMuteButtons(xr18OSCAPI)
			xr18OSCAPI.addListener(addonMuteButtons)
			addonBuilder.add(addonMuteButtons)
		}

		if (properties.getBoolean("router.busorder")) {
			addonBuilder.add(AddonBusOrder())
		}

		if (properties.getBoolean("router.busscribblestrip")) {
			val addonBusScribbleStrip = AddonBusScribbleStrip(
				xr18OSCAPI,
				sessionState
			)
			xr18OSCAPI.addListener(addonBusScribbleStrip)
			addonBuilder.add(addonBusScribbleStrip)
		}

		if (properties.getBoolean("router.linkbusleds")) {
			val addonLinkBusLEDs = AddonLinkBusLEDs(xr18OSCAPI)
			xr18OSCAPI.addListener(addonLinkBusLEDs)
			sessionState.addListener(addonLinkBusLEDs)
			addonBuilder.add(addonLinkBusLEDs)
		}

		if (properties.getBoolean("router.channelcue")) {
			val addonChannelCue = AddonChannelCue(xr18OSCAPI)
			xr18OSCAPI.addListener(addonChannelCue)
			sessionState.addListener(addonChannelCue)
			addonBuilder.add(addonChannelCue)
		}

		if (properties.getBoolean("router.sinedemo")) {
			val addonSineDemo = AddonSineDemo()
			addonBuilder.add(addonSineDemo)
		}

		addons = addonBuilder.toTypedArray()

		if (addonBuilder.isEmpty()) {
			firstXTouchListener = this::sendEventToXR18
			firstXR18Listener = this::sendEventToXTouch
		} else {
			firstXTouchListener = addonBuilder.first()::processEventFromXTouch
			firstXR18Listener = addonBuilder.last()::processEventFromXR18

			addonBuilder.first().nextXR18Processor = this::sendEventToXTouch
			addonBuilder.last().nextXTouchProcessor = this::sendEventToXR18

			for (i in 1 until addonBuilder.size) {
				addonBuilder[i - 1].nextXTouchProcessor = addonBuilder[i]::processEventFromXTouch
				addonBuilder[i].nextXR18Processor = addonBuilder[i - 1]::processEventFromXR18
			}
		}
	}

	fun addXTouchListener(listener: IXTouchEvents) {
		xTouchListeners.add(listener)
	}

	fun routeConnectionEvent(event: Event<IXctlConnectionListener>) {
		addons.forEach { it.processConnectionEvent(event) }
	}

	fun routeEventFromXTouch(event: Event<IXTouchEvents>) {
		firstXTouchListener(event)
	}

	private fun sendEventToXR18(event: Event<IXTouchEvents>) {
		xTouchListeners.broadcast(event)
		// forward event to device last to allow states to update
		event(connectionToXR18)
	}

	fun routeEventFromXR18(event: Event<IXR18Events>) {
		firstXR18Listener(event)
	}

	private fun sendEventToXTouch(event: Event<IXR18Events>) {
		// forward event to device last to allow states to update
		event(connectionToXTouch)
	}
}