package be.t_ars.xtouch.session

import be.t_ars.xtouch.util.Listeners
import be.t_ars.xtouch.xctl.IXTouchListener

class XTouchSession : IXTouchListener {
	enum class EEncoder(val perChannel: Boolean) {
		GAIN(false),
		PAN(false),
		EQ(true),
		BUS(true),
		FX(true),
		DYNAMIC(true)
	}

	enum class EDynamicEncoder {
		GATE,
		COMPRESSOR,
		AUTOMIX
	}

	private var currentOutput = OUTPUT_MAINLR
	private var currentEncoder: EEncoder? = null
	private var currentDynamicEncoder = 0
	private var currentChannel = 1
	private var currentBank = 1
	private var currentEffectSettings: Int? = null
	private val listeners = Listeners<IXTouchSessionListener>()

	fun addListener(listener: IXTouchSessionListener) =
		listeners.add(listener)

	override suspend fun channelSelectPressed(channel: Int) {
		if (channel in 1..8) {
			selectChannel(channel)
		}
	}

	override suspend fun encoderTrackPressed() =
		selectEncoder(EEncoder.GAIN)

	override suspend fun encoderSendPressed() =
		selectEncoder(EEncoder.BUS)

	override suspend fun encoderPanPressed() =
		selectEncoder(EEncoder.PAN)

	override suspend fun encoderPluginPressed() = // fx
		selectEncoder(EEncoder.FX)

	override suspend fun encoderEqPressed() =
		selectEncoder(EEncoder.EQ)

	override suspend fun encoderInstPressed() =
		selectEncoder(EEncoder.DYNAMIC)

	override suspend fun previousBankPressed() =
		switchBanks(false)

	override suspend fun nextBankPressed() =
		switchBanks(true)

	override suspend fun previousChannelPressed() =
		switchChannel(false)

	override suspend fun nextChannelPressed() =
		switchChannel(true)

	override suspend fun flipPressed() =
		globalViewPressed()

	override suspend fun globalViewPressed() {
		resetEffectSettings()
		resetEncoder()
		resetOutput()
		broadcastSelectionChanged()
	}

	override suspend fun functionPressed(function: Int) {
		if (function in 1..4) {
			selectEffectSettings(function)
		}
	}

	override suspend fun modifyPressed(modify: Int) {
		if (modify in 1..4) {
			selectFx(modify)
		}
	}

	override suspend fun automationPressed(automation: Int) {
		if (automation in 1..6) {
			selectBus(automation)
		}
	}

	override suspend fun knobRotated(knob: Int, right: Boolean) {
		when (knob) {
			1 ->
				if (currentEncoder?.perChannel == true) {
					switchChannel(right)
				}
			2 ->
				if (currentEncoder == EEncoder.DYNAMIC) {
					switchDynamicsTab(right)
				}
		}
	}

	private suspend fun selectChannel(channel: Int) =
		setChannel(
			when (currentBank) {
				1 -> channel
				2 -> channel + 8
				3 -> when (channel) {
					1 -> CHANNEL_AUX
					5 -> CHANNEL_RTN1
					6 -> CHANNEL_RTN2
					7 -> CHANNEL_RTN3
					8 -> CHANNEL_RTN4
					else -> currentChannel
				}
				4 -> when (channel) {
					1 -> CHANNEL_BUS1
					2 -> CHANNEL_BUS2
					3 -> CHANNEL_BUS3
					4 -> CHANNEL_BUS4
					5 -> CHANNEL_BUS5
					6 -> CHANNEL_BUS6
					8 -> CHANNEL_MAIN
					else -> currentChannel
				}
				5 -> when (channel) {
					1 -> CHANNEL_FX1
					2 -> CHANNEL_FX2
					3 -> CHANNEL_FX3
					4 -> CHANNEL_FX4
					else -> currentChannel
				}
				else -> currentChannel
			}
		)

	private suspend fun selectEncoder(encoder: EEncoder) {
		// encoder selection is remembered when toggling a bus, so only toggle when main output is selected
		if (currentOutput == OUTPUT_MAINLR && currentEncoder == encoder) {
			// toggle off
			resetEncoder()
		} else {
			resetEffectSettings()
			resetOutput()

			setEncoder(encoder)
		}
	}

	private suspend fun resetEncoder() =
		setEncoder(null)

	private suspend fun setEncoder(encoder: EEncoder?) {
		currentEncoder = encoder
		broadcastSelectionChanged()
	}

	private suspend fun switchBanks(forward: Boolean) {
		val newBank = currentBank + if (forward) 1 else -1
		if (newBank in 1..BANK_COUNT) {
			if (currentOutput == OUTPUT_MAINLR) {
				// only switch banks for encoder when main output is selected
				switchChannelForBankSwitchInEncoder(newBank)
			}
			currentBank = newBank

			if (currentBank > 3 && currentOutput != OUTPUT_MAINLR) {
				// switch back to main outputs when reaching non-support channels
				selectOutput(OUTPUT_MAINLR)
			}
		}
	}

	private suspend fun switchChannelForBankSwitchInEncoder(newBank: Int) {
		if (currentEncoder?.perChannel == true) {
			val newChannel = BANK_CHANNELS[currentBank - 1]
				.indexOf(currentChannel)
				.takeIf { it in 0..7 }
				?.let { BANK_CHANNELS[newBank - 1][it] }
			if (newChannel != null) {
				setChannel(newChannel)
			}
		}
	}

	private suspend fun switchChannel(forward: Boolean) {
		val newChannel = currentChannel + if (forward) 1 else -1
		if (newChannel in 1..CHANNEL_MAIN) {
			setChannel(newChannel)
		}
	}

