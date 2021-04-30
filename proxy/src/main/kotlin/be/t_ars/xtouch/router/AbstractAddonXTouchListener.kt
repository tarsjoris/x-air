package be.t_ars.xtouch.router

import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IXTouchEvents
import be.t_ars.xtouch.xctl.IXTouchListener

abstract class AbstractAddonXTouchListener : IXTouchListener {
	protected var nextEvent: Event<IXTouchEvents>? = null

	fun processEvent(event: Event<IXTouchEvents>): Event<IXTouchEvents>? {
		nextEvent = event
		event(this)
		return nextEvent
	}
}