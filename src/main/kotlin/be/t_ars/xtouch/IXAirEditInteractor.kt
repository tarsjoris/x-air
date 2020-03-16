package be.t_ars.xtouch

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

	fun clickChannel(channel: Int)
	fun clickAux()
	fun clickRtn(rtn: Int)
	fun clickMainFader()
	fun clickMainLR()
	fun clickBus(bus: Int)
	fun clickFx(fx: Int)
	fun clickTab(tab: ETab)
	fun openEffectSettings(effect: Int)
	fun closeDialog()
}