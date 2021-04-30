package be.t_ars.xtouch.router

import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IXR18Events
import be.t_ars.xtouch.xctl.IXTouchEvents

abstract class AbstractAddon : IAddon {
	var nextXTouchProcessor: ((Event<IXTouchEvents>) -> Unit)? = null
	var nextXR18Processor: ((Event<IXR18Events>) -> Unit)? = null

	override fun processEventFromXTouch(event: Event<IXTouchEvents>) {
		getNextXTouchEvent(event)?.also(this::sendToXR18)
	}

	open fun getNextXTouchEvent(event: Event<IXTouchEvents>): Event<IXTouchEvents>? =
		event

	fun sendToXR18(event: Event<IXTouchEvents>) {
		nextXTouchProcessor?.invoke(event)
	}

	override fun processEventFromXR18(event: Event<IXR18Events>) {
		getNextXR18Event(event)?.also(this::sendToXTouch)
	}

	open fun getNextXR18Event(event: Event<IXR18Events>): Event<IXR18Events>? =
		event

	fun sendToXTouch(event: Event<IXR18Events>) {
		nextXR18Processor?.invoke(event)
	}
}