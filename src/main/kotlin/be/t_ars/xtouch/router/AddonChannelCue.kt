package be.t_ars.xtouch.router

import be.t_ars.xtouch.osc.IOSCListener
import be.t_ars.xtouch.osc.XR18OSCAPI
import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IXTouchEvents
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddonChannelCue(
	private val xr18OSCAPI: XR18OSCAPI
) : AbstractAddon(), IOSCListener {
	private val connectionListener = ConnectionListener()
	private val xTouchListener = XTouchListener()

	private var cueChannel: Int = 0

	override fun processConnectionEvent(event: Event<IXctlConnectionListener>) {
		event(connectionListener)
	}

	override fun getNextXTouchEvent(event: Event<IXTouchEvents>) =
		xTouchListener.processEvent(event)

	inner class ConnectionListener : IXctlConnectionListener {
		override fun connected() {
			GlobalScope.launch {
				delay(1000)
				xr18OSCAPI.requestLRMixOn()
				for (i in 1..6) {
					xr18OSCAPI.requestBusMixOn(i)
				}
			}
		}
	}

	inner class XTouchListener : AbstractAddonXTouchListener() {
		override fun midiTracksPressedDown() =
			setBusMixOn(1)

		override fun inputsPressedDown() =
			setBusMixOn(2)

		override fun audioTracksPressedDown() =
			setBusMixOn(3)

		override fun audioInstPressedDown() =
			setBusMixOn(4)

		override fun auxPressedDown() =
			setBusMixOn(5)

		override fun busesPressedDown() =
			setBusMixOn(6)

		private fun setBusMixOn(bus: Int) {
			GlobalScope.launch {
				xr18OSCAPI.setBusMixOn(bus, true)
			}
			nextEvent = null
		}

		override fun userPressedDown() {
			GlobalScope.launch {
				xr18OSCAPI.setLRMixOn(true)
			}
			nextEvent = null
		}
	}
}