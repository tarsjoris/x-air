package be.t_ars.xtouch.router

import be.t_ars.xtouch.util.partial
import be.t_ars.xtouch.xctl.AbstractButtonLEDEvent
import be.t_ars.xtouch.xctl.ButtonLEDEvent
import be.t_ars.xtouch.xctl.EButton
import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IXR18Events
import be.t_ars.xtouch.xctl.IXTouchEvents

class AddonBusOrder : AbstractAddon() {
	private val xTouchListener = XTouchListener()
	private val xr18Listener = XR18Listener()

	// Router events
	override fun getNextXTouchEvent(event: Event<IXTouchEvents>) =
		xTouchListener.processEvent(event)

	override fun getNextXR18Event(event: Event<IXR18Events>) =
		xr18Listener.processEvent(event)

	inner class XTouchListener : AbstractAddonXTouchListener() {
		override fun automationPressed(automation: Int, down: Boolean) {
			nextEvent = partial(
				when (automation) {
					2 -> 3
					3 -> 5
					4 -> 2
					5 -> 4
					else -> automation
				},
				down,
				IXTouchEvents::automationPressed
			)
		}
	}

	inner class XR18Listener : AbstractAddonXR18Listener() {
		override fun setButtonLEDs(buttonLEDEvents: Array<AbstractButtonLEDEvent>) {
			if (buttonLEDEvents.any(this::isRemappadBusEvent)) {
				nextEvent = partial(buttonLEDEvents.map { event ->
					if (event is ButtonLEDEvent) {
						when (event.button) {
							EButton.AUTOMATION_WRITE -> ButtonLEDEvent(EButton.AUTOMATION_TOUCH, event.mode)
							EButton.AUTOMATION_TRIM -> ButtonLEDEvent(EButton.AUTOMATION_WRITE, event.mode)
							EButton.AUTOMATION_TOUCH -> ButtonLEDEvent(EButton.AUTOMATION_LATCH, event.mode)
							EButton.AUTOMATION_LATCH -> ButtonLEDEvent(EButton.AUTOMATION_TRIM, event.mode)
							else -> event
						}
					} else {
						event
					}
				}.toTypedArray(), IXR18Events::setButtonLEDs)
			}
		}

		private fun isRemappadBusEvent(event: AbstractButtonLEDEvent) =
			event is ButtonLEDEvent && event.button in EButton.AUTOMATION_WRITE..EButton.AUTOMATION_LATCH
	}
}