package be.t_ars.xtouch.xctl

interface IConnectionToXR18 : IXTouchEvents {
	fun sendHeartbeat()
	fun channelRecPressed(channel: Int) {
		channelRecPressed(channel, true)
		channelRecPressed(channel, false)
	}

	fun channelSoloPressed(channel: Int) {
		channelSoloPressed(channel, true)
		channelSoloPressed(channel, false)
	}

	fun channelMutePressed(channel: Int) {
		channelMutePressed(channel, true)
		channelMutePressed(channel, false)
	}

	fun channelSelectPressed(channel: Int) {
		channelSelectPressed(channel, true)
		channelSelectPressed(channel, false)
	}

	fun knobPressed(knob: Int) {
		knobPressed(knob, true)
		knobPressed(knob, false)
	}

	fun encoderTrackPressed() {
		encoderTrackPressed(true)
		encoderTrackPressed(false)
	}

	fun encoderSendPressed() {
		encoderSendPressed(true)
		encoderSendPressed(false)
	}

	fun encoderPanPressed() {
		encoderPanPressed(true)
		encoderPanPressed(false)
	}

	fun encoderPluginPressed() {
		encoderPluginPressed(true)
		encoderPluginPressed(false)
	}

	fun encoderEqPressed() {
		encoderEqPressed(true)
		encoderEqPressed(false)
	}

	fun encoderInstPressed() {
		encoderInstPressed(true)
		encoderInstPressed(false)
	}

	fun previousBankPressed() {
		previousBankPressed(true)
		previousBankPressed(false)
	}

	fun nextBankPressed() {
		nextBankPressed(true)
		nextBankPressed(false)
	}

	fun previousChannelPressed() {
		previousChannelPressed(true)
		previousChannelPressed(false)
	}

	fun nextChannelPressed() {
		nextChannelPressed(true)
		nextChannelPressed(false)
	}

	fun flipPressed() {
		flipPressed(true)
		flipPressed(false)
	}

	fun globalViewPressed() {
		globalViewPressed(true)
		globalViewPressed(false)
	}

	fun displayPressed() {
		displayPressed(true)
		displayPressed(false)
	}

	fun smptePressed() {
		smptePressed(true)
		smptePressed(false)
	}

	fun functionPressed(function: Int) {
		functionPressed(function, true)
		functionPressed(function, false)
	}

	fun midiTracksPressed() {
		midiTracksPressed(true)
		midiTracksPressed(false)
	}

	fun inputsPressed() {
		inputsPressed(true)
		inputsPressed(false)
	}

	fun audioTracksPressed() {
		audioTracksPressed(true)
		audioTracksPressed(false)
	}

	fun audioInstPressed() {
		audioInstPressed(true)
		audioInstPressed(false)
	}

	fun auxPressed() {
		auxPressed(true)
		auxPressed(false)
	}

	fun busesPressed() {
		busesPressed(true)
		busesPressed(false)
	}

	fun outputsPressed() {
		outputsPressed(true)
		outputsPressed(false)
	}

	fun userPressed() {
		userPressed(true)
		userPressed(false)
	}

	fun modifyPressed(modify: Int) {
		modifyPressed(modify, true)
		modifyPressed(modify, false)
	}

	fun automationPressed(automation: Int) {
		automationPressed(automation, true)
		automationPressed(automation, false)
	}

	fun utiliyPressed(utility: Int) {
		utiliyPressed(utility, true)
		utiliyPressed(utility, false)
	}

	fun faderPressed(channel: Int) {
		faderPressed(channel, true)
		faderPressed(channel, false)
	}

	fun mainFaderPressed() {
		mainFaderPressed(true)
		mainFaderPressed(false)
	}
}