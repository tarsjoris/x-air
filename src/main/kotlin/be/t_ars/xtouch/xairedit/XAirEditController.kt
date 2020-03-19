package be.t_ars.xtouch.xairedit

import be.t_ars.xtouch.IXTouchSessionListener
import be.t_ars.xtouch.XTouchSession

class XAirEditController(private val interactor: IXAirEditInteractor) :
	IXTouchSessionListener {
	override fun selectionChanged(
		output: Int,
		channel: Int,
		encoder: XTouchSession.EEncoder?,
		dynamicEncoder: XTouchSession.EDynamicEncoder
	) =
		selectChannel(output, channel, encoder, dynamicEncoder)

	override fun effectsSettingsChanged(effectsSettings: Int?) =
		selectEffectSettings(effectsSettings)

	private fun selectChannel(
		output: Int,
		channel: Int,
		encoder: XTouchSession.EEncoder?,
		dynamicEncoder: XTouchSession.EDynamicEncoder
	) {
		when (output) {
			XTouchSession.OUTPUT_MAINLR -> {
				when (channel) {
					in XTouchSession.CHANNEL_BUS1..XTouchSession.CHANNEL_BUS6 ->
						interactor.clickBus(channel - XTouchSession.CHANNEL_BUS1 + 1)
					in XTouchSession.CHANNEL_FX1..XTouchSession.CHANNEL_FX4 ->
						interactor.clickFx(channel - XTouchSession.CHANNEL_FX1 + 1)
					else ->
						interactor.clickMainLR()
				}
				selectEncoder(encoder, dynamicEncoder)
			}
			in XTouchSession.OUTPUT_FX1..XTouchSession.OUTPUT_FX4 -> {
				interactor.clickFx(output - XTouchSession.OUTPUT_FX1 + 1)
				interactor.clickTab(IXAirEditInteractor.ETab.MIXER)
			}
			in XTouchSession.OUTPUT_BUS1..XTouchSession.OUTPUT_BUS6 -> {
				interactor.clickBus(output - XTouchSession.OUTPUT_BUS1 + 1)
				interactor.clickTab(IXAirEditInteractor.ETab.MIXER)
			}
		}
		when (channel) {
			in 1..CHANNEL_COUNT ->
				interactor.clickChannel(channel)
			XTouchSession.CHANNEL_AUX ->
				interactor.clickAux()
			in XTouchSession.CHANNEL_RTN1..XTouchSession.CHANNEL_RTN4 ->
				interactor.clickRtn(channel - XTouchSession.CHANNEL_RTN1 + 1)
			else ->
				interactor.clickMainFader()
		}
	}

	private fun selectEncoder(encoder: XTouchSession.EEncoder?, dynamicEncoder: XTouchSession.EDynamicEncoder) =
		interactor.clickTab(
			when (encoder) {
				XTouchSession.EEncoder.BUS -> IXAirEditInteractor.ETab.SENDS
				XTouchSession.EEncoder.FX -> IXAirEditInteractor.ETab.SENDS
				XTouchSession.EEncoder.EQ -> IXAirEditInteractor.ETab.EQ
				XTouchSession.EEncoder.DYNAMIC ->
					when (dynamicEncoder) {
						XTouchSession.EDynamicEncoder.GATE -> IXAirEditInteractor.ETab.GATE
						XTouchSession.EDynamicEncoder.COMPRESSOR -> IXAirEditInteractor.ETab.COMP
						XTouchSession.EDynamicEncoder.AUTOMIX -> IXAirEditInteractor.ETab.CHANNEL
					}
				else -> IXAirEditInteractor.ETab.MIXER
			}
		)

	private fun selectEffectSettings(effectsSettings: Int?) {
		if (effectsSettings == null) {
			interactor.closeDialog()
		} else {
			interactor.openEffectSettings(effectsSettings)
		}
	}

	companion object {
		private const val CHANNEL_COUNT = 16
	}
}