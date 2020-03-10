package com.tjors.xtouch

import java.awt.Robot
import java.awt.event.InputEvent
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.net.Inet4Address
import java.util.*

private const val WIDTH = 1559
private const val HEIGHT = 963

private const val TAB_MIXER_X = 56
private const val TAB_CHANNEL_X = 192
private const val TAB_EQ_X = 531
private const val TAB_SENDS_X = 761
private const val TAB_Y = 26

private const val CHANNEL1_X = 33
private const val CHANNEL_OFFSET_X = 63
private const val CHANNEL_Y = 488

private const val BUS_X1 = 1372
private const val BUS_X2 = 1459
private const val BUS_Y1 = 625
private const val BUS_Y2 = 668
private const val BUS_Y3 = 707

private const val FX_X = 1372
private const val FX1_Y = 748
private const val FX_OFFSET_Y = 40

private const val MAIN_X = 1415
private const val MAIN_Y = 585

private const val CHANNEL_COUNT = 16
private const val AUX_CHANNEL = CHANNEL_COUNT + 1
private const val RTN1_CHANNEL = AUX_CHANNEL + 1
private const val RTN2_CHANNEL = RTN1_CHANNEL + 1
private const val RTN3_CHANNEL = RTN2_CHANNEL + 1
private const val RTN4_CHANNEL = RTN3_CHANNEL + 1
private const val BUS1_CHANNEL = RTN4_CHANNEL + 1
private const val BUS2_CHANNEL = BUS1_CHANNEL + 1
private const val BUS3_CHANNEL = BUS2_CHANNEL + 1
private const val BUS4_CHANNEL = BUS3_CHANNEL + 1
private const val BUS5_CHANNEL = BUS4_CHANNEL + 1
private const val BUS6_CHANNEL = BUS5_CHANNEL + 1
private const val FX1_CHANNEL = BUS6_CHANNEL + 1
private const val FX2_CHANNEL = FX1_CHANNEL + 1
private const val FX3_CHANNEL = FX2_CHANNEL + 1
private const val FX4_CHANNEL = FX3_CHANNEL + 1
private const val MAIN_CHANNEL = FX4_CHANNEL + 1

private const val BANK_COUNT = 5

private class XAirEditController(x1: Int, y1: Int, x2: Int, y2: Int) : IXctlListener {
    private val offsetX = x1
    private val offsetY = y1
    private val xFactor = (x2 - x1 + 1).toFloat() / WIDTH.toFloat()
    private val yFactor = (y2 - y1 + 1).toFloat() / HEIGHT.toFloat()

    private val robot = Robot()

    private var currentChannel = 1
    private var currentBank = 1
    private var currentBus: Int? = null

    override fun channelSelect(channel: Int) {
        currentChannel = when (currentBank) {
            1 -> channel
            2 -> channel + 8
            3 -> when (channel) {
                1 -> AUX_CHANNEL
                5 -> RTN1_CHANNEL
                6 -> RTN2_CHANNEL
                7 -> RTN3_CHANNEL
                8 -> RTN4_CHANNEL
                else -> currentChannel
            }
            4 -> when (channel) {
                1 -> BUS1_CHANNEL
                2 -> BUS2_CHANNEL
                3 -> BUS3_CHANNEL
                4 -> BUS4_CHANNEL
                5 -> BUS5_CHANNEL
                6 -> BUS6_CHANNEL
                8 -> MAIN_CHANNEL
                else -> currentChannel
            }
            5 -> when (channel) {
                1 -> FX1_CHANNEL
                2 -> FX2_CHANNEL
                3 -> FX3_CHANNEL
                4 -> FX4_CHANNEL
                else -> currentChannel
            }
            else -> currentChannel
        }
        highlightCurrentChannel()
    }

    override fun track() {
        hightlightMain()
        click(TAB_CHANNEL_X, TAB_Y)
    }

    override fun send() {
        hightlightMain()
        click(TAB_SENDS_X, TAB_Y)
    }

    override fun pan() {}

    override fun plugin() {}

    override fun eq() {
        hightlightMain()
        click(TAB_EQ_X, TAB_Y)
    }

    override fun inst() {}

    override fun previousBank() {
        if (currentBank > 1) {
            --currentBank
        }
    }

    override fun nextBank() {
        if (currentBank < BANK_COUNT) {
            ++currentBank
        }
    }

    override fun previousChannel() {
        if (currentChannel > 1) {
            --currentChannel
            highlightCurrentChannel()
        }
    }

    override fun nextChannel() {
        if (currentChannel < MAIN_CHANNEL) {
            ++currentChannel
            highlightCurrentChannel()
        }
    }

    override fun flip() =
        globalView()

    override fun globalView() {
        hightlightMain()
        click(TAB_MIXER_X, TAB_Y)
    }

    override fun fxSelect(fx: Int) =
        click(FX_X, FX1_Y + (fx - 1) * FX_OFFSET_Y)

    override fun busSelect(bus: Int) {
        if (currentBus == bus) {
            hightlightMain()
        } else {
            currentBus = bus
            when (bus) {
                1 ->
                    click(BUS_X1, BUS_Y1)
                2 ->
                    click(BUS_X2, BUS_Y1)
                3 ->
                    click(BUS_X1, BUS_Y2)
                4 ->
                    click(BUS_X2, BUS_Y2)
                5 ->
                    click(BUS_X1, BUS_Y3)
                6 ->
                    click(BUS_X2, BUS_Y3)
            }
        }
    }

    private fun hightlightMain() {
        currentBus = null
        click(MAIN_X, MAIN_Y)
    }

    private fun highlightCurrentChannel() {
        when (currentChannel) {
            in 1..CHANNEL_COUNT ->
                click(CHANNEL1_X + (currentChannel - 1) * CHANNEL_OFFSET_X, CHANNEL_Y)
            AUX_CHANNEL ->
                click(CHANNEL1_X + (AUX_CHANNEL - 1) * CHANNEL_OFFSET_X, CHANNEL_Y)
            in FX1_CHANNEL..FX4_CHANNEL ->
                click(CHANNEL1_X + (currentChannel - 11) * CHANNEL_OFFSET_X, CHANNEL_Y)
        }
    }

    private fun click(x: Int, y: Int) {
        robot.mouseMove(offsetX + (x.toFloat() * xFactor).toInt(), offsetY + (y.toFloat() * yFactor).toInt())
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    }
}

fun main() {
    val props = Properties()
    File("xtouch.properties")
        .takeIf(File::exists)
        ?.also { file ->
            BufferedInputStream(FileInputStream(file)).use(props::load)
        }
    val x1 = props.getProperty("xair-edit.x1", "0").toInt()
    val y1 = props.getProperty("xair-edit.y1", "23").toInt()
    val x2 = props.getProperty("xair-edit.x2", "1558").toInt()
    val y2 = props.getProperty("xair-edit.y2", "985").toInt()
    val ipAddress = props.getProperty("xr18.ipaddress", "192.168.0.3")
    println("Use XAiR XR18 device at $ipAddress")
    val controller = XAirEditController(x1, y1, x2, y2)
    XctlDevice(controller, Inet4Address.getByName(ipAddress)).use {
        val lock = Object()
        synchronized(lock) {
            lock.wait()
        }
    }
}