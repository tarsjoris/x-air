package com.tjors.xtouch

class XAirEditController(private val interactor: XAirEditInteractor) : IXctlListener {
	private enum class EEncoder(val perChannel: Boolean) {
		GAIN(false),
		PAN(false),
		EQ(true),
		BUS(true),
		FX(true),
		DYNAMIC(true)
	}

	private enum class ETab {
		MIXER,
		CHANNEL,
		INPUT,
		GATE,
		EQ,
		COMP,
		SENDS,
		MAIN,
		FX,
		METER
	}

	private var currentOutput = OUTPUT_MAINLR
	private var currentEncoder: EEncoder? = null
	private var currentTab = ETab.MIXER
	private var currentDynamicTab = 0
	private var currentChannel = 1
	private var currentBank = 1
	private var currentEffectSettings: Int? = null

	override fun channelSelectPressed(channel: Int) =
		selectChannel(channel)

	override fun encoderTrackPressed() =
		selectEncoder(EEncoder.GAIN)

	override fun encoderSendPressed() =
		selectEncoder(EEncoder.BUS)

	override fun encoderPanPressed() =
		selectEncoder(EEncoder.PAN)

	override fun encoderPluginPressed() = // fx
		selectEncoder(EEncoder.FX)

	override fun encoderEqPressed() =
		selectEncoder(EEncoder.EQ)

	override fun encoderInstPressed() =
		selectEncoder(EEncoder.DYNAMIC)

	override fun previousBankPressed() =
		switchBanks(false)

	override fun nextBankPressed() =
		switchBanks(true)

	override fun previousChannelPressed() =
		switchChannel(false)

	override fun nextChannelPressed() =
		switchChannel(true)

	override fun flipPressed() =
		globalViewPressed()

	override fun globalViewPressed() {
		resetEffectSettings()
		resetEncoder()
		resetOutput()
		selectCurrentChannel()
	}

	override fun functionPressed(function: Int) {
		if (function in 1..4) {
			selectEffectSettings(function)
		}
	}

	override fun modifyPressed(modify: Int) =
		selectFx(modify)

	override fun automationPressed(automation: Int) =
		selectBus(automation)