	private suspend fun setChannel(channel: Int) {
		currentChannel = channel
		broadcastSelectionChanged()
	}

	private suspend fun selectEffectSettings(effectsSettings: Int) {
		if (currentEffectSettings == effectsSettings) {
			resetEffectSettings()
		} else {
			if (currentEffectSettings == null) {
				resetOutput()
				resetEncoder()
			} else {
				resetEffectSettings()
			}

			setEffectsSettings(effectsSettings)
		}
	}

	private suspend fun resetEffectSettings() {
		if (currentEffectSettings != null) {
			setEffectsSettings(null)
		}
	}

	private suspend fun setEffectsSettings(effectsSettings: Int?) {
		currentEffectSettings = effectsSettings
		listeners.broadcastSuspend { it.effectsSettingsChanged(currentEffectSettings) }
	}

	private suspend fun selectFx(fx: Int) {
		if (currentBank > 3) {
			currentBank = 1
		}
		resetEffectSettings()
		if (currentOutput - OUTPUT_FX1 + 1 == fx) {
			resetOutput()
		} else {
			selectOutput(OUTPUT_FX1 + fx - 1)
		}
	}

	private suspend fun selectBus(bus: Int) {
		if (currentBank > 3) {
			currentBank = 1
		}
		resetEffectSettings()
		if (currentOutput - OUTPUT_BUS1 + 1 == bus) {
			resetOutput()
		} else {
			selectOutput(OUTPUT_BUS1 + bus - 1)
		}
	}

	private suspend fun switchDynamicsTab(right: Boolean) {
		val newDynamicEncoder = currentDynamicEncoder + if (right) 1 else -1
		if (newDynamicEncoder in DYNAMIC_ENCODERS.indices) {
			currentDynamicEncoder = newDynamicEncoder
			broadcastSelectionChanged()
		}
	}

	private suspend fun selectOutput(output: Int) {
		currentOutput = output
		broadcastSelectionChanged()
	}

	private suspend fun broadcastSelectionChanged() =
		listeners.broadcastSuspend {
			it.selectionChanged(
				currentOutput,
				currentChannel,
				currentEncoder,
				DYNAMIC_ENCODERS[currentDynamicEncoder]
			)
		}

	private suspend fun resetOutput() =
		selectOutput(OUTPUT_MAINLR)

	companion object {
		private const val CHANNEL_COUNT = 16
		const val CHANNEL_AUX = CHANNEL_COUNT + 1
		const val CHANNEL_RTN1 = CHANNEL_AUX + 1
		const val CHANNEL_RTN2 = CHANNEL_RTN1 + 1
		const val CHANNEL_RTN3 = CHANNEL_RTN2 + 1
		const val CHANNEL_RTN4 = CHANNEL_RTN3 + 1
		const val CHANNEL_BUS1 = CHANNEL_RTN4 + 1
		const val CHANNEL_BUS2 = CHANNEL_BUS1 + 1
		const val CHANNEL_BUS3 = CHANNEL_BUS2 + 1
		const val CHANNEL_BUS4 = CHANNEL_BUS3 + 1
		const val CHANNEL_BUS5 = CHANNEL_BUS4 + 1
		const val CHANNEL_BUS6 = CHANNEL_BUS5 + 1
		const val CHANNEL_FX1 = CHANNEL_BUS6 + 1
		const val CHANNEL_FX2 = CHANNEL_FX1 + 1
		const val CHANNEL_FX3 = CHANNEL_FX2 + 1
		const val CHANNEL_FX4 = CHANNEL_FX3 + 1
		const val CHANNEL_MAIN = CHANNEL_FX4 + 1

		private const val BANK_COUNT = 5

		private val BANK_CHANNELS = arrayOf(
			arrayOf(1, 2, 3, 4, 5, 6, 7, 8),
			arrayOf(9, 10, 11, 12, 13, 14, 15, 16),
			arrayOf(
				CHANNEL_AUX, null, null, null,
				CHANNEL_RTN1,
				CHANNEL_RTN2,
				CHANNEL_RTN3,
				CHANNEL_RTN4
			),
			arrayOf(
				CHANNEL_BUS1,
				CHANNEL_BUS2,
				CHANNEL_BUS3,
				CHANNEL_BUS4,
				CHANNEL_BUS5,
				CHANNEL_BUS6,
				null,
				CHANNEL_MAIN
			),
			arrayOf(
				CHANNEL_FX1,
				CHANNEL_FX2,
				CHANNEL_FX3,
				CHANNEL_FX4, null, null, null, null
			)
		)

		const val OUTPUT_MAINLR = 1
		const val OUTPUT_FX1 = OUTPUT_MAINLR + 1
		const val OUTPUT_FX2 = OUTPUT_FX1 + 1
		const val OUTPUT_FX3 = OUTPUT_FX2 + 1
		const val OUTPUT_FX4 = OUTPUT_FX3 + 1
		const val OUTPUT_BUS1 = OUTPUT_FX4 + 1
		const val OUTPUT_BUS2 = OUTPUT_BUS1 + 1
		const val OUTPUT_BUS3 = OUTPUT_BUS2 + 1
		const val OUTPUT_BUS4 = OUTPUT_BUS3 + 1
		const val OUTPUT_BUS5 = OUTPUT_BUS4 + 1
		const val OUTPUT_BUS6 = OUTPUT_BUS5 + 1

		private val DYNAMIC_ENCODERS =
			arrayOf(
				EDynamicEncoder.GATE,
				EDynamicEncoder.COMPRESSOR,
				EDynamicEncoder.AUTOMIX
			)
	}
}