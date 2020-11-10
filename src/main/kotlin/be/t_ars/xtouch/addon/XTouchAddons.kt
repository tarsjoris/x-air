package be.t_ars.xtouch.addon

import be.t_ars.xtouch.osc.XR18API
import be.t_ars.xtouch.xctl.IXTouchListener
import be.t_ars.xtouch.xctl.IXctlOutput
import be.t_ars.xtouch.xctl.ToXTouch

class XTouchAddons(private val xr18API: XR18API, private val toXTouch: IXctlOutput) : IXTouchListener {
	private var lrMixOn = true

	override suspend fun userPressed() {
		lrMixOn = !lrMixOn
		xr18API.setLRMixOn(lrMixOn)
		toXTouch.setButtonLED(
			IXctlOutput.EButton.USER,
			if (lrMixOn) IXctlOutput.ELEDMode.OFF else IXctlOutput.ELEDMode.FLASH
		)
	}
}