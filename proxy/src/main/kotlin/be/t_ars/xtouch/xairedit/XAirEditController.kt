package be.t_ars.xtouch.xairedit

import be.t_ars.xtouch.session.IXTouchSessionListener
import be.t_ars.xtouch.session.XTouchSessionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class XAirEditController(private val scope: CoroutineScope, private val interactor: IXAirEditInteractor) :
	IXTouchSessionListener {
	override fun selectionChanged(
		output: Int,
		channel: Int,
		encoder: XTouchSessionState.EEncoder?,
		dynamicEncoder: XTouchSessionState.EDynamicEncoder
	) {
		scope.launch {
			selectChannel(output, channel, encoder, dynamicEncoder)
		}
	}

	override fun effectsSettingsChanged(effectsSettings: Int?) =
		selectEffectSettings(effectsSettings)

	private suspend fun selectChannel(
		output: Int,
		channel: Int,
		encoder: XTouchSessionState.EEncoder?,
		dynamicEncoder: XTouchSessionState.EDynamicEncoder
	) {
		when (output) {
			XTouchSessionState.OUTPUT_MAINLR -> {
				when (channel) {
					in XTouchSessionState.CHANNEL_BUS1..XTouchSessionState.CHANNEL_BUS6 ->
						interactor.clickBus(channel - XTouchSessionState.CHANNEL_BUS1 + 1)
					in XTouchSessionState.CHANNEL_FX1..XTouchSessionState.CHANNEL_FX4 ->
						interactor.clickFx(channel - XTouchSessionState.CHANNEL_FX1 + 1)
					else ->
						interactor.clickMainLR()
				}
				selectEncoder(encoder, dynamicEncoder)
			}
			in XTouchSessionState.OUTPUT_FX1..XTouchSessionState.OUTPUT_FX4 -> {
				interactor.clickFx(output - XTouchSessionState.OUTPUT_FX1 + 1)
				interactor.clickTab(IXAirEditInteractor.ETab.MIXER)
			}
			in XTouchSessionState.OUTPUT_BUS1..XTouchSessionState.OUTPUT_BUS6 -> {
				interactor.clickBus(output - XTouchSessionState.OUTPUT_BUS1 + 1)
				interactor.clickTab(IXAirEditInteractor.ETab.MIXER)
			}
		}
		when (channel) {
			in 1..CHANNEL_COUNT ->
				interactor.clickChannel(channel)
			XTouchSessionState.CHANNEL_AUX ->
				interactor.clickAux()
			in XTouchSessionState.CHANNEL_RTN1..XTouchSessionState.CHANNEL_RTN4 ->
				interactor.clickRtn(channel - XTouchSessionState.CHANNEL_RTN1 + 1)
			else ->
				interactor.clickMainFader()
		}
	}

	private suspend fun selectEncoder(
		encoder: XTouchSessionState.EEncoder?,
		dynamicEncoder: XTouchSessionState.EDynamicEncoder
	) =
		interactor.clickTab(
			when (encoder) {
				XTouchSessionState.EEncoder.BUS -> IXAirEditInteractor.ETab.SENDS
				XTouchSessionState.EEncoder.FX -> IXAirEditInteractor.ETab.SENDS
				XTouchSessionState.EEncoder.EQ -> IXAirEditInteractor.ETab.EQ
				XTouchSessionState.EEncoder.DYNAMIC ->
					when (dynamicEncoder) {
						XTouchSessionState.EDynamicEncoder.GATE -> IXAirEditInteractor.ETab.GATE
						XTouchSessionState.EDynamicEncoder.COMPRESSOR -> IXAirEditInteractor.ETab.COMP
						XTouchSessionState.EDynamicEncoder.AUTOMIX -> IXAirEditInteractor.ETab.MAIN
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

