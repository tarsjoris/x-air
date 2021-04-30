package be.t_ars.xtouch.router

import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IXR18Events
import be.t_ars.xtouch.xctl.IXR18Listener

abstract class AbstractAddonXR18Listener : IXR18Listener {
	protected var nextEvent: Event<IXR18Events>? = null

	fun processEvent(event: Event<IXR18Events>): Event<IXR18Events>? {
		nextEvent = event
		event(this)
		return nextEvent
	}
}