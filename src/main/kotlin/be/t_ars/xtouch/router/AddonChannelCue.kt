package be.t_ars.xtouch.router

import be.t_ars.xtouch.osc.IOSCListener
import be.t_ars.xtouch.osc.XR18OSCAPI
import be.t_ars.xtouch.xctl.EButton
import be.t_ars.xtouch.xctl.ELEDMode
import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IConnectionToXTouch
import be.t_ars.xtouch.xctl.IXTouchEvents
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddonChannelCue(
	private val xr18OSCAPI: XR18OSCAPI,
	private val toXTouch: IConnectionToXTouch
) : IAddon, IOSCListener {
	private val connectionListener = ConnectionListener()
	private val xTouchListener = XTouchListener()

	private var cueChannel: Int = 0

	override fun processConnectionEvent(event: Event<IXctlConnectionListener>) {
		event(connectionListener)
	}

	override fun processEventFromXTouch(event: Event<IXTouchEvents>): Event<IXTouchEvents>? =
		xTouchListener.processEvent(event)

	// XR18
	override fun lrMixOn(on: Boolean) {
		toXTouch.setButtonLED(
			EButton.USER,
			if (on) ELEDMode.OFF else ELEDMode.FLASH
		)
	}

	override fun busMixOn(bus: Int, on: Boolean) {
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