	override fun knobRotated(knob: Int, right: Boolean) {
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

	private fun selectChannel(channel: Int) {
		currentChannel = when (currentBank) {
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
		selectCurrentChannel()
	}

	private fun selectCurrentChannel() {
		selectCurrentOutput()
		when (currentChannel) {
			in 1..CHANNEL_COUNT ->
				interactor.clickChannel(currentChannel)
			CHANNEL_AUX ->
				interactor.clickAux()
			in CHANNEL_RTN1..CHANNEL_RTN4 ->
				interactor.clickRtn(currentChannel - CHANNEL_RTN1 + 1)
			else ->
				interactor.clickMainFader()
		}
	}

	private fun selectEncoder(encoder: EEncoder) {
		// encoder selection is remembered when toggling a bus, so only toggle when main output is selected
		if (currentOutput == OUTPUT_MAINLR && currentEncoder == encoder) {
			// toggle off
			resetEncoder()
		} else {
			resetEffectSettings()
			resetOutput()

			currentEncoder = encoder
			selectCurrentEncoder()
		}
	}

	private fun resetEncoder() {
		currentEncoder = null
		selectCurrentEncoder()
	}

	private fun selectCurrentEncoder() {
		selectTab(
			when (currentEncoder) {
				EEncoder.BUS -> ETab.SENDS
				EEncoder.FX -> ETab.SENDS
				EEncoder.EQ -> ETab.EQ
				EEncoder.DYNAMIC -> DYANMIC_TABS[currentDynamicTab]
				else -> ETab.MIXER
			}
		)
	}

	private fun switchBanks(forward: Boolean) {
		val newBank = currentBank + if (forward) 1 else -1
		if (newBank in 1..BANK_COUNT) {
			switchChannelForBankSwitchInEncoder(newBank)
			currentBank = newBank
		}
	}

	private fun switchChannelForBankSwitchInEncoder(newBank: Int) {
		if (currentEncoder?.perChannel == true) {
			BANK_CHANNELS[currentBank - 1]
				.indexOf(currentChannel)
				.takeIf { it in 0..7 }
				?.let { BANK_CHANNELS[newBank - 1][it] }
				?.also {
					currentChannel = it
					selectCurrentChannel()
				}
		}
	}

	private fun switchChannel(forward: Boolean) {
		val newChannel = currentChannel + if (forward) 1 else -1
		if (newChannel in 1..CHANNEL_MAIN) {
			currentChannel = newChannel
			selectCurrentChannel()
		}
	}

	private fun selectEffectSettings(effect: Int) {
		if (currentEffectSettings == effect) {
			resetEffectSettings()
		} else {
			if (currentEffectSettings == null) {
				resetOutput()
				resetEncoder()
			} else {
				interactor.closeDialog()
			}

			currentEffectSettings = effect
			interactor.openEffectSettings(effect)
		}
	}

	private fun resetEffectSettings() {
		if (currentEffectSettings != null) {
			currentEffectSettings = null
			interactor.closeDialog()
		}
	}

	private fun selectFx(fx: Int) {
		resetEffectSettings()
		if (currentOutput - OUTPUT_FX1 + 1 == fx) {
			resetOutput()
			selectCurrentEncoder()
		} else {
			selectOutput(OUTPUT_FX1 + fx - 1)
			selectTab(ETab.MIXER)
		}
	}

	private fun selectBus(bus: Int) {
		resetEffectSettings()
		if (currentOutput - OUTPUT_BUS1 + 1 == bus) {
			resetOutput()
			selectCurrentEncoder()
		} else {
			selectOutput(OUTPUT_BUS1 + bus - 1)
			selectTab(ETab.MIXER)
		}
	}

	private fun switchDynamicsTab(right: Boolean) {
		val newDynamicTab = currentDynamicTab + if (right) 1 else -1
		if (newDynamicTab in DYANMIC_TABS.indices) {
			currentDynamicTab = newDynamicTab
			selectTab(DYANMIC_TABS[currentDynamicTab])
		}
	}

	private fun selectOutput(output: Int) {
		currentOutput = output
		selectCurrentOutput()
	}

	private fun resetOutput() =
		selectOutput(OUTPUT_MAINLR)

	private fun selectCurrentOutput() {
		when (currentOutput) {
			OUTPUT_MAINLR ->
				when (currentChannel) {
					in CHANNEL_BUS1..CHANNEL_BUS6 ->
						interactor.clickBus(currentChannel - CHANNEL_BUS1 + 1)
					in CHANNEL_FX1..CHANNEL_FX4 ->
						interactor.clickFx(currentChannel - CHANNEL_FX1 + 1)
					else ->
						interactor.clickMainLR()
				}
			in OUTPUT_FX1..OUTPUT_FX4 ->
				interactor.clickFx(currentOutput - OUTPUT_FX1 + 1)
			in OUTPUT_BUS1..OUTPUT_BUS6 ->
				interactor.clickBus(currentOutput - OUTPUT_BUS1 + 1)
		}
	}

	private fun selectTab(tab: ETab) {
		currentTab = tab
		when (tab) {
			ETab.MIXER -> interactor.clickTabMixer()
			ETab.CHANNEL -> interactor.clickTabChannel()
			ETab.INPUT -> interactor.clickTabInput()
			ETab.GATE -> interactor.clickTabGate()
			ETab.EQ -> interactor.clickTabEq()
			ETab.COMP -> interactor.clickTabComp()
			ETab.SENDS -> interactor.clickTabSends()
			ETab.MAIN -> interactor.clickTabMain()
			ETab.FX -> interactor.clickTabFx()
			ETab.METER -> interactor.clickTabMeter()
		}
	}

	companion object {
		private const val CHANNEL_COUNT = 16
		private const val CHANNEL_AUX = CHANNEL_COUNT + 1
		private const val CHANNEL_RTN1 = CHANNEL_AUX + 1
		private const val CHANNEL_RTN2 = CHANNEL_RTN1 + 1
		private const val CHANNEL_RTN3 = CHANNEL_RTN2 + 1
		private const val CHANNEL_RTN4 = CHANNEL_RTN3 + 1
		private const val CHANNEL_BUS1 = CHANNEL_RTN4 + 1
		private const val CHANNEL_BUS2 = CHANNEL_BUS1 + 1
		private const val CHANNEL_BUS3 = CHANNEL_BUS2 + 1
		private const val CHANNEL_BUS4 = CHANNEL_BUS3 + 1
		private const val CHANNEL_BUS5 = CHANNEL_BUS4 + 1
		private const val CHANNEL_BUS6 = CHANNEL_BUS5 + 1
		private const val CHANNEL_FX1 = CHANNEL_BUS6 + 1
		private const val CHANNEL_FX2 = CHANNEL_FX1 + 1
		private const val CHANNEL_FX3 = CHANNEL_FX2 + 1
		private const val CHANNEL_FX4 = CHANNEL_FX3 + 1
		private const val CHANNEL_MAIN = CHANNEL_FX4 + 1

		private const val BANK_COUNT = 5

		private val BANK_CHANNELS = arrayOf(
			arrayOf(1, 2, 3, 4, 5, 6, 7, 8),
			arrayOf(9, 10, 11, 12, 13, 14, 15, 16),
			arrayOf(CHANNEL_AUX, null, null, null, CHANNEL_RTN1, CHANNEL_RTN2, CHANNEL_RTN3, CHANNEL_RTN4),
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
			arrayOf(CHANNEL_FX1, CHANNEL_FX2, CHANNEL_FX3, CHANNEL_FX4, null, null, null, null)
		)

		private const val OUTPUT_MAINLR = 1
		private const val OUTPUT_FX1 = OUTPUT_MAINLR + 1
		private const val OUTPUT_FX2 = OUTPUT_FX1 + 1
		private const val OUTPUT_FX3 = OUTPUT_FX2 + 1
		private const val OUTPUT_FX4 = OUTPUT_FX3 + 1
		private const val OUTPUT_BUS1 = OUTPUT_FX4 + 1
		private const val OUTPUT_BUS2 = OUTPUT_BUS1 + 1
		private const val OUTPUT_BUS3 = OUTPUT_BUS2 + 1
		private const val OUTPUT_BUS4 = OUTPUT_BUS3 + 1
		private const val OUTPUT_BUS5 = OUTPUT_BUS4 + 1
		private const val OUTPUT_BUS6 = OUTPUT_BUS5 + 1

		private val DYANMIC_TABS = arrayOf(ETab.GATE, ETab.COMP, ETab.CHANNEL)
	}
}