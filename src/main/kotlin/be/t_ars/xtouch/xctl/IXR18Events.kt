package be.t_ars.xtouch.xctl

enum class EChannelButton {
	REC, SOLO, MUTE, SELECT
}

enum class EButton {
	ENCODER_TRACK,
	ENCODER_SEND,
	ENCODER_PAN,
	ENCODER_PLUGIN,
	ENCODER_EQ,
	ENCODER_INST,
	PREV_BANK,
	NEXT_BANK,
	PREV_CHANNEL,
	NEXT_CHANNEL,
	FLIP,
	GLOBAL_VIEW,
	F1,
	F2,
	F3,
	F4,
	F5,
	F6,
	F7,
	F8,
	MIDI_TRACKS,
	INPUTS,
	AUDIO_TRACKS,
	AUDIO_INST,
	AUX,
	BUSES,
	OUTPUTS,
	USER,
	MODIFY_SHIFT,
	MODIFY_OPTION,
	MODIFY_CONTROL,
	MODIFY_ALT,
	AUTOMATION_READ,
	AUTOMATION_WRITE,
	AUTOMATION_TRIM,
	AUTOMATION_TOUCH,
	AUTOMATION_LATCH,
	AUTOMATION_GROUP,
	UTILITY_SAVE,
	UTILITY_UNDO,
	UTILITY_CANCEL,
	UTILITY_ENTER,
	SOLO
}

enum class ELEDMode {
	OFF, FLASH, ON
}

enum class EScribbleColor {
	BLACK, RED, GREEN, YELLOW, BLUE, PINK, CYAN, WHITE
}

data class Meter(val channel: Int, val value: Int)

sealed class AbstractButtonLEDEvent(val mode: ELEDMode)
class ChannelButtonLEDEvent(val channel: Int, val channelButton: EChannelButton, mode: ELEDMode) :
	AbstractButtonLEDEvent(mode)

class ButtonLEDEvent(val button: EButton, mode: ELEDMode) : AbstractButtonLEDEvent(mode)
class UnusedButtonLEDEvent(val id: Byte, mode: ELEDMode) : AbstractButtonLEDEvent(mode)

sealed class AbstractFaderEvent(val position: Int)
class ChannelFaderEvent(val channel: Int, position: Int) : AbstractFaderEvent(position)
class MainFaderEvent(position: Int) : AbstractFaderEvent(position)

sealed class AbstractLEDEvent(val data: Byte)
class LEDRingEvent(val channel: Int, val left: Boolean, data: Byte) : AbstractLEDEvent(data)
class DigitEvent(val index: Int, data: Byte) : AbstractLEDEvent(data)

class ScribbleStripEvent(
	val channel: Int,
	val color: EScribbleColor,
	val secondLineInverted: Boolean,
	val line1: String,
	val line2: String
)

interface IXR18Events {
	fun setButtonLEDs(buttonLEDEvents: Array<AbstractButtonLEDEvent>)
	fun setLEDs(ledEvents: Array<AbstractLEDEvent>)
	fun setMeters(meters: Array<Meter>)
	fun setFaderPositions(faderEvents: Array<AbstractFaderEvent>)
	fun setScribbleTrips(scribbleStripEvents: Array<ScribbleStripEvent>)
	fun systemMessage(message: ByteArray)
}

fun IXR18Events.setChannelButtonLED(channel: Int, channelButton: EChannelButton, mode: ELEDMode) {
	setButtonLEDs(arrayOf(ChannelButtonLEDEvent(channel, channelButton, mode)))
}

fun IXR18Events.setButtonLED(button: EButton, mode: ELEDMode) {
	setButtonLEDs(arrayOf(ButtonLEDEvent(button, mode)))
}

fun IXR18Events.setLEDRingSingle(channel: Int, index: Int?) {
	if (index == null || index in 0..12) {
		when (index) {
			0 -> setLEDRing(channel, 0x01.toByte(), 0x00.toByte())
			1 -> setLEDRing(channel, 0x02.toByte(), 0x00.toByte())
			2 -> setLEDRing(channel, 0x04.toByte(), 0x00.toByte())
			3 -> setLEDRing(channel, 0x08.toByte(), 0x00.toByte())
			4 -> setLEDRing(channel, 0x10.toByte(), 0x00.toByte())
			5 -> setLEDRing(channel, 0x20.toByte(), 0x00.toByte())
			6 -> setLEDRing(channel, 0x40.toByte(), 0x00.toByte())
			7 -> setLEDRing(channel, 0x00.toByte(), 0x01.toByte())
			8 -> setLEDRing(channel, 0x00.toByte(), 0x02.toByte())
			9 -> setLEDRing(channel, 0x00.toByte(), 0x04.toByte())
			10 -> setLEDRing(channel, 0x00.toByte(), 0x08.toByte())
			11 -> setLEDRing(channel, 0x00.toByte(), 0x10.toByte())
			12 -> setLEDRing(channel, 0x00.toByte(), 0x20.toByte())
			else -> setLEDRing(channel, 0x00.toByte(), 0x00.toByte())
		}
	}
}

