package be.t_ars.xtouch.games

import be.t_ars.xtouch.xctl.EButton
import be.t_ars.xtouch.xctl.EChannelButton
import be.t_ars.xtouch.xctl.ELEDMode
import be.t_ars.xtouch.xctl.EScribbleColor
import be.t_ars.xtouch.xctl.IConnectionToXTouch
import be.t_ars.xtouch.xctl.IXTouchListener
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import be.t_ars.xtouch.xctl.XctlConnectionStub
import be.t_ars.xtouch.xctl.XctlUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val playerChannels = arrayOf(1, 8)
private val channelButtonTypes = arrayOf(
	EChannelButton.SELECT,
	EChannelButton.MUTE,
	EChannelButton.SOLO,
	EChannelButton.REC
)
private const val FIELD_X_OFFSET = 2
private const val FIELD_WIDTH = 6
private const val FIELD_HEIGHT = 4
private const val DELAY = 1000L

private class PongListener(private val output: IConnectionToXTouch) : IXctlConnectionListener, IXTouchListener {
	val playerPositions = arrayOf(0, 0)
	val playerScores = arrayOf(0, 0)
	var playing = false
	var right = true
	var up = true
	var x = 0
	var y = 0

	override fun connected() {
		playerPositions[0] = 0
		playerPositions[1] = 0
		for (player in 0..1) {
			output.setChannelButtonLED(
				playerChannels[player],
				channelButtonTypes[playerPositions[player]],
				ELEDMode.ON
			)
		}
		output.setButtonLED(EButton.FLIP, ELEDMode.FLASH)
	}

	override fun flipPressed(down: Boolean) {
		if (down && !playing) {
			startBall()
		}
	}

	override fun faderMoved(channel: Int, position: Int) {
		if (channel in playerChannels) {
			val player = playerChannels.indexOf(channel)
			val button = (XctlUtil.toFaderPercentage(position) * 3F).roundToInt()
			if (playerPositions[player] != button) {
				output.setChannelButtonLED(
					channel,
					channelButtonTypes[playerPositions[player]],
					ELEDMode.OFF
				)
				playerPositions[player] = button
				output.setChannelButtonLED(
					channel,
					channelButtonTypes[playerPositions[player]],
					ELEDMode.ON
				)
			}
		}
	}

	private fun startBall() {
		GlobalScope.launch {
			output.setButtonLED(EButton.FLIP, ELEDMode.OFF)
			setBall(false)
			right = true
			up = true
			x = 0
			y = 0
			playing = true
			setBall(true)
			for (player in 0..1) {
				output.setScribbleTrip(playerChannels[player], EScribbleColor.WHITE, false, "", "")
				output.setChannelButtonLED(
					playerChannels[player],
					channelButtonTypes[playerPositions[player]],
					ELEDMode.ON
				)
			}
			while (playing) {
				delay(DELAY)
				setBall(false)
				progressBall()
				if (x < 0) {
					if (playerPositions[0] == y) {
						right = !right
						progressBall()
						setBall(true)
					} else {
						endGame(1)
					}
				} else if (x >= FIELD_WIDTH) {
					if (playerPositions[1] == y) {
						right = !right
						progressBall()
						setBall(true)
					} else {
						endGame(0)
					}
				} else {
					setBall(true)
				}
			}
		}
	}

	private fun endGame(winner: Int) {
		playing = false
		blinkBall()
		output.setScribbleTrip(playerChannels[1 - winner], EScribbleColor.RED, true, "", "LOOSER")
		output.setScribbleTrip(playerChannels[winner], EScribbleColor.GREEN, true, "", "WINNER")
		++playerScores[winner]
		for (player in 0..1) {
			output.setLEDRingContinuous(playerChannels[player], playerScores[player] - 1)
		}
		output.setButtonLED(EButton.FLIP, ELEDMode.FLASH)
	}

	private fun progressBall() {
		x += if (right) 1 else -1
		if (up) {
			if (y == FIELD_HEIGHT - 1) {
				up = !up
				y = FIELD_HEIGHT - 2
			} else {
				++y
			}
		} else {
			if (y == 0) {
				up = !up
				y = 1
			} else {
				--y
			}
		}
	}

	private fun setBall(on: Boolean) =
		setBallMode(if (on) ELEDMode.ON else ELEDMode.OFF)

	private fun blinkBall() =
		setBallMode(ELEDMode.FLASH)

	private fun setBallMode(mode: ELEDMode) =
		output.setChannelButtonLED(FIELD_X_OFFSET + x, channelButtonTypes[y], mode)
}

fun main() {
	val connection = XctlConnectionStub()
	val listener = PongListener(connection.getConnectionToXTouch())
	connection.addConnectionListener(listener)
	connection.addXTouchListener(listener)
	connection.run()
}