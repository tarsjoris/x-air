package com.tjors.xtouch

import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants

class XAirEditProxyUI(private val connection: XctlConnection) : JFrame() {
	private inner class WListener : WindowAdapter() {
		override fun windowClosing(e: WindowEvent?) {
			connection.stop()
			saveProperties()
		}
	}

	private inner class XctlListener : IXctlListener {
		override fun connected() =
			setConnected(true)

		override fun disconnected() =
			setConnected(false)
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

		layout = GridBagLayout()

		statusLabel.isOpaque = true
		setConnected(false)

		add(statusLabel, GridBagConstraints().apply {
			fill = GridBagConstraints.BOTH
			gridx = 0
			gridy = 0
			weightx = 1.toDouble()
			weighty = 1.toDouble()
			insets = Insets(10, 10, 10, 10)
		})

		calibrateButton.text = "Calibrate"
		calibrateButton.addActionListener { _ ->
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
			insets = Insets(10, 10, 10, 10)
		})

		addWindowListener(WListener())

		connection.addListener(XctlListener())

		loadProperties()
	}

	private fun setConnected(connected: Boolean) {
		statusLabel.text = if (connected) "CONNECTED" else "DISCONNECTED"
		statusLabel.background = if (connected) Color.GREEN else Color.RED
	}

	private fun searchTopRight() {
		val color = Color(47, 47, 47)
		val robot = Robot()
		MouseInfo.getPointerInfo().location.also {
			right = it.x
			top = it.y
		}
		if (robot.getPixelColor(right, top) != color) {
			calibrateButton.text = "Error"
			calibrateState = ECalibrateState.NONE
			return
		}
		while (robot.getPixelColor(right + 1, top) == color) {
			++right
		}
		while (robot.getPixelColor(right, top - 1) == color) {
			--top
		}
	}

	private fun searchBottomLeft() {
		val color = Color(33, 33, 33)
		val robot = Robot()
		MouseInfo.getPointerInfo().location.also {
			left = it.x
			bottom = it.y
		}
		if (robot.getPixelColor(left, bottom) != color) {
			calibrateButton.text = "Error"
			calibrateState = ECalibrateState.NONE
			return
		}
		while (robot.getPixelColor(left - 1, bottom) == color) {
			--left
		}
		while (robot.getPixelColor(left, bottom + 1) == color) {
			++bottom
		}
		XAirEditInteractor.setCalibration(left, top, right, bottom)
	}

	private fun loadProperties() {
		UI_PROPERTIES_FILE
			.takeIf(File::exists)
			?.also { file ->
				val props = Properties()
				BufferedInputStream(FileInputStream(file)).use(props::load)
				val x = props.getProperty(PROP_WINDOW_X, "200").toInt()
				val y = props.getProperty(PROP_WINDOW_Y, "200").toInt()
				val w = props.getProperty(PROP_WINDOW_W, "150").toInt()
				val h = props.getProperty(PROP_WINDOW_H, "150").toInt()
				setBounds(x, y, w, h)
			}
	}

	private fun saveProperties() {
		val props = Properties()
		props.setProperty(PROP_WINDOW_X, location.x.toString())
		props.setProperty(PROP_WINDOW_Y, location.y.toString())
		props.setProperty(PROP_WINDOW_W, size.width.toString())
		props.setProperty(PROP_WINDOW_H, size.height.toString())
		BufferedOutputStream(FileOutputStream(UI_PROPERTIES_FILE)).use {
			props.store(it, "XAir Edit Proxy UI settings")
		}
	}

	companion object {
		private val UI_PROPERTIES_FILE = File("ui.properties")
		private const val PROP_WINDOW_X = "window.x"
		private const val PROP_WINDOW_Y = "window.y"
		private const val PROP_WINDOW_W = "window.w"
		private const val PROP_WINDOW_H = "window.h"
	}
}