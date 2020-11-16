package be.t_ars.xtouch.router

import be.t_ars.xtouch.osc.IXR18Listener
import be.t_ars.xtouch.osc.XR18API
import be.t_ars.xtouch.xctl.EButton
import be.t_ars.xtouch.xctl.ELEDMode
import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IConnectionToXTouch
import be.t_ars.xtouch.xctl.IXTouchEvents
import be.t_ars.xtouch.xctl.IXTouchListener
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddonMuteButtons(
	private val xr18API: XR18API,
	private val toXTouch: IConnectionToXTouch
) : IAddon, IXR18Listener {
	private val connectionListener = ConnectionListener()
	private val xTouchListener = XTouchListener()

	private var lrMixOn = true
	private val busMixOn = Array(6) { true }

	override fun processConnectionEvent(event: Event<IXctlConnectionListener>) {
		event(connectionListener)
	}

	override fun processEventFromXTouch(event: Event<IXTouchEvents>): Event<IXTouchEvents>? =
		xTouchListener.processEvent(event)

	// XR18
	override fun lrMixOn(on: Boolean) {
		lrMixOn = on
		toXTouch.setButtonLED(
			EButton.USER,
			if (on) ELEDMode.OFF else ELEDMode.FLASH
		)
	}

	override fun busMixOn(bus: Int, on: Boolean) {
		busMixOn[bus - 1] = on
		toXTouch.setButtonLED(
			when (bus) {
				1 -> EButton.MIDI_TRACKS
				2 -> EButton.INPUTS
				3 -> EButton.AUDIO_TRACKS
				4 -> EButton.AUDIO_INST
				5 -> EButton.AUX
				else -> EButton.BUSES
			},
			if (on) ELEDMode.OFF else ELEDMode.FLASH
		)
	}

	inner class ConnectionListener : IXctlConnectionListener {
		override fun connected() {
			GlobalScope.launch {
				delay(1000)
				xr18API.requestLRMixOn()
				for (i in 1..6) {
					xr18API.requestBusMixOn(i)
				}
			}
		}
	}

	inner class XTouchListener : IXTouchListener {
		private var nextEvent: Event<IXTouchEvents>? = null

		fun processEvent(event: Event<IXTouchEvents>): Event<IXTouchEvents>? {
			nextEvent = event
			event(this)
			return nextEvent
		}

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
				xr18API.setBusMixOn(bus, !busMixOn[bus - 1])
			}
			nextEvent = null
		}

		override fun userPressedDown() {
			GlobalScope.launch {
				xr18API.setLRMixOn(!lrMixOn)
			}
			nextEvent = null
		}
	}
}