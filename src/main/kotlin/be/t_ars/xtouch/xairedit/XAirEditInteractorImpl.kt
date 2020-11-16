package be.t_ars.xtouch.xairedit

import be.t_ars.xtouch.settings.ISettingsManager
import kotlinx.coroutines.delay
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.util.*
import kotlin.math.roundToInt

class XAirEditInteractorImpl(private val settingsManager: ISettingsManager) :
	IXAirEditInteractor {
	private var offsetX: Int = 0
	private var offsetY: Int = 23
	private var xFactor: Float = 1F
	private var yFactor: Float = 1F

	private val robot = Robot()
	private var currentOutput: Int? = null
	private var currentChannel: Int? = null
	private var currentTab: IXAirEditInteractor.ETab? = null

	init {
		val props = settingsManager.loadProperties("interactor")
		val left = props.getProperty(PROP_LEFT, "0").toInt()
		val top = props.getProperty(PROP_TOP, "23").toInt()
		val right = props.getProperty(PROP_RIGHT, "1558").toInt()
		val bottom = props.getProperty(PROP_BOTTOM, "985").toInt()
		calibrationChanged(left, top, right, bottom)
	}

	private fun calibrationChanged(left: Int, top: Int, right: Int, bottom: Int) {
		offsetX = left
		offsetY = top
		xFactor = (right - left).toFloat() / RIGHT.toFloat()
		yFactor = (bottom - top).toFloat() / BOTTOM.toFloat()
	}

	override suspend fun clickChannel(channel: Int) =
		selectChannelInternal(channel)

	override suspend fun clickAux() =
		selectChannelInternal(CHANNEL_COUNT + 1)

	override suspend fun clickRtn(rtn: Int) =
		selectChannelInternal(CHANNEL_COUNT + 1 + rtn)

	private suspend fun selectChannelInternal(channel: Int) {
		if (currentChannel != channel) {
			currentChannel = channel
			click(CHANNEL1_X + (channel - 1) * CHANNEL_OFFSET_X, CHANNEL_Y)
		}
	}

	override suspend fun clickMainFader() {
		if (currentChannel != 0) {
			currentChannel = 0
			click(MAIN_FADER_X, MAIN_FADER_Y)
		}
	}

	override suspend fun clickMainLR() {
		if (currentOutput != 0) {
			currentOutput = 0
			click(MAIN_LR_X, MAIN_LR_Y)
		}
	}

	override suspend fun clickBus(bus: Int) {
		if (currentOutput != bus) {
			currentOutput = bus
			when (bus) {
				1 -> click(BUS_X1, BUS_Y1)
				2 -> click(BUS_X2, BUS_Y1)
				3 -> click(BUS_X1, BUS_Y2)
				4 -> click(BUS_X2, BUS_Y2)
				5 -> click(BUS_X1, BUS_Y3)
				6 -> click(BUS_X2, BUS_Y3)
			}
		}
	}

	override suspend fun clickFx(fx: Int) {
		if (currentOutput != 6 + fx) {
			currentOutput = 6 + fx
			click(FX_X, FX1_Y + (fx - 1) * FX_OFFSET_Y)
		}
	}

	override suspend fun clickTab(tab: IXAirEditInteractor.ETab) {
		if (currentTab != tab) {
			currentTab = tab
			click(
				when (tab) {
					IXAirEditInteractor.ETab.MIXER -> TAB_MIXER_X
					IXAirEditInteractor.ETab.CHANNEL -> TAB_CHANNEL_X
					IXAirEditInteractor.ETab.INPUT -> TAB_INPUT_X
					IXAirEditInteractor.ETab.GATE -> TAB_GATE_X
					IXAirEditInteractor.ETab.EQ -> TAB_EQ_X
					IXAirEditInteractor.ETab.COMP -> TAB_COMP_X
					IXAirEditInteractor.ETab.SENDS -> TAB_SENDS_X
					IXAirEditInteractor.ETab.MAIN -> TAB_MAIN_X
					IXAirEditInteractor.ETab.FX -> TAB_FX_X
					IXAirEditInteractor.ETab.METER -> TAB_METER_X
				},
				TAB_Y
			)
		}
	}

	override fun openEffectSettings(effect: Int) =
		keyPress(KeyEvent.VK_F1 + effect - 1)

	override fun closeDialog() =
		keyPress(KeyEvent.VK_ESCAPE)

	private suspend fun click(x: Int, y: Int) {
		robot.mouseMove(
			offsetX + (x.toFloat() * xFactor).roundToInt(),
			offsetY + (y.toFloat() * yFactor).roundToInt()
		)
		delay(10)
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
		delay(10)
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
		delay(10)
	}

	private fun keyPress(key: Int) {
		robot.keyPress(key)
		robot.keyRelease(key)
	}

	fun setCalibration(left: Int, top: Int, right: Int, bottom: Int) {
		val properties = Properties()
		properties.setProperty(PROP_LEFT, left.toString())
		properties.setProperty(PROP_TOP, top.toString())
		properties.setProperty(PROP_RIGHT, right.toString())
		properties.setProperty(PROP_BOTTOM, bottom.toString())
		settingsManager.saveProperties("interactor", properties)
		calibrationChanged(left, top, right, bottom)
	}

	companion object {
		private const val PROP_LEFT = "xair-edit.left"
		private const val PROP_TOP = "xair-edit.top"
		private const val PROP_RIGHT = "xair-edit.right"
		private const val PROP_BOTTOM = "xair-edit.bottom"

		const val RIGHT = 1559
		const val BOTTOM = 960

		const val TAB_MIXER_X = 56
		const val TAB_CHANNEL_X = 192
		const val TAB_INPUT_X = 321
		const val TAB_GATE_X = 430
		const val TAB_EQ_X = 531
		const val TAB_COMP_X = 647
		const val TAB_SENDS_X = 761
		const val TAB_MAIN_X = 876
		const val TAB_FX_X = 975
		const val TAB_METER_X = 1087
		const val TAB_Y = 26

		const val CHANNEL1_X = 33
		const val CHANNEL_OFFSET_X = 63
		const val CHANNEL_Y = 488

		const val BUS_X1 = 1372
		const val BUS_X2 = 1459
		const val BUS_Y1 = 625
		const val BUS_Y2 = 668
		const val BUS_Y3 = 707

		const val FX_X = 1372
		const val FX1_Y = 748
		const val FX_OFFSET_Y = 40

		const val MAIN_LR_X = 1415
		const val MAIN_LR_Y = 585

		const val MAIN_FADER_X = 1526
		const val MAIN_FADER_Y = 488

		const val MAIN_MUTE_X = 1529
		const val MAIN_MUTE_Y = 920

		private const val CHANNEL_COUNT = 16
	}
}