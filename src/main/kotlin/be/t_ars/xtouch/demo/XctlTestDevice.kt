package be.t_ars.xtouch.demo

import be.t_ars.xtouch.xctl.EButton
import be.t_ars.xtouch.xctl.EChannelButton
import be.t_ars.xtouch.xctl.ELEDMode
import be.t_ars.xtouch.xctl.EScribbleColor
import be.t_ars.xtouch.xctl.IConnectionToXTouch
import be.t_ars.xtouch.xctl.IXTouchListener
import be.t_ars.xtouch.xctl.XctlConnectionStub
import be.t_ars.xtouch.xctl.XctlUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt

private class Listener(val output: IConnectionToXTouch) : IXTouchListener {
	override fun channelSelectPressedDown(channel: Int) {
		output.setDigits(channel)
		output.setMeter(channel, 8)
	}

	override fun faderMoved(channel: Int, position: Int) {
		when (channel) {
			in 1..2 -> output.setLEDRingSingle(channel, (XctlUtil.toFaderPercentage(position) * 12).toInt())
			in 3..4 -> output.setLEDRingWithHalves(channel, (XctlUtil.toFaderPercentage(position) * 24).toInt())
			in 5..6 -> output.setLEDRingContinuous(channel, (XctlUtil.toFaderPercentage(position) * 12).toInt())
			in 7..8 -> output.setLEDRingLeftRight(
				channel,
				(XctlUtil.toFaderPercentage(position) * 12).toInt() - 6
			)
		}
	}

	override fun mainFaderMoved(position: Int) {
		output.setChannelFaderPosition(1, position)
	}

	override fun flipPressedDown() {
		animateFadersSine { it > 0 }
		for (j in 1..2) {
			animateFadersSine()
		}
		animateFadersSine { it <= 0 }
	}

	private fun animateFadersSine(predicate: (Float) -> Boolean = { true }) {
		GlobalScope.launch {
			for (i in 1..360) {
				for (channel in 0..8) {
					val angleDegrees = i.toFloat() - (channel.toFloat() / 9F) * 360F
					if (predicate.invoke(angleDegrees)) {
						val angle = angleDegrees / 360F * 2 * PI
						val fraction = (1F - cos(angle).toFloat()) / 2F
						val position = fraction.times(XctlUtil.FADER_POSIION_RANGE.last).roundToInt()
						if (channel < 8) {
							output.setChannelFaderPosition(channel + 1, position)
						} else {
							output.setMainFaderPosition(position)
						}
					}
				}
				delay(10)
			}
		}
	}

	override fun globalViewPressedDown() {
		GlobalScope.launch {
			setChannelButtonsLEDS(ELEDMode.ON)
			delay(1000)
			setChannelButtonsLEDS(ELEDMode.FLASH)
			delay(2000)
			setChannelButtonsLEDS(ELEDMode.OFF)
			setButtonLEDS(ELEDMode.ON)
			delay(1000)
			setButtonLEDS(ELEDMode.FLASH)
			delay(2000)
			setButtonLEDS(ELEDMode.OFF)
		}
	}

	private suspend fun setChannelButtonsLEDS(mode: ELEDMode) {
		for (channel in 1..8) {
			for (button in EChannelButton.values()) {
				output.setChannelButtonLED(channel, button, mode)
				delay(20)
			}
		}
	}

	private suspend fun setButtonLEDS(mode: ELEDMode) {
		for (button in EButton.values()) {
			output.setButtonLED(button, mode)
			delay(20)
		}
	}

	override fun knobPressedDown(knob: Int) {
		when (knob) {
			1 -> output.setScribbleTrip(1, EScribbleColor.BLUE, false, "Kotlin", " Rules")
			2 -> output.setScribbleTrip(2, EScribbleColor.RED, true, "Button", "   2")
		}
	}
}

fun main() {
	val connection = XctlConnectionStub()
	connection.addXTouchListener(Listener(connection.getConnectionToXTouch()))
	connection.run()
}