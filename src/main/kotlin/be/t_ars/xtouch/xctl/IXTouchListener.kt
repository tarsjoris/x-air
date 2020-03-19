package be.t_ars.xtouch.xctl

interface IXTouchListener {
	fun channelRecPressed(channel: Int) {}
	fun channelSoloPressed(channel: Int) {}
	fun channelMutePressed(channel: Int) {}
	fun channelSelectPressed(channel: Int) {}
	fun knobPressed(knob: Int) {}
	fun encoderTrackPressed() {}
	fun encoderSendPressed() {}
	fun encoderPanPressed() {}
	fun encoderPluginPressed() {}
	fun encoderEqPressed() {}
	fun encoderInstPressed() {}
	fun previousBankPressed() {}
	fun nextBankPressed() {}
	fun previousChannelPressed() {}
	fun nextChannelPressed() {}
	fun flipPressed() {}
	fun globalViewPressed() {}
	fun displayPressed() {}
	fun smptePressed() {}
	fun functionPressed(function: Int) {}
	fun midiTracksPressed() {}
	fun inputsPressed() {}
	fun audioTracksPressed() {}
	fun audioInstPressed() {}
	fun auxPressed() {}
	fun busesPressed() {}
	fun outputsPressed() {}
	fun userPressed() {}
	fun modifyPressed(modify: Int) {}
	fun automationPressed(automation: Int) {}
	fun knobRotated(knob: Int, right: Boolean) {}
	fun faderMoved(channel: Int, position: Float) { }
	fun mainFaderMoved(position: Float) { }
}