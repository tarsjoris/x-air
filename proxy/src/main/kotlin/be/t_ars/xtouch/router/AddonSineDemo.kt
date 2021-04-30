package be.t_ars.xtouch.router

import be.t_ars.xtouch.xctl.AbstractFaderEvent
import be.t_ars.xtouch.xctl.ChannelFaderEvent
import be.t_ars.xtouch.xctl.Event
import be.t_ars.xtouch.xctl.IXR18Events
import be.t_ars.xtouch.xctl.IXTouchEvents
import be.t_ars.xtouch.xctl.MainFaderEvent
import be.t_ars.xtouch.xctl.XctlUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt

class AddonSineDemo : AbstractAddon() {
	private val xTouchListener = XTouchListener()
	private val xr18Listener = XR18Listener()

	private val faderPositions: Array<Int> = Array(XctlUtil.CHANNEL_COUNT + 1) {
		0
	}

	@Volatile
	private var demoRunning = false
	private var demoJob: Job? = null

	// Router events
	override fun getNextXTouchEvent(event: Event<IXTouchEvents>) =
		xTouchListener.processEvent(event)

	override fun getNextXR18Event(event: Event<IXR18Events>) =
		xr18Listener.processEvent(event)

	inner class XTouchListener : AbstractAddonXTouchListener() {
		override fun faderMoved(channel: Int, position: Int) {
			if (demoRunning) {
				nextEvent = null
			} else {
				faderPositions[channel - 1] = position
			}
		}

		override fun mainFaderMoved(position: Int) {
			if (demoRunning) {
				nextEvent = null
			} else {
				faderPositions[XctlUtil.CHANNEL_COUNT] = position
			}
		}

		override fun displayPressedDown() {
			toggleSineDemo()
		}
	}

	inner class XR18Listener : AbstractAddonXR18Listener() {
		override fun setFaderPositions(faderEvents: Array<AbstractFaderEvent>) {
			if (demoRunning) {
				nextEvent = null
			}
			faderEvents.forEach { event ->
				when (event) {
					is ChannelFaderEvent -> faderPositions[event.channel - 1] = event.position
					is MainFaderEvent -> faderPositions[XctlUtil.CHANNEL_COUNT] = event.position
				}
			}
		}
	}

	private fun toggleSineDemo() {
		//faderPositions.forEachIndexed { index, pos -> println("[$index]=$pos")  }
		demoRunning = !demoRunning
		if (demoRunning) {
			demoJob = GlobalScope.launch {
				doSine()
			}
		} else {
			runBlocking {
				demoJob?.join()
			}
			sendToXTouch {
				it.setFaderPositions(
					Array(XctlUtil.CHANNEL_COUNT + 1) { index ->
						when (index) {
							in 0 until XctlUtil.CHANNEL_COUNT -> ChannelFaderEvent(index + 1, faderPositions[index])
							else -> MainFaderEvent(faderPositions[index])
						}
					}
				)
			}
		}
	}

	private suspend fun doSine() {
		while (demoRunning) {
			animateFadersSine()
		}
	}

	private suspend fun animateFadersSine() {
		for (angle in 1..360) {
			if (!demoRunning) {
				return
			}
			sendToXTouch {
				it.setFaderPositions(
					Array(XctlUtil.CHANNEL_COUNT + 1) { channel ->
						generateFaderEvent(angle, channel)
					}
				)
			}
			delay(10)
		}
	}

	private fun generateFaderEvent(i: Int, channel: Int): AbstractFaderEvent {
		val angleDegrees = i.toFloat() - (channel.toFloat() / 9F) * 360F
		val angle = angleDegrees / 360F * 2 * PI
		val fraction = (1F - cos(angle).toFloat()) / 2F
		val position = fraction.times(XctlUtil.FADER_POSIION_RANGE.last).roundToInt()
		return if (channel < XctlUtil.CHANNEL_COUNT) {
			ChannelFaderEvent(channel + 1, position)
		} else {
			MainFaderEvent(position)
		}
	}
}