package be.t_ars.xtouch.router

import be.t_ars.xtouch.osc.IOSCListener
import be.t_ars.xtouch.osc.XR18OSCAPI
import be.t_ars.xtouch.xctl.EButton
import be.t_ars.xtouch.xctl.ELEDMode
import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IXTouchEvents
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import be.t_ars.xtouch.xctl.setButtonLED
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddonMuteButtons(private val xr18OSCAPI: XR18OSCAPI) : AbstractAddon(), IOSCListener {
	private val connectionListener = ConnectionListener()
	private val xTouchListener = XTouchListener()

	// XR18 state
	private var lrMixOn = true
	private val busMixOn = Array(6) { true }

	// Router events
	override fun processConnectionEvent(event: Event<IXctlConnectionListener>) =
		event(connectionListener)

	override fun getNextXTouchEvent(event: Event<IXTouchEvents>) =
		xTouchListener.processEvent(event)

	// XR18 events
	override fun lrMixOn(on: Boolean) {
		lrMixOn = on
		sendToXTouch {
			it.setButtonLED(
				EButton.USER,
				if (on) ELEDMode.OFF else ELEDMode.FLASH
			)
		}
	}

	override fun busMixOn(bus: Int, on: Boolean) {
		busMixOn[bus - 1] = on
		sendToXTouch {
			it.setButtonLED(
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
	}

	inner class ConnectionListener : IXctlConnectionListener {
		override fun connected() {
			GlobalScope.launch {
				delay(1000)
				xr18OSCAPI.requestLRMixOn()
				xr18OSCAPI.requestBusesMixOn()
			}
		}
	}

	inner class XTouchListener : AbstractAddonXTouchListener() {
		override fun midiTracksPressedDown() =
			toggleBusMixOn(1)

		override fun inputsPressedDown() =
			toggleBusMixOn(2)

		override fun audioTracksPressedDown() =
			toggleBusMixOn(3)

		override fun audioInstPressedDown() =
			toggleBusMixOn(4)

		override fun auxPressedDown() =
			toggleBusMixOn(5)

		override fun busesPressedDown() =
			toggleBusMixOn(6)

		private fun toggleBusMixOn(bus: Int) {
			GlobalScope.launch {
				xr18OSCAPI.setBusMixOn(bus, !busMixOn[bus - 1])
			}
			nextEvent = null
		}

		override fun userPressedDown() {
			GlobalScope.launch {
				xr18OSCAPI.setLRMixOn(!lrMixOn)
			}
			nextEvent = null
		}
	}
}