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
			for (i in 0..359) {
				for (channel in 0..8) {
					val angleDegrees = i.toFloat() - (channel.toFloat() / 9F) * 360F
					if (angleDegrees > 0) {
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
			for (j in 1..1) {
				for (i in 0..359) {
					for (channel in 0..8) {
						val angleDegrees = i.toFloat() - (channel.toFloat() / 9F) * 360F
						val angle = angleDegrees / 360F * 2 * PI
						val position = (1F - cos(angle).toFloat()) / 2F
						if (channel < 8) {
							output.setFaderPosition(channel + 1, position)
						} else {
							output.setMainFaderPosition(position)
						}
					}
					delay(10)
				}
			}
			for (i in 0..360) {
				for (channel in 0..8) {
					val angleDegrees = i.toFloat() - (channel.toFloat() / 9F) * 360F
					if (angleDegrees <=  0) {
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
	}
}

fun main() {
	val connection: IXctlConnection = XctlConnectionImpl()
	connection.addXTouchListener(Listener(connection.getOutput()))
	connection.run()
}