package be.t_ars.xtouch.ui

import be.t_ars.xtouch.settings.ISettingsManager
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants
import kotlin.system.exitProcess

class XAirEditProxyUI(
	private val settingsManager: ISettingsManager,
	private val windowClosingListener: () -> Unit,
	xtouchProxyEnabled: Boolean,
	calibrationUpdater: ((Int, Int, Int, Int) -> Unit)?,
	searchAction: (() -> Unit)?,
	monitorMixLink: String?
) : JFrame() {
	private inner class WListener : WindowAdapter() {
		override fun windowClosing(e: WindowEvent?) {
			saveProperties()
			windowClosingListener()
			exitProcess(0)
		}
	}

	private val properties = settingsManager.loadProperties("ui")
	private val xtouchProxyStatusLabel: JLabel?
	private val searchButton: JButton?

	init {
		isAlwaysOnTop = true

		val icon = this.javaClass.classLoader.getResource("app-icon.png")
		iconImage = Toolkit.getDefaultToolkit().createImage(icon)

		layout = GridBagLayout()

		if (xtouchProxyEnabled) {
			xtouchProxyStatusLabel = JLabel("", SwingConstants.CENTER)
			xtouchProxyStatusLabel.isOpaque = true
			setConnected(false)
			add(xtouchProxyStatusLabel, GridBagConstraints().apply {
				fill = GridBagConstraints.BOTH
				gridx = 0
				gridy = 0
				weightx = 1.toDouble()
				weighty = 1.toDouble()
				insets = Insets(INSET, INSET, INSET, INSET)
			})
		} else {
			xtouchProxyStatusLabel = null
		}

		if (calibrationUpdater != null) {
			val calibrateButton = JButton()
			calibrateButton.text = "Calibrate"
			calibrateButton.addActionListener {
				val calibrateOpque = properties.getProperty(PROP_CALIBRATE_OPAQUE, "true").toBoolean()
				val callibrateFrame = CallibrateFrame(calibrateOpque, calibrationUpdater)
				callibrateFrame.start()
			}
			add(calibrateButton, GridBagConstraints().apply {
				fill = GridBagConstraints.BOTH
				gridx = 0
				gridy = 1
				weightx = 1.toDouble()
				weighty = 1.toDouble()
				insets = Insets(INSET, INSET, INSET, INSET)
			})
		}

		if (searchAction != null) {
			searchButton = JButton("Search")
			searchButton.addActionListener {
				searchAction()
			}
			add(searchButton, GridBagConstraints().apply {
				fill = GridBagConstraints.BOTH
				gridx = 0
				gridy = 2
				weightx = 1.toDouble()
				weighty = 1.toDouble()
				insets = Insets(INSET, INSET, INSET, INSET)
			})
		} else {
			searchButton = null
		}

		if (monitorMixLink != null) {
			val qrCode = createBarcode(monitorMixLink)
			add(JLabel(ImageIcon(qrCode)), GridBagConstraints().apply {
				gridx = 0
				gridy = 3
				insets = Insets(INSET, INSET, INSET, INSET)
			})
		}

		addWindowListener(WListener())

		loadProperties()
	}

	fun setConnected(connected: Boolean) {
		xtouchProxyStatusLabel?.apply {
			text = if (connected) "CONNECTED" else "DISCONNECTED"
			background = if (connected) OK_COLOR else NOK_COLOR
		}
	}

	private fun loadProperties() {
		val x = properties.getProperty(PROP_WINDOW_X, "200").toInt()
		val y = properties.getProperty(PROP_WINDOW_Y, "200").toInt()
		val w = properties.getProperty(PROP_WINDOW_W, "140").toInt()
		val h = properties.getProperty(PROP_WINDOW_H, "155").toInt()
		setBounds(x, y, w, h)
	}

	private fun saveProperties() {
		properties.setProperty(PROP_WINDOW_X, location.x.toString())
		properties.setProperty(PROP_WINDOW_Y, location.y.toString())
		properties.setProperty(PROP_WINDOW_W, size.width.toString())
		properties.setProperty(PROP_WINDOW_H, size.height.toString())
		settingsManager.saveProperties("ui", properties)
	}

	companion object {
		private const val PROP_WINDOW_X = "window.x"
		private const val PROP_WINDOW_Y = "window.y"
		private const val PROP_WINDOW_W = "window.w"
		private const val PROP_WINDOW_H = "window.h"
		private const val PROP_CALIBRATE_OPAQUE = "calibrate.opaque"

		private const val INSET = 10

		private val OK_COLOR = Color(53, 231, 45)
		private val NOK_COLOR = Color(231, 45, 46)
	}
}