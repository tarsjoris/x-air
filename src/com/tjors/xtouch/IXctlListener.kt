package com.tjors.xtouch

interface IXctlListener {
    fun channelRec(channel: Int) { }
    fun channelSolo(channel: Int) { }
    fun channelMute(channel: Int) { }
    fun channelSelect(channel: Int) { }
}