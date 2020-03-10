package com.tjors.xtouch

interface IXctlListener {
    fun channelRec(channel: Int) { }
    fun channelSolo(channel: Int) { }
    fun channelMute(channel: Int) { }
    fun channelSelect(channel: Int) { }
    fun track() { }
    fun send() { }
    fun pan() { }
    fun plugin() { }
    fun eq() { }
    fun inst() { }
    fun previousBank() { }
    fun nextBank() { }
    fun previousChannel() { }
    fun nextChannel() { }
    fun flip() { }
    fun globalView() { }
    fun fxSelect(fx: Int) { }
    fun busSelect(bus: Int) { }
}