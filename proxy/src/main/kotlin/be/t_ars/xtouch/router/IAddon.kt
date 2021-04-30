package be.t_ars.xtouch.router

import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IXR18Events
import be.t_ars.xtouch.xctl.IXTouchEvents
import be.t_ars.xtouch.xctl.IXctlConnectionListener

interface IAddon {
	fun processConnectionEvent(event: Event<IXctlConnectionListener>) {}

	fun processEventFromXTouch(event: Event<IXTouchEvents>)

	fun processEventFromXR18(event: Event<IXR18Events>)
}