package be.t_ars.xtouch

import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.util.*

class XAirEditInteractorImpl(private val settingsManager: ISettingsManager) : IXAirEditInteractor {
	private var offsetX: Int = 0
	private var offsetY: Int = 23
	private var xFactor: Float = 1F
	private var yFactor: Float = 1F

	private val robot = Robot()

	init {
		val props = settingsManager.loadProperties("interctor")
		val left = props.getProperty(PROP_LEFT, "0").toInt()
		val top = props.getProperty(PROP_TOP, "23").toInt()
		val right = props.getProperty(PROP_RIGHT, "1558").toInt()
		val bottom = props.getProperty(PROP_BOTTOM, "985").toInt()
		calibrationChanged(left, top, right, bottom)
	}

	private fun calibrationChanged(left: Int, top: Int, right: Int, bottom: Int) {
		offsetX = left - LEFT
		offsetY = top - TOP
		xFactor = (right - left).toFloat() / (RIGHT - LEFT).toFloat()
		yFactor = (bottom - top).toFloat() / (BOTTOM - TOP).toFloat()
	}

	override fun clickChannel(channel: Int) =
		click(CHANNEL1_X + (channel - 1) * CHANNEL_OFFSET_X, CHANNEL_Y)

	override fun clickAux() =
		click(CHANNEL1_X + CHANNEL_COUNT * CHANNEL_OFFSET_X, CHANNEL_Y)

	override fun clickRtn(rtn: Int) =
		click(CHANNEL1_X + (rtn + CHANNEL_COUNT) * CHANNEL_OFFSET_X, CHANNEL_Y)

	override fun clickMainFader() =
		click(MAIN_FADER_X, MAIN_FADER_Y)

	override fun clickMainLR() =
		click(MAIN_LR_X, MAIN_LR_Y)

	override fun clickBus(bus: Int) {
		when (bus) {
			1 -> click(BUS_X1, BUS_Y1)
			2 -> click(BUS_X2, BUS_Y1)
			3 -> click(BUS_X1, BUS_Y2)
			4 -> click(BUS_X2, BUS_Y2)
			5 -> click(BUS_X1, BUS_Y3)
			6 -> click(BUS_X2, BUS_Y3)
		}
	}

	override fun clickFx(fx: Int) =
		click(FX_X, FX1_Y + (fx - 1) * FX_OFFSET_Y)

	override fun clickTab(tab: IXAirEditInteractor.ETab) =
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

	override fun openEffectSettings(effect: Int) =
		keyPress(KeyEvent.VK_F1 + effect - 1)

	override fun closeDialog() =
		keyPress(KeyEvent.VK_ESCAPE)

	private fun click(x: Int, y: Int) {
		robot.mouseMove(offsetX + (x.toFloat() * xFactor).toInt(), offsetY + (y.toFloat() * yFactor).toInt())
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
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

		private const val LEFT = 2
		private const val TOP = 0
		private const val RIGHT = 1559
		private const val BOTTOM = 960

		private const val TAB_MIXER_X = 56
		private const val TAB_CHANNEL_X = 192
		private const val TAB_INPUT_X = 321
		private const val TAB_GATE_X = 430
		private const val TAB_EQ_X = 531
		private const val TAB_COMP_X = 647
		private const val TAB_SENDS_X = 761
		private const val TAB_MAIN_X = 876
		private const val TAB_FX_X = 975
		private const val TAB_METER_X = 1087
		private const val TAB_Y = 26

		private const val CHANNEL1_X = 33
		private const val CHANNEL_OFFSET_X = 63
		private const val CHANNEL_Y = 488

		private const val BUS_X1 = 1372
		private const val BUS_X2 = 1459
		private const val BUS_Y1 = 625
		private const val BUS_Y2 = 668
		private const val BUS_Y3 = 707

		private const val FX_X = 1372
		private const val FX1_Y = 748
		private const val FX_OFFSET_Y = 40

		private const val MAIN_LR_X = 1415
		private const val MAIN_LR_Y = 585

		private const val MAIN_FADER_X = 1526
		private const val MAIN_FADER_Y = 488

		private const val CHANNEL_COUNT = 16
	}
}