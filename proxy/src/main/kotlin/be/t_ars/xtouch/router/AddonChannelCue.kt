package be.t_ars.xtouch.router

import be.t_ars.xtouch.osc.IOSCListener
import be.t_ars.xtouch.osc.XR18OSCAPI
import be.t_ars.xtouch.session.IXTouchSessionListener
import be.t_ars.xtouch.session.XTouchSessionState
import be.t_ars.xtouch.util.partial
import be.t_ars.xtouch.xctl.AbstractButtonLEDEvent
import be.t_ars.xtouch.xctl.ButtonLEDEvent
import be.t_ars.xtouch.xctl.ChannelButtonLEDEvent
import be.t_ars.xtouch.xctl.EButton
import be.t_ars.xtouch.xctl.EChannelButton
import be.t_ars.xtouch.xctl.ELEDMode
import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IXR18Events
import be.t_ars.xtouch.xctl.IXTouchEvents
import be.t_ars.xtouch.xctl.IXctlConnectionListener
import be.t_ars.xtouch.xctl.XctlUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddonChannelCue(
	private val xr18OSCAPI: XR18OSCAPI
) : AbstractAddon(), IOSCListener, IXTouchSessionListener {
	private val connectionListener = ConnectionListener()
	private val xTouchListener = XTouchListener()
	private val xr18Listener = XR18Listener()

	// Session state
	private var currentOutput = XTouchSessionState.OUTPUT_MAINLR

	// XR18 state
	private var soloSource = IOSCListener.ESoloSource.OFF
	private var busLinked = mutableMapOf<IOSCListener.EBusLink, Boolean>().also {
		for (busLink in IOSCListener.EBusLink.values()) {
			it[busLink] = false
		}
	}

	// Session state
	private var busEncoderSelected = false

	// Router events
	override fun processConnectionEvent(event: Event<IXctlConnectionListener>) {
		event(connectionListener)
	}

	override fun getNextXTouchEvent(event: Event<IXTouchEvents>) =
		xTouchListener.processEvent(event)

	override fun getNextXR18Event(event: Event<IXR18Events>) =
		xr18Listener.processEvent(event)

	// Session events
	override fun selectionChanged(
		output: Int,
		channel: Int,
		encoder: XTouchSessionState.EEncoder?,
		dynamicEncoder: XTouchSessionState.EDynamicEncoder
	) {
		if (currentOutput != output) {
			currentOutput = output
			updateFlipButtonLED()
			updateChannelRecLEDS()
		}
		val newBusEncoderSelected = encoder == XTouchSessionState.EEncoder.BUS
		if (busEncoderSelected != newBusEncoderSelected) {
			busEncoderSelected = newBusEncoderSelected
			updateChannelRecLEDS()
		}
	}

	// XR18 events
	override suspend fun soloSource(source: IOSCListener.ESoloSource) {
		soloSource = source
		updateFlipButtonLED()
		updateChannelRecLEDS()
	}

	override suspend fun busLink(busLink: IOSCListener.EBusLink, on: Boolean) {
		busLinked[busLink] = on
	}

	private fun updateFlipButtonLED() {
		sendToXTouch(
			if (when (currentOutput) {
					XTouchSessionState.OUTPUT_MAINLR ->
						soloSource == IOSCListener.ESoloSource.LR
					XTouchSessionState.OUTPUT_BUS1 ->
						soloSource == IOSCListener.ESoloSource.BUS1 || soloSource == IOSCListener.ESoloSource.BUS12
					XTouchSessionState.OUTPUT_BUS2 ->
						soloSource == IOSCListener.ESoloSource.BUS2 || soloSource == IOSCListener.ESoloSource.BUS12
					XTouchSessionState.OUTPUT_BUS3 ->
						soloSource == IOSCListener.ESoloSource.BUS3 || soloSource == IOSCListener.ESoloSource.BUS34
					XTouchSessionState.OUTPUT_BUS4 ->
						soloSource == IOSCListener.ESoloSource.BUS4 || soloSource == IOSCListener.ESoloSource.BUS34
					XTouchSessionState.OUTPUT_BUS5 ->
						soloSource == IOSCListener.ESoloSource.BUS5 || soloSource == IOSCListener.ESoloSource.BUS56
					XTouchSessionState.OUTPUT_BUS6 ->
						soloSource == IOSCListener.ESoloSource.BUS6 || soloSource == IOSCListener.ESoloSource.BUS56
					else -> false
				}
			) FLIP_LED_ON_EVENT else FLIP_LED_OFF_EVENT
		)
	}

	private fun updateChannelRecLEDS() {
		if (busEncoderSelected && currentOutput == XTouchSessionState.OUTPUT_MAINLR) {
			sendToXTouch(
				partial(
					Array(XctlUtil.CHANNEL_COUNT) { channel ->
						ChannelButtonLEDEvent(
							channel + 1,
							EChannelButton.REC,
							if (isCueBus(channel - 1)) ELEDMode.ON else ELEDMode.OFF
						)
					},
					IXR18Events::setButtonLEDs
				)
			)
		} else {
			sendToXTouch(ALL_OFF_EVENTS)
		}
	}

	private fun isCueBus(bus: Int) =
		when (soloSource) {
			IOSCListener.ESoloSource.BUS1 -> bus == 1
			IOSCListener.ESoloSource.BUS2 -> bus == 2
			IOSCListener.ESoloSource.BUS3 -> bus == 3
			IOSCListener.ESoloSource.BUS4 -> bus == 4
			IOSCListener.ESoloSource.BUS5 -> bus == 5
			IOSCListener.ESoloSource.BUS6 -> bus == 6
			IOSCListener.ESoloSource.BUS12 -> bus == 1 || bus == 2
			IOSCListener.ESoloSource.BUS34 -> bus == 3 || bus == 4
			IOSCListener.ESoloSource.BUS56 -> bus == 5 || bus == 6
			else -> false
		}

	inner class ConnectionListener : IXctlConnectionListener {
		override fun connected() {
			GlobalScope.launch {
				delay(1000)
				xr18OSCAPI.requestSoloSource()
				for (busLink in IOSCListener.EBusLink.values()) {
					xr18OSCAPI.requestBusLink(busLink)
				}
			}
		}
	}

	inner class XTouchListener : AbstractAddonXTouchListener() {
		override fun flipPressed(down: Boolean) {
			if (down) {
				when (currentOutput) {
					XTouchSessionState.OUTPUT_MAINLR ->
						xr18OSCAPI.setSoloSource(IOSCListener.ESoloSource.LR)
					XTouchSessionState.OUTPUT_BUS1 ->
						selectBusOutput(IOSCListener.ESoloSource.BUS1)
					XTouchSessionState.OUTPUT_BUS2 ->
						selectBusOutput(IOSCListener.ESoloSource.BUS2)
					XTouchSessionState.OUTPUT_BUS3 ->
						selectBusOutput(IOSCListener.ESoloSource.BUS3)
					XTouchSessionState.OUTPUT_BUS4 ->
						selectBusOutput(IOSCListener.ESoloSource.BUS4)
					XTouchSessionState.OUTPUT_BUS5 ->
						selectBusOutput(IOSCListener.ESoloSource.BUS5)
					XTouchSessionState.OUTPUT_BUS6 ->
						selectBusOutput(IOSCListener.ESoloSource.BUS6)
				}
			}
			nextEvent = null
		}

		override fun channelRecPressed(channel: Int, down: Boolean) {
			if (down && busEncoderSelected) {
				when (channel) {
					3 -> selectBusOutput(IOSCListener.ESoloSource.BUS1)
					4 -> selectBusOutput(IOSCListener.ESoloSource.BUS2)
					5 -> selectBusOutput(IOSCListener.ESoloSource.BUS3)
					6 -> selectBusOutput(IOSCListener.ESoloSource.BUS4)
					7 -> selectBusOutput(IOSCListener.ESoloSource.BUS5)
					8 -> selectBusOutput(IOSCListener.ESoloSource.BUS6)
				}
				nextEvent = null
			}
		}

		private fun selectBusOutput(bus: IOSCListener.ESoloSource) {
			val busLink = when (bus) {
				IOSCListener.ESoloSource.BUS1 -> IOSCListener.EBusLink.BUS12
				IOSCListener.ESoloSource.BUS2 -> IOSCListener.EBusLink.BUS12
				IOSCListener.ESoloSource.BUS3 -> IOSCListener.EBusLink.BUS34
				IOSCListener.ESoloSource.BUS4 -> IOSCListener.EBusLink.BUS34
				IOSCListener.ESoloSource.BUS5 -> IOSCListener.EBusLink.BUS56
				else -> IOSCListener.EBusLink.BUS56
			}
			val isLinked = busLinked[busLink] ?: false
			val linkedBus = when (bus) {
				IOSCListener.ESoloSource.BUS1 -> IOSCListener.ESoloSource.BUS12
				IOSCListener.ESoloSource.BUS2 -> IOSCListener.ESoloSource.BUS12
				IOSCListener.ESoloSource.BUS3 -> IOSCListener.ESoloSource.BUS34
				IOSCListener.ESoloSource.BUS4 -> IOSCListener.ESoloSource.BUS34
				IOSCListener.ESoloSource.BUS5 -> IOSCListener.ESoloSource.BUS56
				else -> IOSCListener.ESoloSource.BUS56
			}
			val newSoloSource = if (isLinked) linkedBus else bus
			xr18OSCAPI.setSoloSource(if (soloSource != newSoloSource) newSoloSource else IOSCListener.ESoloSource.LR)
		}
	}

	inner class XR18Listener : AbstractAddonXR18Listener() {
		override fun setButtonLEDs(buttonLEDEvents: Array<AbstractButtonLEDEvent>) {
			val newEvents = buttonLEDEvents.mapNotNull { event ->
				if (event is ButtonLEDEvent && event.button == EButton.FLIP)
					null
				else
					event
			}
			nextEvent = { it.setButtonLEDs(newEvents.toTypedArray()) }
		}
	}

	companion object {
		private val ALL_OFF_EVENTS = partial(
			Array(XctlUtil.CHANNEL_COUNT) { channel ->
				ChannelButtonLEDEvent(channel + 1, EChannelButton.REC, ELEDMode.OFF)
			},
			IXR18Events::setButtonLEDs
		)

		private val FLIP_LED_ON_EVENT = partial(
			arrayOf(ButtonLEDEvent(EButton.FLIP, ELEDMode.ON)),
			IXR18Events::setButtonLEDs
		)
		private val FLIP_LED_OFF_EVENT = partial(
			arrayOf(ButtonLEDEvent(EButton.FLIP, ELEDMode.OFF)),
			IXR18Events::setButtonLEDs
		)
	}
}