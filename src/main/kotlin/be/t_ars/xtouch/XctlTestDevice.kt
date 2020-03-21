package be.t_ars.xtouch

import be.t_ars.xtouch.xctl.IXTouchListener
import be.t_ars.xtouch.xctl.IXctlConnection
import be.t_ars.xtouch.xctl.IXctlOutput
import be.t_ars.xtouch.xctl.XctlConnectionImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos

private class Listener(val output: IXctlOutput) : IXTouchListener {
	override fun channelSelectPressed(channel: Int) {
		output.setDigits(channel)
		output.setMeter(channel, 8)
	}

	override fun faderMoved(channel: Int, position: Float) {
		when (channel) {
			in 1..2 -> output.setLEDRing(channel, (position * 12).toInt())
			in 3..4 -> output.setLEDRingWithHalves(channel, (position * 24).toInt())
			in 5..6 -> output.setLEDRingContinuous(channel, (position * 12).toInt())
			in 7..8 -> output.setLEDRingLeftRight(channel, (position * 12).toInt() - 6)
		}
	}

	override fun mainFaderMoved(position: Float) {
		output.setFaderPosition(1, position)
	}

	override fun flipPressed() {
		GlobalScope.launch {
			animateFadersSine { it > 0 }
			for (j in 1..2) {
				animateFadersSine()
			}
			animateFadersSine { it <= 0 }
		}
	}

	private suspend fun animateFadersSine(predicate: (Float) -> Boolean = { true }) {
		for (i in 1..360) {
			for (channel in 0..8) {
				val angleDegrees = i.toFloat() - (channel.toFloat() / 9F) * 360F
				if (predicate.invoke(angleDegrees)) {
					val angle = angleDegrees / 360F * 2 * PI
					val position = (1F - cos(angle).toFloat()) / 2F
					if (channel < 8) {
						output.setFaderPosition(channel + 1, position)
					} else {
						output.setMainFaderPosition(position)
					}
				}
			}
			delay(10)
		}
	}

	override fun globalViewPressed() {
		GlobalScope.launch {
			setChannelButtonsLEDS(IXctlOutput.ELEDMode.ON)
			delay(1000)
			setChannelButtonsLEDS(IXctlOutput.ELEDMode.FLASH)
			delay(2000)
			setChannelButtonsLEDS(IXctlOutput.ELEDMode.OFF)
			setButtonLEDS(IXctlOutput.ELEDMode.ON)
			delay(1000)
			setButtonLEDS(IXctlOutput.ELEDMode.FLASH)
			delay(2000)
			setButtonLEDS(IXctlOutput.ELEDMode.OFF)
		}
	}

	private suspend fun setChannelButtonsLEDS(mode: IXctlOutput.ELEDMode) {
		for (channel in 1..8) {
			for (button in IXctlOutput.EChannelButton.values()) {
				output.setChannelButtonLED(channel, button, mode)
				delay(20)
			}
		}
	}

	private suspend fun setButtonLEDS(mode: IXctlOutput.ELEDMode) {
		for (button in IXctlOutput.EButton.values()) {
			output.setButtonLED(button, mode)
			delay(20)
		}
	}

	override fun knobPressed(knob: Int) {
		when (knob) {
			1 -> output.setScribbleTrip(1, IXctlOutput.EScribbleColor.BLUE, false, "Kotlin", " Rules")
			2 -> output.setScribbleTrip(2, IXctlOutput.EScribbleColor.RED, true, "Button", "   2")
		}
	}
}

fun main() {
	val connection: IXctlConnection = XctlConnectionImpl()
	connection.addXTouchListener(Listener(connection.getOutput()))
	connection.run()
}