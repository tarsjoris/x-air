package be.t_ars.xtouch.router

import be.t_ars.xtouch.osc.XR18OSCAPI
import be.t_ars.xtouch.session.XTouchSessionState
import be.t_ars.xtouch.util.Listeners
import be.t_ars.xtouch.util.getBoolean
import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IConnectionToXR18
import be.t_ars.xtouch.xctl.IConnectionToXTouch
import be.t_ars.xtouch.xctl.IXR18Events
import be.t_ars.xtouch.xctl.IXTouchEvents
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import java.net.InetAddress
import java.util.*

class ProxyRouter(
	private val xr18Address: InetAddress,
	sessionState: XTouchSessionState,
	private val connectionToXTouch: IConnectionToXTouch,
	private val connectionToXR18: IConnectionToXR18,
	properties: Properties
) {
	private val connectionListeners = Listeners<IXctlConnectionListener>()
	private val xTouchListeners = Listeners<IXTouchEvents>()
	private val xr18Listeners = Listeners<IXR18Events>()

	private val addons: Array<IAddon>

	private val xr18OSCAPI: Lazy<XR18OSCAPI> = lazy(this::createXR18API)

	init {
		val addonBuilder = mutableListOf<IAddon>()

		if (properties.getBoolean("router.mutebuttons")) {
			val addonMuteButtons = AddonMuteButtons(xr18OSCAPI.value, connectionToXTouch)
			xr18OSCAPI.value.addListener(addonMuteButtons)
			addonBuilder.add(addonMuteButtons)
		}

		if (properties.getBoolean("router.busorder")) {
			addonBuilder.add(AddonBusOrder())
		}

		if (properties.getBoolean("router.linkbusleds")) {
		}

		if (properties.getBoolean("router.busscribblestrip")) {
			val addonBusScribbleStrip = AddonBusScribbleStrip(
				xr18OSCAPI.value,
				connectionToXTouch,
				sessionState
			)
			xr18OSCAPI.value.addListener(addonBusScribbleStrip)
			addonBuilder.add(addonBusScribbleStrip)
		}

		if (properties.getBoolean("router.channelcue")) {
		}

		addons = addonBuilder.toTypedArray()
	}

	fun stop() {
		if (xr18OSCAPI.isInitialized()) {
			xr18OSCAPI.value.stop()
		}
	}

	fun addConnectionListener(listener: IXctlConnectionListener) {
		connectionListeners.add(listener)
	}

	fun addXTouchListener(listener: IXTouchEvents) {
		xTouchListeners.add(listener)
	}

	fun addXR18Listener(listener: IXR18Events) {
		xr18Listeners.add(listener)
	}

	fun routeConnectionEvent(event: Event<IXctlConnectionListener>) {
		addons.forEach { it.processConnectionEvent(event) }
		connectionListeners.broadcast(event)
	}

	fun routeEventFromXTouch(initialEvent: Event<IXTouchEvents>) {
		val startEvent: Event<IXTouchEvents>? = initialEvent
		val resultEvent = addons.fold(startEvent) { event, addon ->
			event?.let(addon::processEventFromXTouch)
		}
		resultEvent?.also(xTouchListeners::broadcast)
		// forward event to device last to allow states to update
		resultEvent?.also { it(connectionToXR18) }
	}

	fun routeEventFromXR18(initialEvent: Event<IXR18Events>) {
		val startEvent: Event<IXR18Events>? = initialEvent
		val resultEvent = addons.fold(startEvent) { event, addon ->
			event?.let(addon::processEventFromXR18)
		}
		resultEvent?.also(xr18Listeners::broadcast)
		// forward event to device last to allow states to update
		resultEvent?.also { it(connectionToXTouch) }
	}

	private fun createXR18API(): XR18OSCAPI {
		val xr18API = XR18OSCAPI(xr18Address)
		Thread(xr18API::run).start()
		return xr18API
	}
}