package be.t_ars.xtouch.router

import be.t_ars.xtouch.osc.IOSCListener
import be.t_ars.xtouch.osc.XR18OSCAPI
import be.t_ars.xtouch.session.IXTouchSessionListener
import be.t_ars.xtouch.session.XTouchSessionState
import be.t_ars.xtouch.xctl.AbstractButtonLEDEvent
import be.t_ars.xtouch.xctl.ButtonLEDEvent
import be.t_ars.xtouch.xctl.EButton
import be.t_ars.xtouch.xctl.ELEDMode
import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IXR18Events
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddonLinkBusLEDs(
	private val xr18OSCAPI: XR18OSCAPI
) : AbstractAddon(), IOSCListener, IXTouchSessionListener {
	private val connectionListener = ConnectionListener()
	private val xr18Listener = XR18Listener()

	// Session state
	private var currentOutput = XTouchSessionState.OUTPUT_MAINLR

	// XR18 state
	private var busLinked = mutableMapOf<IOSCListener.EBusLink, Boolean>().also {
		for (busLink in IOSCListener.EBusLink.values()) {
			it[busLink] = false
		}
	}

	// Router events
	override fun processConnectionEvent(event: Event<IXctlConnectionListener>) {
		event(connectionListener)
	}

	override fun getNextXR18Event(event: Event<IXR18Events>) =
		xr18Listener.processEvent(event)

	// Session events
	override fun selectionChanged(
		output: Int,
		channel: Int,
		encoder: XTouchSessionState.EEncoder?,
		dynamicEncoder: XTouchSessionState.EDynamicEncoder
	) {
		if (currentOutput != output) {
			currentOutput = output
		}
	}

	// XR18 events
	override suspend fun busLink(busLink: IOSCListener.EBusLink, on: Boolean) {
		busLinked[busLink] = on
		when (currentOutput) {
			XTouchSessionState.OUTPUT_BUS1 -> setLED(EButton.AUTOMATION_WRITE, on)
			XTouchSessionState.OUTPUT_BUS2 -> setLED(EButton.AUTOMATION_READ, on)
			XTouchSessionState.OUTPUT_BUS3 -> setLED(EButton.AUTOMATION_TOUCH, on)
			XTouchSessionState.OUTPUT_BUS4 -> setLED(EButton.AUTOMATION_TRIM, on)
			XTouchSessionState.OUTPUT_BUS5 -> setLED(EButton.AUTOMATION_GROUP, on)
			XTouchSessionState.OUTPUT_BUS6 -> setLED(EButton.AUTOMATION_LATCH, on)
		}
	}

	private fun setLED(button: EButton, on: Boolean) =
		sendToXTouch {
			it.setButtonLEDs(
				arrayOf(
					ButtonLEDEvent(
						button,
						if (on) ELEDMode.ON else ELEDMode.OFF
					)
				)
			)
		}

	inner class ConnectionListener : IXctlConnectionListener {
		override fun connected() {
			GlobalScope.launch {
				delay(1000)
				for (busLink in IOSCListener.EBusLink.values()) {
					xr18OSCAPI.requestBusLink(busLink)
				}
			}
		}
	}

	inner class XR18Listener : AbstractAddonXR18Listener() {
		private fun hasEvent(buttonLEDEvents: Array<AbstractButtonLEDEvent>, button: EButton, mode: ELEDMode) =
			buttonLEDEvents.any {
				it is ButtonLEDEvent &&
						it.button == button &&
						it.mode == mode
			}

		private fun overrideEvent(
			buttonLEDEvents: Array<AbstractButtonLEDEvent>,
			button: EButton,
			mode: ELEDMode
		): Array<AbstractButtonLEDEvent> {
			val matchesButton: (AbstractButtonLEDEvent) -> Boolean = {
				it is ButtonLEDEvent && it.button == button
			}
			val newEvent = ButtonLEDEvent(button, mode)
			return if (buttonLEDEvents.any(matchesButton)) {
				buttonLEDEvents.map {
					if (matchesButton(it)) newEvent else it
				}.toTypedArray()
			} else {
				Array(buttonLEDEvents.size + 1) {
					if (it < buttonLEDEvents.size) buttonLEDEvents[it] else newEvent
				}
			}
		}

		private fun overrideEvents(
			buttonLEDEvents: Array<AbstractButtonLEDEvent>,
			busLink: IOSCListener.EBusLink,
			button1: EButton,
			button2: EButton
		) =
			if (busLinked[busLink] == true) {
				when {
					hasEvent(buttonLEDEvents, button1, ELEDMode.ON) -> {
						overrideEvent(buttonLEDEvents, button2, ELEDMode.ON)
					}
					hasEvent(buttonLEDEvents, button2, ELEDMode.ON) -> {
						overrideEvent(buttonLEDEvents, button1, ELEDMode.ON)
					}
					hasEvent(buttonLEDEvents, button1, ELEDMode.OFF) -> {
						overrideEvent(buttonLEDEvents, button2, ELEDMode.OFF)
					}
					hasEvent(buttonLEDEvents, button2, ELEDMode.OFF) -> {
						overrideEvent(buttonLEDEvents, button1, ELEDMode.OFF)
					}
					else -> buttonLEDEvents
				}
			} else {
				buttonLEDEvents
			}

		override fun setButtonLEDs(buttonLEDEvents: Array<AbstractButtonLEDEvent>) {
			var newEvents = buttonLEDEvents
			newEvents = overrideEvents(
				newEvents,
				IOSCListener.EBusLink.BUS12,
				EButton.AUTOMATION_READ,
				EButton.AUTOMATION_WRITE
			)
			newEvents = overrideEvents(
				newEvents,
				IOSCListener.EBusLink.BUS34,
				EButton.AUTOMATION_TRIM,
				EButton.AUTOMATION_TOUCH
			)
			newEvents = overrideEvents(
				newEvents,
				IOSCListener.EBusLink.BUS56,
				EButton.AUTOMATION_LATCH,
				EButton.AUTOMATION_GROUP
			)
			nextEvent = { it.setButtonLEDs(newEvents) }
		}
	}
}