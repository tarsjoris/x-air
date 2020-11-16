package be.t_ars.xtouch.router

import be.t_ars.xtouch.osc.XR18API
import be.t_ars.xtouch.settings.ISettingsManager
import be.t_ars.xtouch.util.Listeners
import be.t_ars.xtouch.util.getBoolean
import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IConnectionToXR18
import be.t_ars.xtouch.xctl.IConnectionToXTouch
import be.t_ars.xtouch.xctl.IXR18Events
import be.t_ars.xtouch.xctl.IXTouchEvents
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import java.net.InetAddress

class ProxyRouter(
	private val xr18Address: InetAddress,
	connectionToXTouch: IConnectionToXTouch,
	connectionToXR18: IConnectionToXR18,
	settingsManager: ISettingsManager
) {
	private val connectionListeners = Listeners<IXctlConnectionListener>()
	private val xTouchListeners = Listeners<IXTouchEvents>()
	private val xr18Listeners = Listeners<IXR18Events>()

	private val addons: Array<IAddon>

	private val xr18API: Lazy<XR18API> = lazy(this::createXR18API)

	init {
		val addonBuilder = mutableListOf<IAddon>()
		val properties = settingsManager.loadProperties("router")

		if (properties.getBoolean("router.mutebuttons")) {
			val addonMuteButtons = AddonMuteButtons(xr18API.value, connectionToXTouch)
			xr18API.value.addListener(addonMuteButtons)
			addonBuilder.add(addonMuteButtons)
		}

		if (properties.getBoolean("router.busorder")) {
			addonBuilder.add(AddonBusOrder())
		}

		addons = addonBuilder.toTypedArray()

		xTouchListeners.add(connectionToXR18)
		xr18Listeners.add(connectionToXTouch)
	}

	fun stop() {
		if (xr18API.isInitialized()) {
			xr18API.value.stop()
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
	}

	fun routeEventFromXR18(initialEvent: Event<IXR18Events>) {
		val startEvent: Event<IXR18Events>? = initialEvent
		val resultEvent = addons.fold(startEvent) { event, addon ->
			event?.let(addon::processEventFromXR18)
		}
		resultEvent?.also(xr18Listeners::broadcast)
	}

	private fun createXR18API(): XR18API {
		val xr18API = XR18API(xr18Address)
		Thread(xr18API::run).start()
		return xr18API
	}
}