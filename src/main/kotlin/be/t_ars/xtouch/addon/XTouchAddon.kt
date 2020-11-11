package be.t_ars.xtouch.addon

import be.t_ars.xtouch.osc.IXR18Listener
import be.t_ars.xtouch.osc.XR18API
import be.t_ars.xtouch.xctl.IXTouchListener
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import be.t_ars.xtouch.xctl.IXctlOutput
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class XTouchAddon(private val xr18API: XR18API, private val toXTouch: IXctlOutput) : IXR18Listener, IXTouchListener,
	IXctlConnectionListener {
	private var lrMixOn = true
	private val busMixOn = Array(6) { true }

	override fun connected() {
		GlobalScope.launch {
			delay(1000)
			xr18API.requestLRMixOn()
			for (i in 1..6) {
				xr18API.requestBusMixOn(i)
			}
		}
	}

	// XR18
	override fun lrMixOn(on: Boolean) {
		lrMixOn = on
		toXTouch.setButtonLED(
			IXctlOutput.EButton.USER,
			if (on) IXctlOutput.ELEDMode.OFF else IXctlOutput.ELEDMode.FLASH
		)
	}

	override fun busMixOn(bus: Int, on: Boolean) {
		busMixOn[bus - 1] = on
		toXTouch.setButtonLED(
			when (bus) {
				1 -> IXctlOutput.EButton.MIDI_TRACKS
				2 -> IXctlOutput.EButton.INPUTS
				3 -> IXctlOutput.EButton.AUDIO_TRACKS
				4 -> IXctlOutput.EButton.AUDIO_INST
				5 -> IXctlOutput.EButton.AUX
				else -> IXctlOutput.EButton.BUSES
			},
			if (on) IXctlOutput.ELEDMode.OFF else IXctlOutput.ELEDMode.FLASH
		)
	}

	// XTouch
	override suspend fun midiTracksPressed() =
		setBusMixOn(1)

	override suspend fun inputsPressed() =
		setBusMixOn(2)

	override suspend fun audioTracksPressed() =
		setBusMixOn(3)

	override suspend fun audioInstPressed() =
		setBusMixOn(4)

	override suspend fun auxPressed() =
		setBusMixOn(5)

	override suspend fun busesPressed() =
		setBusMixOn(6)

	private fun setBusMixOn(bus: Int) =
		xr18API.setBusMixOn(bus, !busMixOn[bus - 1])

	override suspend fun userPressed() {
		xr18API.setLRMixOn(!lrMixOn)
	}
}