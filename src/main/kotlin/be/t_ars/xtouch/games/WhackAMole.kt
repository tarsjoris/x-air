package be.t_ars.xtouch.games

import be.t_ars.xtouch.xctl.IXTouchListener
import be.t_ars.xtouch.xctl.IXctlConnection
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import be.t_ars.xtouch.xctl.IXctlOutput
import be.t_ars.xtouch.xctl.XctlConnectionImpl
import kotlinx.coroutines.delay
import kotlin.random.Random

private class WhackAMoleListener(private val output: IXctlOutput) : IXctlConnectionListener, IXTouchListener {
	private val random = Random(System.currentTimeMillis())
	private val channelButtons = IXctlOutput.EChannelButton.values()
	private val buttons = IXctlOutput.EButton.values()

	private var score = 0
	private var currentChannels = false
	private var currentChannel = 1
	private var currentChannelButton = IXctlOutput.EChannelButton.SELECT
	private var currentButton = IXctlOutput.EButton.ENCODER_TRACK

	override fun connected() {
		score = 0
		clearCurrent()
		placeMole()
	}

	override suspend fun channelRecPressed(channel: Int) {
		if (currentChannels && currentChannel == channel && currentChannelButton == IXctlOutput.EChannelButton.REC) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun channelSoloPressed(channel: Int) {
		if (currentChannels && currentChannel == channel && currentChannelButton == IXctlOutput.EChannelButton.SOLO) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun channelMutePressed(channel: Int) {
		if (currentChannels && currentChannel == channel && currentChannelButton == IXctlOutput.EChannelButton.MUTE) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun channelSelectPressed(channel: Int) {
		if (currentChannels && currentChannel == channel && currentChannelButton == IXctlOutput.EChannelButton.SELECT) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun encoderTrackPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.ENCODER_TRACK) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun encoderSendPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.ENCODER_SEND) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun encoderPanPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.ENCODER_PAN) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun encoderPluginPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.ENCODER_PLUGIN) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun encoderEqPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.ENCODER_EQ) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun encoderInstPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.ENCODER_INST) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun previousBankPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.PREV_BANK) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun nextBankPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.NEXT_BANK) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun previousChannelPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.PREV_CHANNEL) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun nextChannelPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.NEXT_CHANNEL) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun flipPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.FLIP) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun globalViewPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.GLOBAL_VIEW) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun functionPressed(function: Int) {
		if (!currentChannels && currentButton == when (function) {
				1 -> IXctlOutput.EButton.F1
				2 -> IXctlOutput.EButton.F2
				3 -> IXctlOutput.EButton.F3
				4 -> IXctlOutput.EButton.F4
				5 -> IXctlOutput.EButton.F5
				6 -> IXctlOutput.EButton.F6
				7 -> IXctlOutput.EButton.F7
				else -> IXctlOutput.EButton.F8
			}
		) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun midiTracksPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.MIDI_TRACKS) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun inputsPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.INPUTS) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun audioTracksPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.AUDIO_TRACKS) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun audioInstPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.AUDIO_INST) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun auxPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.AUX) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun busesPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.BUSES) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun outputsPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.OUTPUTS) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun userPressed() {
		if (!currentChannels && currentButton == IXctlOutput.EButton.USER) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun modifyPressed(modify: Int) {
		if (!currentChannels && currentButton == when (modify) {
				1 -> IXctlOutput.EButton.MODIFY_SHIFT
				2 -> IXctlOutput.EButton.MODIFY_OPTION
				3 -> IXctlOutput.EButton.MODIFY_CONTROL
				else -> IXctlOutput.EButton.MODIFY_ALT
			}
		) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun automationPressed(automation: Int) {
		if (!currentChannels && currentButton == when (automation) {
				1 -> IXctlOutput.EButton.AUTOMATION_READ
				2 -> IXctlOutput.EButton.AUTOMATION_WRITE
				3 -> IXctlOutput.EButton.AUTOMATION_TRIM
				4 -> IXctlOutput.EButton.AUTOMATION_TOUCH
				5 -> IXctlOutput.EButton.AUTOMATION_LATCH
				else -> IXctlOutput.EButton.AUTOMATION_GROUP
			}
		) {
			next()
		} else {
			loose()
		}
	}

	override suspend fun utiliyPressed(utility: Int) {
		if (!currentChannels && currentButton == when (utility) {
				1 -> IXctlOutput.EButton.UTILITY_SAVE
				2 -> IXctlOutput.EButton.UTILITY_UNDO
				3 -> IXctlOutput.EButton.UTILITY_CANCEL
				else -> IXctlOutput.EButton.UTILITY_ENTER
			}
		) {
			next()
		} else {
			loose()
		}
	}

	private suspend fun loose() {
		output.setScribbleTrip(1, IXctlOutput.EScribbleColor.RED, true, " GAME", " OVER")
		for (channel in 2..8) {
			output.setScribbleTrip(channel, IXctlOutput.EScribbleColor.RED, false, "", "")
		}
		delay(1000)
		for (channel in 1..8) {
			output.setScribbleTrip(channel, IXctlOutput.EScribbleColor.WHITE, false, "", "")
		}
		score = 0
		clearCurrent()
		placeMole()
	}

	private fun next() {
		clearCurrent()
		++score
		placeMole()
	}

	private fun clearCurrent() {
		if (currentChannels) {
			output.setChannelButtonLED(
				currentChannel,
				currentChannelButton,
				IXctlOutput.ELEDMode.OFF
			)
		} else {
			output.setButtonLED(
				currentButton,
				IXctlOutput.ELEDMode.OFF
			)
		}
	}

	private fun placeMole() {
		output.setDigits(score)
		currentChannels = random.nextBoolean()
		if (currentChannels) {
			currentChannel = random.nextInt(8) + 1
			currentChannelButton = channelButtons[random.nextInt(channelButtons.size)]
			output.setChannelButtonLED(
				currentChannel,
				currentChannelButton,
				IXctlOutput.ELEDMode.ON
			)
		} else {
			currentButton = buttons[random.nextInt(buttons.size - 1)]
			output.setButtonLED(
				currentButton,
				IXctlOutput.ELEDMode.ON
			)
		}
	}
}

fun main() {
	val connection: IXctlConnection = XctlConnectionImpl()
	val listener = WhackAMoleListener(connection.getOutput())
	connection.addConnectionListener(listener)
	connection.addXTouchListener(listener)
	connection.run()
}