fun IXR18Events.setLEDRingWithHalves(channel: Int, index: Int?) {
	if (index == null || index in 0..24) {
		when (index) {
			0 -> setLEDRing(channel, 0x01.toByte(), 0x00.toByte())
			1 -> setLEDRing(channel, 0x03.toByte(), 0x00.toByte())
			2 -> setLEDRing(channel, 0x02.toByte(), 0x00.toByte())
			3 -> setLEDRing(channel, 0x06.toByte(), 0x00.toByte())
			4 -> setLEDRing(channel, 0x04.toByte(), 0x00.toByte())
			5 -> setLEDRing(channel, 0x0C.toByte(), 0x00.toByte())
			6 -> setLEDRing(channel, 0x08.toByte(), 0x00.toByte())
			7 -> setLEDRing(channel, 0x18.toByte(), 0x00.toByte())
			8 -> setLEDRing(channel, 0x10.toByte(), 0x00.toByte())
			9 -> setLEDRing(channel, 0x30.toByte(), 0x00.toByte())
			10 -> setLEDRing(channel, 0x20.toByte(), 0x00.toByte())
			11 -> setLEDRing(channel, 0x60.toByte(), 0x00.toByte())
			12 -> setLEDRing(channel, 0x40.toByte(), 0x00.toByte())
			13 -> setLEDRing(channel, 0x40.toByte(), 0x01.toByte())
			14 -> setLEDRing(channel, 0x00.toByte(), 0x01.toByte())
			15 -> setLEDRing(channel, 0x00.toByte(), 0x03.toByte())
			16 -> setLEDRing(channel, 0x00.toByte(), 0x02.toByte())
			17 -> setLEDRing(channel, 0x00.toByte(), 0x06.toByte())
			18 -> setLEDRing(channel, 0x00.toByte(), 0x04.toByte())
			19 -> setLEDRing(channel, 0x00.toByte(), 0x0C.toByte())
			20 -> setLEDRing(channel, 0x00.toByte(), 0x08.toByte())
			21 -> setLEDRing(channel, 0x00.toByte(), 0x18.toByte())
			22 -> setLEDRing(channel, 0x00.toByte(), 0x10.toByte())
			23 -> setLEDRing(channel, 0x00.toByte(), 0x30.toByte())
			24 -> setLEDRing(channel, 0x00.toByte(), 0x20.toByte())
			else -> setLEDRing(channel, 0x00.toByte(), 0x00.toByte())
		}
	}
}

fun IXR18Events.setLEDRingContinuous(channel: Int, index: Int?) {
	if (index == null || index in 0..12) {
		when (index) {
			0 -> setLEDRing(channel, 0x01.toByte(), 0x00.toByte())
			1 -> setLEDRing(channel, 0x03.toByte(), 0x00.toByte())
			2 -> setLEDRing(channel, 0x07.toByte(), 0x00.toByte())
			3 -> setLEDRing(channel, 0x0F.toByte(), 0x00.toByte())
			4 -> setLEDRing(channel, 0x1F.toByte(), 0x00.toByte())
			5 -> setLEDRing(channel, 0x3F.toByte(), 0x00.toByte())
			6 -> setLEDRing(channel, 0x7F.toByte(), 0x00.toByte())
			7 -> setLEDRing(channel, 0x7F.toByte(), 0x01.toByte())
			8 -> setLEDRing(channel, 0x7F.toByte(), 0x03.toByte())
			9 -> setLEDRing(channel, 0x7F.toByte(), 0x07.toByte())
			10 -> setLEDRing(channel, 0x7F.toByte(), 0x0F.toByte())
			11 -> setLEDRing(channel, 0x7F.toByte(), 0x1F.toByte())
			12 -> setLEDRing(channel, 0x7F.toByte(), 0x3F.toByte())
			else -> setLEDRing(channel, 0x00.toByte(), 0x00.toByte())
		}
	}
}

