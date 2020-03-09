package com.tjors.xtouch

import java.net.Inet4Address

private class DebugListener: IXctlListener {
    override fun channelRec(channel: Int) {
        println("Channel $channel rec")
    }
    override fun channelSolo(channel: Int) {
        println("Channel $channel solo")
    }
    override fun channelSelect(channel: Int) {
        println("Channel $channel select")
    }

    override fun channelMute(channel: Int) {
        println("Channel $channel mute")
    }
}

fun main() {
    val xairAddress = Inet4Address.getByName("192.168.0.238")
    val device = XctlDevice(DebugListener(), xairAddress)
    device.use { _ ->
        val lock = java.lang.Object()
        synchronized(lock) {
            lock.wait()
        }
    }
}