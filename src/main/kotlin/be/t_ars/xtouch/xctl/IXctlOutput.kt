package be.t_ars.xtouch.xctl

interface IXctlOutput {
	enum class EChannelButton {
		REC, SOLO, MUTE, SELECT
	}

	enum class EButton {
		TRACK,
		SEND,
		PAN,
		PLUGIN,
		EQ,
		INST,
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

	fun setChannelButtonLED(channel: Int, channelButton: EChannelButton, mode: ELEDMode)
	fun setButtonLED(button: EButton, mode: ELEDMode)
	fun setLEDRing(channel: Int, index: Int?)
	fun setLEDRingWithHalves(channel: Int, index: Int?)
	fun setLEDRingContinuous(channel: Int, index: Int?)
	fun setLEDRingLeftRight(channel: Int, index: Int?)
	fun setLEDRingRaw(channel: Int, left: Byte, right: Byte)
	fun setDigits(number: Int)
	fun setMeter(channel: Int, value: Int)
	fun setMeters(values: IntArray)
	fun setFaderPosition(channel: Int, position: Float)
	fun setMainFaderPosition(position: Float)
}