fun IXR18Events.setLEDRingLeftRight(channel: Int, index: Int?) {
	if (index == null || index in -6..6) {
		when (index) {
			-6 -> setLEDRing(channel, 0x7F.toByte(), 0x00.toByte())
			-5 -> setLEDRing(channel, 0x7E.toByte(), 0x00.toByte())
			-4 -> setLEDRing(channel, 0x7C.toByte(), 0x00.toByte())
			-3 -> setLEDRing(channel, 0x78.toByte(), 0x00.toByte())
			-2 -> setLEDRing(channel, 0x70.toByte(), 0x00.toByte())
			-1 -> setLEDRing(channel, 0x60.toByte(), 0x00.toByte())
			0 -> setLEDRing(channel, 0x40.toByte(), 0x00.toByte())
			1 -> setLEDRing(channel, 0x40.toByte(), 0x01.toByte())
			2 -> setLEDRing(channel, 0x40.toByte(), 0x03.toByte())
			3 -> setLEDRing(channel, 0x40.toByte(), 0x07.toByte())
			4 -> setLEDRing(channel, 0x40.toByte(), 0x0F.toByte())
			5 -> setLEDRing(channel, 0x40.toByte(), 0x1F.toByte())
			6 -> setLEDRing(channel, 0x40.toByte(), 0x3F.toByte())
			else -> setLEDRing(channel, 0x00.toByte(), 0x00.toByte())
		}
	}
}

fun IXR18Events.setLEDRing(channel: Int, left: Byte, right: Byte) {
	XctlUtil.validateChannel(channel)
	setLEDs(arrayOf(LEDRingEvent(channel, true, left), LEDRingEvent(channel, false, right)))
}

fun IXR18Events.setLEDRingRaw(channel: Int, left: Boolean, data: Byte) {
	setLEDs(arrayOf(LEDRingEvent(channel, left, data)))
}

fun IXR18Events.setDigits(number: Int) {
	setLEDs(
		arrayOf(
			DigitEvent(0, if (number > 9) DIGITS[number.div(10).rem(10)] else 0x00.toByte()),
			DigitEvent(1, DIGITS[number.rem(10)]),
		)
	)
}

fun IXR18Events.setDigitRaw(index: Int, data: Byte) {
	setLEDs(arrayOf(DigitEvent(index, data)))
}

fun IXR18Events.setMeter(channel: Int, value: Int) {
	setMeters(arrayOf(Meter(channel, value)))
}

fun IXR18Events.setChannelFaderPosition(channel: Int, position: Int) {
	setFaderPositions(arrayOf(ChannelFaderEvent(channel, position)))
}

fun IXR18Events.setMainFaderPosition(position: Int) {
	setFaderPositions(arrayOf(MainFaderEvent(position)))
}

fun IXR18Events.setScribbleTrip(
	channel: Int,
	color: EScribbleColor,
	secondLineInverted: Boolean,
	line1: String,
	line2: String
) {
	setScribbleTrip(ScribbleStripEvent(channel, color, secondLineInverted, line1, line2))
}

fun IXR18Events.setScribbleTrip(event: ScribbleStripEvent) {
	setScribbleTrips(arrayOf(event))
}

private val DIGIT_LINES = byteArrayOf(0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40)
private val DIGITS = byteArrayOf(
	(DIGIT_LINES[0] + DIGIT_LINES[1] + DIGIT_LINES[2] + DIGIT_LINES[3] + DIGIT_LINES[4] + DIGIT_LINES[5]).toByte(), // 0
	(DIGIT_LINES[1] + DIGIT_LINES[2]).toByte(), // 1
	(DIGIT_LINES[0] + DIGIT_LINES[1] + DIGIT_LINES[3] + DIGIT_LINES[4] + DIGIT_LINES[6]).toByte(), // 2
	(DIGIT_LINES[0] + DIGIT_LINES[1] + DIGIT_LINES[2] + DIGIT_LINES[3] + DIGIT_LINES[6]).toByte(), // 3
	(DIGIT_LINES[1] + DIGIT_LINES[2] + DIGIT_LINES[5] + DIGIT_LINES[6]).toByte(), // 4
	(DIGIT_LINES[0] + DIGIT_LINES[2] + DIGIT_LINES[3] + DIGIT_LINES[5] + DIGIT_LINES[6]).toByte(), // 5
	(DIGIT_LINES[0] + DIGIT_LINES[2] + DIGIT_LINES[3] + DIGIT_LINES[4] + DIGIT_LINES[5] + DIGIT_LINES[6]).toByte(), // 6
	(DIGIT_LINES[0] + DIGIT_LINES[1] + DIGIT_LINES[2]).toByte(), // 7
	(DIGIT_LINES[0] + DIGIT_LINES[1] + DIGIT_LINES[2] + DIGIT_LINES[3] + DIGIT_LINES[4] + DIGIT_LINES[5] + DIGIT_LINES[6]).toByte(), // 8
	(DIGIT_LINES[0] + DIGIT_LINES[1] + DIGIT_LINES[2] + DIGIT_LINES[3] + DIGIT_LINES[5] + DIGIT_LINES[6]).toByte() // 9
)