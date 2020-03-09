package com.tjors.xtouch

import java.awt.Robot
import java.awt.event.InputEvent
import java.net.Inet4Address

private const val OFFSET_X = 0
private const val OFFSET_Y = 23

private const val CHANNEL1_X1 = 3
private const val CHANNEL1_X2 = 62
private const val CHANNEL_X_OFFSET = 63
private const val CHANNEL_Y1 = 491 - OFFSET_Y
private const val CHANNEL_Y2 = 530 - OFFSET_Y
private const val CHANNEL1_MIDDLE_X = 33
private const val CHANNEL_MIDDLE_Y = 511 - OFFSET_Y

private class XAirEditController : IXctlListener {
    private val robot = Robot()

    override fun channelSelect(channel: Int) {
        robot.mouseMove(OFFSET_X + CHANNEL1_MIDDLE_X + (channel - 1) * CHANNEL_X_OFFSET, OFFSET_Y + CHANNEL_MIDDLE_Y)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    }
}

fun main() {
    XctlDevice(XAirEditController(), Inet4Address.getByName("192.168.0.238")).use {
        val lock = java.lang.Object()
        synchronized(lock) {
            lock.wait()
        }
    }
}