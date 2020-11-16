package be.t_ars.xtouch.games

import be.t_ars.xtouch.xctl.EButton
import be.t_ars.xtouch.xctl.EChannelButton
import be.t_ars.xtouch.xctl.ELEDMode
import be.t_ars.xtouch.xctl.EScribbleColor
import be.t_ars.xtouch.xctl.IConnectionToXTouch
import be.t_ars.xtouch.xctl.IXTouchListener
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import be.t_ars.xtouch.xctl.XctlConnectionStub
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private class WhackAMoleListener(private val output: IConnectionToXTouch) : IXctlConnectionListener, IXTouchListener {
	private val random = Random(System.currentTimeMillis())
	private val channelButtons = EChannelButton.values()
	private val buttons = EButton.values()

	private var score = 0
	private var currentChannels = false
	private var currentChannel = 1
	private var currentChannelButton = EChannelButton.SELECT
	private var currentButton = EButton.ENCODER_TRACK

	override fun connected() {
		score = 0
		clearCurrent()
		placeMole()
	}

	override fun channelRecPressedDown(channel: Int) {
		if (currentChannels && currentChannel == channel && currentChannelButton == EChannelButton.REC) {
			next()
		} else {
			loose()
		}
	}

	override fun channelSoloPressedDown(channel: Int) {
		if (currentChannels && currentChannel == channel && currentChannelButton == EChannelButton.SOLO) {
			next()
		} else {
			loose()
		}
	}

	override fun channelMutePressedDown(channel: Int) {
		if (currentChannels && currentChannel == channel && currentChannelButton == EChannelButton.MUTE) {
			next()
		} else {
			loose()
		}
	}

	override fun channelSelectPressedDown(channel: Int) {
		if (currentChannels && currentChannel == channel && currentChannelButton == EChannelButton.SELECT) {
			next()
		} else {
			loose()
		}
	}

	override fun encoderTrackPressedDown() {
		if (!currentChannels && currentButton == EButton.ENCODER_TRACK) {
			next()
		} else {
			loose()
		}
	}

	override fun encoderSendPressedDown() {
		if (!currentChannels && currentButton == EButton.ENCODER_SEND) {
			next()
		} else {
			loose()
		}
	}

	override fun encoderPanPressedDown() {
		if (!currentChannels && currentButton == EButton.ENCODER_PAN) {
			next()
		} else {
			loose()
		}
	}

	override fun encoderPluginPressedDown() {
		if (!currentChannels && currentButton == EButton.ENCODER_PLUGIN) {
			next()
		} else {
			loose()
		}
	}

	override fun encoderEqPressedDown() {
		if (!currentChannels && currentButton == EButton.ENCODER_EQ) {
			next()
		} else {
			loose()
		}
	}

	override fun encoderInstPressedDown() {
		if (!currentChannels && currentButton == EButton.ENCODER_INST) {
			next()
		} else {
			loose()
		}
	}

	override fun previousBankPressedDown() {
		if (!currentChannels && currentButton == EButton.PREV_BANK) {
			next()
		} else {
			loose()
		}
	}

	override fun nextBankPressedDown() {
		if (!currentChannels && currentButton == EButton.NEXT_BANK) {
			next()
		} else {
			loose()
		}
	}

	override fun previousChannelPressedDown() {
		if (!currentChannels && currentButton == EButton.PREV_CHANNEL) {
			next()
		} else {
			loose()
		}
	}

	override fun nextChannelPressedDown() {
		if (!currentChannels && currentButton == EButton.NEXT_CHANNEL) {
			next()
		} else {
			loose()
		}
	}

	override fun flipPressedDown() {
		if (!currentChannels && currentButton == EButton.FLIP) {
			next()
		} else {
			loose()
		}
	}

	override fun globalViewPressedDown() {
		if (!currentChannels && currentButton == EButton.GLOBAL_VIEW) {
			next()
		} else {
			loose()
		}
	}

	override fun functionPressedDown(function: Int) {
		if (!currentChannels && currentButton == when (function) {
				1 -> EButton.F1
				2 -> EButton.F2
				3 -> EButton.F3
				4 -> EButton.F4
				5 -> EButton.F5
				6 -> EButton.F6
				7 -> EButton.F7
				else -> EButton.F8
			}
		) {
			next()
		} else {
			loose()
		}
	}

	override fun midiTracksPressedDown() {
		if (!currentChannels && currentButton == EButton.MIDI_TRACKS) {
			next()
		} else {
			loose()
		}
	}

	override fun inputsPressedDown() {
		if (!currentChannels && currentButton == EButton.INPUTS) {
			next()
		} else {
			loose()
		}
	}

	override fun audioTracksPressedDown() {
		if (!currentChannels && currentButton == EButton.AUDIO_TRACKS) {
			next()
		} else {
			loose()
		}
	}

	override fun audioInstPressedDown() {
		if (!currentChannels && currentButton == EButton.AUDIO_INST) {
			next()
		} else {
			loose()
		}
	}

	override fun auxPressedDown() {
		if (!currentChannels && currentButton == EButton.AUX) {
			next()
		} else {
			loose()
		}
	}

	override fun busesPressedDown() {
		if (!currentChannels && currentButton == EButton.BUSES) {
			next()
		} else {
			loose()
		}
	}

	override fun outputsPressedDown() {
		if (!currentChannels && currentButton == EButton.OUTPUTS) {
			next()
		} else {
			loose()
		}
	}

	override fun userPressedDown() {
		if (!currentChannels && currentButton == EButton.USER) {
			next()
		} else {
			loose()
		}
	}

	override fun modifyPressedDown(modify: Int) {
		if (!currentChannels && currentButton == when (modify) {
				1 -> EButton.MODIFY_SHIFT
				2 -> EButton.MODIFY_OPTION
				3 -> EButton.MODIFY_CONTROL
				else -> EButton.MODIFY_ALT
			}
		) {
			next()
		} else {
			loose()
		}
	}

	override fun automationPressedDown(automation: Int) {
		if (!currentChannels && currentButton == when (automation) {
				1 -> EButton.AUTOMATION_READ
				2 -> EButton.AUTOMATION_WRITE
				3 -> EButton.AUTOMATION_TRIM
				4 -> EButton.AUTOMATION_TOUCH
				5 -> EButton.AUTOMATION_LATCH
				else -> EButton.AUTOMATION_GROUP
			}
		) {
			next()
		} else {
			loose()
		}
	}

	override fun utiliyPressedDown(utility: Int) {
		if (!currentChannels && currentButton == when (utility) {
				1 -> EButton.UTILITY_SAVE
				2 -> EButton.UTILITY_UNDO
				3 -> EButton.UTILITY_CANCEL
				else -> EButton.UTILITY_ENTER
			}
		) {
			next()
		} else {
			loose()
		}
	}

	private fun loose() {
		GlobalScope.launch {
			output.setScribbleTrip(1, EScribbleColor.RED, true, " GAME", " OVER")
			for (channel in 2..8) {
				output.setScribbleTrip(channel, EScribbleColor.RED, false, "", "")
			}
			delay(1000)
			for (channel in 1..8) {
				output.setScribbleTrip(channel, EScribbleColor.WHITE, false, "", "")
			}
			score = 0
			clearCurrent()
			placeMole()
		}
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
				ELEDMode.OFF
			)
		} else {
			output.setButtonLED(
				currentButton,
				ELEDMode.OFF
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
				ELEDMode.ON
			)
		} else {
			currentButton = buttons[random.nextInt(buttons.size - 1)]
			output.setButtonLED(
				currentButton,
				ELEDMode.ON
			)
		}
	}
}

fun main() {
	val connection = XctlConnectionStub()
	val listener = WhackAMoleListener(connection.getConnectionToXTouch())
	connection.addConnectionListener(listener)
	connection.addXTouchListener(listener)
	connection.run()
}