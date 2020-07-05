package be.t_ars.xtouch

import be.t_ars.xtouch.settings.ISettingsManager
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.*
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants
import kotlin.system.exitProcess

class XAirEditProxyUI(
	private val settingsManager: ISettingsManager,
	private val windowClosingListener: () -> Unit,
	private val calibrationUpdater: (Int, Int, Int, Int) -> Unit
) : JFrame() {
	private inner class WListener : WindowAdapter() {
		override fun windowClosing(e: WindowEvent?) {
			saveProperties()
			windowClosingListener.invoke()
			exitProcess(0)
		}
	}

	private enum class ECalibrateState {
		NONE,
		TOP_RIGHT,
		BOTTOM_LEFT
	}

	private val statusLabel = JLabel("", SwingConstants.CENTER)
	private val calibrateButton = JButton()
	private var calibrateState = ECalibrateState.NONE
	private var left = 0
	private var right = 0
	private var top = 0
	private var bottom = 0

	init {
		isAlwaysOnTop = true

		val icon = this.javaClass.classLoader.getResource("app-icon.png")
		iconImage = Toolkit.getDefaultToolkit().createImage(icon)

		layout = GridBagLayout()

		statusLabel.isOpaque = true
		setConnected(false)

		add(statusLabel, GridBagConstraints().apply {
			fill = GridBagConstraints.BOTH
			gridx = 0
			gridy = 0
			weightx = 1.toDouble()
			weighty = 1.toDouble()
			insets = Insets(INSET, INSET, INSET, INSET)
		})

		calibrateButton.text = "Calibrate"
		calibrateButton.addActionListener {
			when (calibrateState) {
				ECalibrateState.NONE -> {
					calibrateButton.text = "Top right"
					calibrateState = ECalibrateState.TOP_RIGHT
				}
				ECalibrateState.TOP_RIGHT -> {
					calibrateButton.text = "Bottom left"
					calibrateState = ECalibrateState.BOTTOM_LEFT
					searchTopRight()
				}
				ECalibrateState.BOTTOM_LEFT -> {
					calibrateButton.text = "Calibrate"
					calibrateState = ECalibrateState.NONE
					searchBottomLeft()
				}
			}
		}
		add(calibrateButton, GridBagConstraints().apply {
			fill = GridBagConstraints.BOTH
			gridx = 0
			gridy = 1
			weightx = 1.toDouble()
			weighty = 1.toDouble()
			insets = Insets(INSET, INSET, INSET, INSET)
		})

		addWindowListener(WListener())

		loadProperties()
	}

	fun setConnected(connected: Boolean) {
		statusLabel.text = if (connected) "CONNECTED" else "DISCONNECTED"
		statusLabel.background = if (connected) OK_COLOR else NOK_COLOR
	}

	private fun searchTopRight() {
		val robot = Robot()
		MouseInfo.getPointerInfo().location.also {
			right = it.x
			top = it.y
		}
		val color = robot.getPixelColor(right, top)
		while (robot.getPixelColor(right + 1, top) == color) {
			++right
		}
		while (robot.getPixelColor(right, top - 1) == color) {
			--top
		}
	}

	private fun searchBottomLeft() {
		val robot = Robot()
		MouseInfo.getPointerInfo().location.also {
			left = it.x
			bottom = it.y
		}
		val color = robot.getPixelColor(left, bottom)
		while (robot.getPixelColor(left - 1, bottom) == color) {
			--left
		}
		while (robot.getPixelColor(left, bottom + 1) == color) {
			++bottom
		}
		calibrationUpdater.invoke(left, top, right, bottom)
	}

	private fun loadProperties() {
		val props = settingsManager.loadProperties("ui")
		val x = props.getProperty(PROP_WINDOW_X, "200").toInt()
		val y = props.getProperty(PROP_WINDOW_Y, "200").toInt()
		val w = props.getProperty(PROP_WINDOW_W, "140").toInt()
		val h = props.getProperty(PROP_WINDOW_H, "155").toInt()
		setBounds(x, y, w, h)
	}

	private fun saveProperties() {
		val props = Properties()
		props.setProperty(PROP_WINDOW_X, location.x.toString())
		props.setProperty(PROP_WINDOW_Y, location.y.toString())
		props.setProperty(PROP_WINDOW_W, size.width.toString())
		props.setProperty(PROP_WINDOW_H, size.height.toString())
		settingsManager.saveProperties("ui", props)
	}

	companion object {
		private const val PROP_WINDOW_X = "window.x"
		private const val PROP_WINDOW_Y = "window.y"
		private const val PROP_WINDOW_W = "window.w"
		private const val PROP_WINDOW_H = "window.h"

		private const val INSET = 10

		private val OK_COLOR = Color(53, 231, 45)
		private val NOK_COLOR = Color(231, 45, 46)
	}
}