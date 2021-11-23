package be.t_ars.xtouch.xairedit

interface IXAirEditInteractor {
	enum class ETab {
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

	suspend fun clickChannel(channel: Int)
	suspend fun clickAux()
	suspend fun clickRtn(rtn: Int)
	suspend fun clickMainFader()
	suspend fun clickMainLR()
	suspend fun clickBus(bus: Int)
	suspend fun clickFx(fx: Int)
	suspend fun clickTab(tab: ETab)
	fun openEffectSettings(effect: Int)
	fun closeDialog()
}