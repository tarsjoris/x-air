package com.tjors.xtouch

interface IXctlListener {
    fun channelRecPressed(channel: Int) { }
    fun channelSoloPressed(channel: Int) { }
    fun channelMutePressed(channel: Int) { }
    fun channelSelectPressed(channel: Int) { }
    fun knobPressed(knob: Int) { }
    fun encoderTrackPressed() { }
    fun encoderSendPressed() { }
    fun encoderPanPressed() { }
    fun encoderPluginPressed() { }
    fun encoderEqPressed() { }
    fun encoderInstPressed() { }
    fun previousBankPressed() { }
    fun nextBankPressed() { }
    fun previousChannelPressed() { }
    fun nextChannelPressed() { }
    fun flipPressed() { }
    fun globalViewPressed() { }
    fun fxSelectPressed(fx: Int) { }
    fun busSelectPressed(bus: Int) { }
    fun knobRotated(knob: Int, right: Boolean) { }
}