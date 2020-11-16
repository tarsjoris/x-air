package be.t_ars.xtouch.xctl

interface IXTouchListener : IXTouchEvents {
	fun channelRecPressedDown(channel: Int) {}
	override fun channelRecPressed(channel: Int, down: Boolean) {
		if (down) {
			channelRecPressedDown(channel)
		}
	}

	fun channelSoloPressedDown(channel: Int) {}
	override fun channelSoloPressed(channel: Int, down: Boolean) {
		if (down) {
			channelSoloPressedDown(channel)
		}
	}

	fun channelMutePressedDown(channel: Int) {}
	override fun channelMutePressed(channel: Int, down: Boolean) {
		if (down) {
			channelMutePressedDown(channel)
		}
	}

	fun channelSelectPressedDown(channel: Int) {}
	override fun channelSelectPressed(channel: Int, down: Boolean) {
		if (down) {
			channelSelectPressedDown(channel)
		}
	}

	fun knobPressedDown(knob: Int) {}
	override fun knobPressed(knob: Int, down: Boolean) {
		if (down) {
			knobPressedDown(knob)
		}
	}

	fun encoderTrackPressedDown() {}
	override fun encoderTrackPressed(down: Boolean) {
		if (down) {
			encoderTrackPressedDown()
		}
	}

	fun encoderSendPressedDown() {}
	override fun encoderSendPressed(down: Boolean) {
		if (down) {
			encoderSendPressedDown()
		}
	}

	fun encoderPanPressedDown() {}
	override fun encoderPanPressed(down: Boolean) {
		if (down) {
			encoderPanPressedDown()
		}
	}

	fun encoderPluginPressedDown() {}
	override fun encoderPluginPressed(down: Boolean) {
		if (down) {
			encoderPluginPressedDown()
		}
	}

	fun encoderEqPressedDown() {}
	override fun encoderEqPressed(down: Boolean) {
		if (down) {
			encoderEqPressedDown()
		}
	}

	fun encoderInstPressedDown() {}
	override fun encoderInstPressed(down: Boolean) {
		if (down) {
			encoderInstPressedDown()
		}
	}

	fun previousBankPressedDown() {}
	override fun previousBankPressed(down: Boolean) {
		if (down) {
			previousBankPressedDown()
		}
	}

	fun nextBankPressedDown() {}
	override fun nextBankPressed(down: Boolean) {
		if (down) {
			nextBankPressedDown()
		}
	}

	fun previousChannelPressedDown() {}
	override fun previousChannelPressed(down: Boolean) {
		if (down) {
			previousChannelPressedDown()
		}
	}

	fun nextChannelPressedDown() {}
	override fun nextChannelPressed(down: Boolean) {
		if (down) {
			nextChannelPressedDown()
		}
	}

	fun flipPressedDown() {}
	override fun flipPressed(down: Boolean) {
		if (down) {
			flipPressedDown()
		}
	}

	fun globalViewPressedDown() {}
	override fun globalViewPressed(down: Boolean) {
		if (down) {
			globalViewPressedDown()
		}
	}

	fun displayPressedDown() {}
	override fun displayPressed(down: Boolean) {
		if (down) {
			displayPressedDown()
		}
	}

	fun smptePressedDown() {}
	override fun smptePressed(down: Boolean) {
		if (down) {
			smptePressedDown()
		}
	}

	fun functionPressedDown(function: Int) {}
	override fun functionPressed(function: Int, down: Boolean) {
		if (down) {
			functionPressedDown(function)
		}
	}

	fun midiTracksPressedDown() {}
	override fun midiTracksPressed(down: Boolean) {
		if (down) {
			midiTracksPressedDown()
		}
	}

	fun inputsPressedDown() {}
	override fun inputsPressed(down: Boolean) {
		if (down) {
			inputsPressedDown()
		}
	}

	fun audioTracksPressedDown() {}
	override fun audioTracksPressed(down: Boolean) {
		if (down) {
			audioTracksPressedDown()
		}
	}

	fun audioInstPressedDown() {}
	override fun audioInstPressed(down: Boolean) {
		if (down) {
			audioInstPressedDown()
		}
	}

	fun auxPressedDown() {}
	override fun auxPressed(down: Boolean) {
		if (down) {
			auxPressedDown()
		}
	}

	fun busesPressedDown() {}
	override fun busesPressed(down: Boolean) {
		if (down) {
			busesPressedDown()
		}
	}

	fun outputsPressedDown() {}
	override fun outputsPressed(down: Boolean) {
		if (down) {
			outputsPressedDown()
		}
	}

	fun userPressedDown() {}
	override fun userPressed(down: Boolean) {
		if (down) {
			userPressedDown()
		}
	}

	fun modifyPressedDown(modify: Int) {}
	override fun modifyPressed(modify: Int, down: Boolean) {
		if (down) {
			modifyPressedDown(modify)
		}
	}

	fun automationPressedDown(automation: Int) {}
	override fun automationPressed(automation: Int, down: Boolean) {
		if (down) {
			automationPressedDown(automation)
		}
	}

	fun utiliyPressedDown(utility: Int) {}
	override fun utiliyPressed(utility: Int, down: Boolean) {
		if (down) {
			utiliyPressedDown(utility)
		}
	}

	fun faderPressedDown(channel: Int) {}
	override fun faderPressed(channel: Int, down: Boolean) {
		if (down) {
			faderPressedDown(channel)
		}
	}

	fun mainFaderPressedDown() {}
	override fun mainFaderPressed(down: Boolean) {
		if (down) {
			mainFaderPressedDown()
		}
	}

	override fun knobRotated(knob: Int, right: Boolean) {}
	override fun faderMoved(channel: Int, position: Int) {}
	override fun mainFaderMoved(position: Int) {}
	override fun systemMessage(message: ByteArray) {}
}