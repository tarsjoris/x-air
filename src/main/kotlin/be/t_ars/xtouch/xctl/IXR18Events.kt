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