package be.t_ars.xtouch.ui

import be.t_ars.xtouch.xairedit.XAirEditInteractorImpl
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import kotlin.math.roundToInt

class CallibrateFrame(
	private val calibrateOpque: Boolean,
	private val callibrationUpdate: (Int, Int, Int, Int) -> Unit
) : JFrame() {
	private val statusLabel = JLabel("", SwingConstants.CENTER)
	private val cancelButton = JButton()
	private val saveButton = JButton()
	private val adjustPanel = JPanel()

	private var state = EState.MIXER
	private var mixerX: Int = 0
	private var mixerY: Int = 0
	private var rect: Rectangle? = null

	init {
		isAlwaysOnTop = true
		isUndecorated = true
		if (calibrateOpque) {
			background = Color(255, 255, 255, 100)
		} else {
			background = Color(255, 255, 255, 0)
		}

		val panel = JPanel()
		panel.background = BACKGROUND_COLOR
		panel.layout = GridBagLayout()

		panel.add(statusLabel, GridBagConstraints().apply {
			fill = GridBagConstraints.BOTH
			gridx = 0
			gridwidth = 2
			gridy = 0
			weightx = 1.toDouble()
			weighty = 1.toDouble()
			insets = Insets(INSET, INSET, INSET, INSET)
		})

		adjustPanel.isVisible = false
		adjustPanel.layout = GridBagLayout()
		adjustPanel.isOpaque = false

		var indexY = 0
		listOf(
			"Top-left" to listOf(
				"<" to { adjustRect(-1, 0, 0, 0) },
				"^" to { adjustRect(0, -1, 0, 0) },
				"v" to { adjustRect(0, 1, 0, 0) },
				">" to { adjustRect(1, 0, 0, 0) }
			),
			"Bottom-right" to listOf(
				"<" to { adjustRect(0, 0, -1, 0) },
				"^" to { adjustRect(0, 0, 0, -1) },
				"v" to { adjustRect(0, 0, 0, 1) },
				">" to { adjustRect(0, 0, 1, 0) }
			)
		).forEach { (panelLabel, actions) ->
			val adjustLabel = JLabel(panelLabel)
			adjustPanel.add(adjustLabel, GridBagConstraints().apply {
				fill = GridBagConstraints.NONE
				gridx = 0
				gridy = indexY
				insets = Insets(INSET, INSET, INSET, INSET)
			})

			var indexX = 1
			actions.forEach { (label, action) ->
				val adjustButton = JButton(label)
				adjustButton.addActionListener {
					action()
				}
				adjustPanel.add(adjustButton, GridBagConstraints().apply {
					fill = GridBagConstraints.NONE
					gridx = indexX++
					gridy = indexY
					insets = Insets(INSET, INSET, INSET, INSET)
				})
			}
			indexY++
		}

		panel.add(adjustPanel, GridBagConstraints().apply {
			fill = GridBagConstraints.NONE
			gridx = 0
			gridwidth = 2
			gridy = 1
			weightx = 1.toDouble()
			weighty = 1.toDouble()
		})

		cancelButton.text = "Cancel"
		cancelButton.addActionListener {
			this@CallibrateFrame.isVisible = false
		}

		panel.add(cancelButton, GridBagConstraints().apply {
			fill = GridBagConstraints.NONE
			gridx = 0
			gridy = 2
			weightx = 1.toDouble()
			weighty = 1.toDouble()
			insets = Insets(INSET, INSET, INSET, INSET)
		})

		saveButton.text = "Save"
		saveButton.addActionListener {
			val r = rect
			if (r != null) {
				callibrationUpdate(r.x, r.y, r.x + r.width, r.y + r.height)
			}
			this@CallibrateFrame.isVisible = false
		}
		saveButton.isVisible = false

		panel.add(saveButton, GridBagConstraints().apply {
			fill = GridBagConstraints.NONE
			gridx = 1
			gridy = 2
			weightx = 1.toDouble()
			weighty = 1.toDouble()
			insets = Insets(INSET, INSET, INSET, INSET)
		})

		val backgroundPanel = object : JPanel() {
			override fun paintComponent(g: Graphics) {
				val newGraphics = g.create()
				if (newGraphics is Graphics2D) {
					paintMixer(newGraphics)
				}
			}
		}
		contentPane = backgroundPanel

		layout = GridBagLayout()
		add(panel, GridBagConstraints().apply {
			fill = GridBagConstraints.NONE
			gridx = 0
			gridy = 0
			weightx = 1.toDouble()
			weighty = 1.toDouble()
		})

		addMouseListener(ClickListener())

		setBounds(100, 100, 500, 500)
	}

	private fun adjustRect(deltaX1: Int, deltaY1: Int, deltaX2: Int, deltaY2: Int) {
		val r = rect
		if (r != null) {
			rect = Rectangle(r.x + deltaX1, r.y + deltaY1, r.width - deltaX1 + deltaX2, r.height - deltaY1 + deltaY2)
			this@CallibrateFrame.repaint()
		}
	}

	fun start() {
		extendedState = MAXIMIZED_BOTH
		isVisible = true
		statusLabel.text = "Click on the mixer button (top-left)"
	}

	private fun paintMixer(g: Graphics2D) {
		val r = rect
		if (r != null) {
			val loc = contentPane.locationOnScreen
			g.translate(-loc.x, -loc.y)
			paintOutline(g, r.x, r.y, r.x + r.width, r.y + r.height)
		}
	}

	inner class ClickListener : MouseAdapter() {
		override fun mouseClicked(e: MouseEvent?) {
			when (state) {
				EState.MIXER -> {
					mixerX = e?.xOnScreen ?: 0
					mixerY = e?.yOnScreen ?: 0
					state = EState.MUTE
					statusLabel.text = "Click on the main mute button (bottom-right)"
				}
				EState.MUTE -> {
					rect = getRectFor(mixerX, mixerY, e?.xOnScreen ?: 0, e?.yOnScreen ?: 0)
					state = EState.DONE
					statusLabel.text = "Finetune until all dots hit the correct buttons"
					adjustPanel.isVisible = true
					saveButton.isVisible = true
					this@CallibrateFrame.repaint()
				}
				else -> {
					// nothing
				}
			}
		}
	}

	enum class EState {
		MIXER, MUTE, DONE
	}

	companion object {
		private const val INSET = 10

		private val BACKGROUND_COLOR = Color(200, 200, 200)

		fun getRectFor(mixerX: Int, mixerY: Int, muteX: Int, muteY: Int): Rectangle {
			val factorX =
				(muteX - mixerX).toFloat() / (XAirEditInteractorImpl.MAIN_MUTE_X - XAirEditInteractorImpl.TAB_MIXER_X).toFloat()
			val factorY =
				(muteY - mixerY).toFloat() / (XAirEditInteractorImpl.MAIN_MUTE_Y - XAirEditInteractorImpl.TAB_Y).toFloat()
			val left = mixerX - XAirEditInteractorImpl.TAB_MIXER_X * factorX
			val top = mixerY - XAirEditInteractorImpl.TAB_Y * factorY
			return Rectangle(
				left.roundToInt(),
				top.roundToInt(),
				(XAirEditInteractorImpl.RIGHT * factorX).roundToInt(),
				(XAirEditInteractorImpl.BOTTOM * factorY).roundToInt()
			)
		}

		fun paintOutline(g: Graphics2D, left: Int, top: Int, right: Int, bottom: Int) {
			val xFactor = (right - left).toFloat() / XAirEditInteractorImpl.RIGHT.toFloat()
			val yFactor = (bottom - top).toFloat() / XAirEditInteractorImpl.BOTTOM.toFloat()

			g.paint = Color(0, 0, 255)
			g.drawRect(left, top, right - left + 1, bottom - top + 1)

			// tabs
			drawDot(g, XAirEditInteractorImpl.TAB_MIXER_X, XAirEditInteractorImpl.TAB_Y, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.TAB_CHANNEL_X, XAirEditInteractorImpl.TAB_Y, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.TAB_INPUT_X, XAirEditInteractorImpl.TAB_Y, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.TAB_GATE_X, XAirEditInteractorImpl.TAB_Y, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.TAB_EQ_X, XAirEditInteractorImpl.TAB_Y, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.TAB_COMP_X, XAirEditInteractorImpl.TAB_Y, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.TAB_SENDS_X, XAirEditInteractorImpl.TAB_Y, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.TAB_MAIN_X, XAirEditInteractorImpl.TAB_Y, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.TAB_FX_X, XAirEditInteractorImpl.TAB_Y, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.TAB_METER_X, XAirEditInteractorImpl.TAB_Y, left, top, xFactor, yFactor)

			//channels
			for (i in 0 until 21) {
				drawDot(
					g, XAirEditInteractorImpl.CHANNEL1_X + i * XAirEditInteractorImpl.CHANNEL_OFFSET_X,
					XAirEditInteractorImpl.CHANNEL_Y, left, top, xFactor, yFactor
				)
			}

			drawDot(
				g,
				XAirEditInteractorImpl.MAIN_FADER_X,
				XAirEditInteractorImpl.MAIN_FADER_Y, left, top, xFactor, yFactor
			)
			drawDot(g, XAirEditInteractorImpl.MAIN_LR_X, XAirEditInteractorImpl.MAIN_LR_Y, left, top, xFactor, yFactor)

			drawDot(g, XAirEditInteractorImpl.BUS_X1, XAirEditInteractorImpl.BUS_Y1, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.BUS_X2, XAirEditInteractorImpl.BUS_Y1, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.BUS_X1, XAirEditInteractorImpl.BUS_Y2, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.BUS_X2, XAirEditInteractorImpl.BUS_Y2, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.BUS_X1, XAirEditInteractorImpl.BUS_Y3, left, top, xFactor, yFactor)
			drawDot(g, XAirEditInteractorImpl.BUS_X2, XAirEditInteractorImpl.BUS_Y3, left, top, xFactor, yFactor)

			for (i in 0 until 4) {
				drawDot(
					g,
					XAirEditInteractorImpl.FX_X,
					XAirEditInteractorImpl.FX1_Y + i * XAirEditInteractorImpl.FX_OFFSET_Y,
					left,
					top,
					xFactor,
					yFactor
				)
			}
		}

		private fun drawDot(g: Graphics2D, x: Int, y: Int, offsetX: Int, offsetY: Int, xFactor: Float, yFactor: Float) {
			val xPos = offsetX + (x.toFloat() * xFactor).roundToInt()
			val yPos = offsetY + (y.toFloat() * yFactor).roundToInt()
			g.fillOval(xPos - 5, yPos - 5, 10, 10)
		}
	}
}