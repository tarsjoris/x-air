package be.t_ars.xtouch.xctl

interface IXTouchListener {
	suspend fun channelRecPressed(channel: Int) {}
	suspend fun channelSoloPressed(channel: Int) {}
	suspend fun channelMutePressed(channel: Int) {}
	suspend fun channelSelectPressed(channel: Int) {}
	suspend fun knobPressed(knob: Int) {}
	suspend fun encoderTrackPressed() {}
	suspend fun encoderSendPressed() {}
	suspend fun encoderPanPressed() {}
	suspend fun encoderPluginPressed() {}
	suspend fun encoderEqPressed() {}
	suspend fun encoderInstPressed() {}
	suspend fun previousBankPressed() {}
	suspend fun nextBankPressed() {}
	suspend fun previousChannelPressed() {}
	suspend fun nextChannelPressed() {}
	suspend fun flipPressed() {}
	suspend fun globalViewPressed() {}
	suspend fun displayPressed() {}
	suspend fun smptePressed() {}
	suspend fun functionPressed(function: Int) {}
	suspend fun midiTracksPressed() {}
	suspend fun inputsPressed() {}
	suspend fun audioTracksPressed() {}
	suspend fun audioInstPressed() {}
	suspend fun auxPressed() {}
	suspend fun busesPressed() {}
	suspend fun outputsPressed() {}
	suspend fun userPressed() {}
	suspend fun modifyPressed(modify: Int) {}
	suspend fun automationPressed(automation: Int) {}
	suspend fun utiliyPressed(utility: Int) {}
	suspend fun knobRotated(knob: Int, right: Boolean) {}
	suspend fun faderMoved(channel: Int, position: Float) {}
	suspend fun mainFaderMoved(position: Float) {}
}