package be.t_ars.xtouch

import be.t_ars.xtouch.xairedit.IXAirEditInteractor

class XAirEditInteractorMock : IXAirEditInteractor {
	var currenTab = IXAirEditInteractor.ETab.MIXER
	var currentChannel: Int = 1
	var currentOutput = OUTPUT_MAINLR
	var currentEffectsSettingsDialog: Int? = null

	override suspend fun clickChannel(channel: Int) {
		currentChannel = channel
	}

	override suspend fun clickAux() {
		currentChannel = CHANNEL_AUX
	}

	override suspend fun clickRtn(rtn: Int) {
		currentChannel = CHANNEL_RTN1 + rtn - 1
	}

	override suspend fun clickMainFader() {
		currentChannel = CHANNEL_MAIN
	}

	override suspend fun clickMainLR() {
		currentOutput = OUTPUT_MAINLR
	}

	override suspend fun clickBus(bus: Int) {
		currentOutput = OUTPUT_BUS1 + bus - 1
	}

	override suspend fun clickFx(fx: Int) {
		currentOutput = OUTPUT_FX1 + fx - 1
	}

	override suspend fun clickTab(tab: IXAirEditInteractor.ETab) {
		currenTab = tab
	}

	override suspend fun openEffectSettings(effect: Int) {
		currentEffectsSettingsDialog = effect
	}

	override suspend fun closeDialog() {
		currentEffectsSettingsDialog = null
	}

	companion object {
		const val CHANNEL_COUNT = 16
		const val CHANNEL_AUX = CHANNEL_COUNT + 1
		const val CHANNEL_RTN1 = CHANNEL_AUX + 1
		const val CHANNEL_RTN2 = CHANNEL_RTN1 + 1
		const val CHANNEL_RTN3 = CHANNEL_RTN2 + 1
		const val CHANNEL_RTN4 = CHANNEL_RTN3 + 1
		const val CHANNEL_MAIN = CHANNEL_RTN3 + 1

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
	}
}