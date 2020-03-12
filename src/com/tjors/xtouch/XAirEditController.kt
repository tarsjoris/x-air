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
private const val TAB_INPUT_X = 321
private const val TAB_GATE_X = 430
private const val TAB_EQ_X = 531
private const val TAB_COMP_X = 647
private const val TAB_SENDS_X = 761
private const val TAB_MAIN_X = 876
private const val TAB_FX_X = 975
private const val TAB_METER_X = 1087
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

private const val MAIN_LR_X = 1415
private const val MAIN_LR_Y = 585

private const val MAIN_FADER_X = 1526
private const val MAIN_FADER_Y = 488

private const val CHANNEL_COUNT = 16
private const val CHANNEL_AUX = CHANNEL_COUNT + 1
private const val CHANNEL_RTN1 = CHANNEL_AUX + 1
private const val CHANNEL_RTN2 = CHANNEL_RTN1 + 1
private const val CHANNEL_RTN3 = CHANNEL_RTN2 + 1
private const val CHANNEL_RTN4 = CHANNEL_RTN3 + 1
private const val CHANNEL_BUS1 = CHANNEL_RTN4 + 1
private const val CHANNEL_BUS2 = CHANNEL_BUS1 + 1
private const val CHANNEL_BUS3 = CHANNEL_BUS2 + 1
private const val CHANNEL_BUS4 = CHANNEL_BUS3 + 1
private const val CHANNEL_BUS5 = CHANNEL_BUS4 + 1
private const val CHANNEL_BUS6 = CHANNEL_BUS5 + 1
private const val CHANNEL_FX1 = CHANNEL_BUS6 + 1
private const val CHANNEL_FX2 = CHANNEL_FX1 + 1
private const val CHANNEL_FX3 = CHANNEL_FX2 + 1
private const val CHANNEL_FX4 = CHANNEL_FX3 + 1
private const val CHANNEL_MAIN = CHANNEL_FX4 + 1

private const val BANK_COUNT = 5

private val BANK_CHANNELS = arrayOf(
    arrayOf(1, 2, 3, 4, 5, 6, 7, 8),
    arrayOf(9, 10, 11, 12, 13, 14, 15, 16),
    arrayOf(CHANNEL_AUX, null, null, null, CHANNEL_RTN1, CHANNEL_RTN2, CHANNEL_RTN3, CHANNEL_RTN4),
    arrayOf(CHANNEL_BUS1, CHANNEL_BUS2, CHANNEL_BUS3, CHANNEL_BUS4, CHANNEL_BUS5, CHANNEL_BUS6, null, CHANNEL_MAIN),
    arrayOf(CHANNEL_FX1, CHANNEL_FX2, CHANNEL_FX3, CHANNEL_FX4, null, null, null, null)
)

private const val OUTPUT_MAINLR = 1
private const val OUTPUT_FX1 = OUTPUT_MAINLR + 1
private const val OUTPUT_FX2 = OUTPUT_FX1 + 1
private const val OUTPUT_FX3 = OUTPUT_FX2 + 1
private const val OUTPUT_FX4 = OUTPUT_FX3 + 1
private const val OUTPUT_BUS1 = OUTPUT_FX4 + 1
private const val OUTPUT_BUS2 = OUTPUT_BUS1 + 1
private const val OUTPUT_BUS3 = OUTPUT_BUS2 + 1
private const val OUTPUT_BUS4 = OUTPUT_BUS3 + 1
private const val OUTPUT_BUS5 = OUTPUT_BUS4 + 1
private const val OUTPUT_BUS6 = OUTPUT_BUS5 + 1

private enum class EEncoder(val perChannel: Boolean) {
    TRACK(false),
    PAN(false),
    EQ(true),
    SEND(true),
    PLUGIN(true),
    INST(true)
}

private enum class ETab {
    MIXER,
    CHANNEL,
    INPUT,
    GATE,
    EQ,
    COMP,
    SENDS,
    MAIN,
    FX,
    METER
}

private val INST_TABS = arrayOf(ETab.GATE, ETab.COMP, ETab.CHANNEL)

private class XAirEditController(x1: Int, y1: Int, x2: Int, y2: Int) : IXctlListener {
    private val offsetX = x1
    private val offsetY = y1
    private val xFactor = (x2 - x1 + 1).toFloat() / WIDTH.toFloat()
    private val yFactor = (y2 - y1 + 1).toFloat() / HEIGHT.toFloat()

    private val robot = Robot()

    private var currentOutput = OUTPUT_MAINLR
    private var currentEncoder: EEncoder? = null
    private var currentTab = ETab.MIXER
    private var currentInstTab = 0
    private var currentChannel = 1
    private var currentBank = 1

    override fun channelSelectPressed(channel: Int) {
        currentChannel = when (currentBank) {
            1 -> channel
            2 -> channel + 8
            3 -> when (channel) {
                1 -> CHANNEL_AUX
                5 -> CHANNEL_RTN1
                6 -> CHANNEL_RTN2
                7 -> CHANNEL_RTN3
                8 -> CHANNEL_RTN4
                else -> currentChannel
            }
            4 -> when (channel) {
                1 -> CHANNEL_BUS1
                2 -> CHANNEL_BUS2
                3 -> CHANNEL_BUS3
                4 -> CHANNEL_BUS4
                5 -> CHANNEL_BUS5
                6 -> CHANNEL_BUS6
                8 -> CHANNEL_MAIN
                else -> currentChannel
            }
            5 -> when (channel) {
                1 -> CHANNEL_FX1
                2 -> CHANNEL_FX2
                3 -> CHANNEL_FX3
                4 -> CHANNEL_FX4
                else -> currentChannel
            }
            else -> currentChannel
        }
        selectCurrentChannel()
    }

    override fun encoderTrackPressed() =
        selectEncoder(EEncoder.TRACK)

    override fun encoderSendPressed() =
        selectEncoder(EEncoder.SEND)

    override fun encoderPanPressed() =
        selectEncoder(EEncoder.PAN)

    override fun encoderPluginPressed() = // fx
        selectEncoder(EEncoder.PLUGIN)

    override fun encoderEqPressed() =
        selectEncoder(EEncoder.EQ)

    override fun encoderInstPressed() =
        selectEncoder(EEncoder.INST)

    override fun previousBankPressed() {
        if (currentBank > 1) {
            switchChannelForBankSwitchInEncoder(-1)
            --currentBank
        }
    }

    override fun nextBankPressed() {
        if (currentBank < BANK_COUNT) {
            switchChannelForBankSwitchInEncoder(1)
            ++currentBank
        }
    }

    private fun switchChannelForBankSwitchInEncoder(offset: Int) {
        if (currentEncoder?.perChannel == true) {
            BANK_CHANNELS[currentBank - 1]
                .indexOf(currentChannel)
                .takeIf { it in 0..7 }
                ?.let { BANK_CHANNELS[currentBank - 1 + offset][it] }
                ?.also {
                    currentChannel = it
                    selectCurrentChannel()
                }
        }
    }

    override fun previousChannelPressed() {
        if (currentChannel > 1) {
            --currentChannel
            selectCurrentChannel()
        }
    }

    override fun nextChannelPressed() {
        if (currentChannel < CHANNEL_MAIN) {
            ++currentChannel
            selectCurrentChannel()
        }
    }

    override fun flipPressed() =
        globalViewPressed()

    override fun globalViewPressed() {
        currentEncoder = null
        selectOutput(OUTPUT_MAINLR)
        selectTab(ETab.MIXER)
        selectCurrentChannel()
    }

    override fun fxSelectPressed(fx: Int) {
        if (currentOutput - OUTPUT_FX1 + 1 == fx) {
            selectOutput(OUTPUT_MAINLR)
            selectCurrentEncoder()
        } else {
            selectOutput(OUTPUT_FX1 + fx - 1)
            selectTab(ETab.MIXER)
        }
    }

    override fun busSelectPressed(bus: Int) {
        if (currentOutput - OUTPUT_BUS1 + 1 == bus) {
            selectOutput(OUTPUT_MAINLR)
            selectCurrentEncoder()
        } else {
            selectOutput(OUTPUT_BUS1 + bus - 1)
            selectTab(ETab.MIXER)
        }
    }

    override fun knobRotated(knob: Int, right: Boolean) {
        when (knob) {
            1 ->
                if (currentEncoder?.perChannel == true) {
                    if (right) {
                        nextChannelPressed()
                    } else {
                        previousChannelPressed()
                    }
                }
            2 ->
                if (currentEncoder == EEncoder.INST) {
                    if (right) {
                        if (currentInstTab + 1 < INST_TABS.size) {
                            selectTab(INST_TABS[++currentInstTab])
                        }
                    } else {
                        if (currentInstTab > 0) {
                            selectTab(INST_TABS[--currentInstTab])
                        }
                    }
                }
        }
    }

    private fun selectOutput(output: Int) {
        currentOutput = output
        selectCurrentOutput()
    }

    private fun selectCurrentOutput() {
        when (currentOutput) {
            OUTPUT_MAINLR ->
                when (currentChannel) {
                    in CHANNEL_BUS1..CHANNEL_BUS6 ->
                        selectBus(currentChannel - CHANNEL_BUS1 + 1)
                    in CHANNEL_FX1..CHANNEL_FX4 ->
                        selectFx(currentChannel - CHANNEL_FX1 + 1)
                    else ->
                        click(MAIN_LR_X, MAIN_LR_Y)
                }
            in OUTPUT_FX1..OUTPUT_FX4 ->
                selectFx(currentOutput - OUTPUT_FX1 + 1)
            in OUTPUT_BUS1..OUTPUT_BUS6 ->
                selectBus(currentOutput - OUTPUT_BUS1 + 1)
        }
    }

    private fun selectFx(fx: Int) =
        click(FX_X, FX1_Y + (fx - 1) * FX_OFFSET_Y)

    private fun selectBus(bus: Int) {
        when (bus) {
            1 -> click(BUS_X1, BUS_Y1)
            2 -> click(BUS_X2, BUS_Y1)
            3 -> click(BUS_X1, BUS_Y2)
            4 -> click(BUS_X2, BUS_Y2)
            5 -> click(BUS_X1, BUS_Y3)
            6 -> click(BUS_X2, BUS_Y3)
        }
    }

    private fun selectEncoder(encoder: EEncoder) {
        // encoder selection is remembered when toggling a bus, so only toggle when main output is selected
        if (currentOutput == 0 && currentEncoder == encoder) {
            // toggle off
            currentEncoder = null
        } else {
            selectOutput(OUTPUT_MAINLR)
            currentEncoder = encoder
        }
        selectCurrentEncoder()
    }

    private fun selectCurrentEncoder() {
        selectTab(
            when (currentEncoder) {
                EEncoder.SEND -> ETab.SENDS
                EEncoder.PLUGIN -> ETab.SENDS
                EEncoder.EQ -> ETab.EQ
                EEncoder.INST -> INST_TABS[currentInstTab]
                else -> ETab.MIXER
            }
        )
    }

    private fun selectTab(tab: ETab) {
        currentTab = tab
        when (tab) {
            ETab.MIXER -> click(TAB_MIXER_X, TAB_Y)
            ETab.CHANNEL -> click(TAB_CHANNEL_X, TAB_Y)
            ETab.INPUT -> click(TAB_INPUT_X, TAB_Y)
            ETab.GATE -> click(TAB_GATE_X, TAB_Y)
            ETab.EQ -> click(TAB_EQ_X, TAB_Y)
            ETab.COMP -> click(TAB_COMP_X, TAB_Y)
            ETab.SENDS -> click(TAB_SENDS_X, TAB_Y)
            ETab.MAIN -> click(TAB_MAIN_X, TAB_Y)
            ETab.FX -> click(TAB_FX_X, TAB_Y)
            ETab.METER -> click(TAB_METER_X, TAB_Y)
        }
    }

    private fun selectCurrentChannel() {
        selectCurrentOutput()
        when (currentChannel) {
            in 1..CHANNEL_COUNT ->
                click(CHANNEL1_X + (currentChannel - 1) * CHANNEL_OFFSET_X, CHANNEL_Y)
            CHANNEL_AUX ->
                click(CHANNEL1_X + CHANNEL_COUNT * CHANNEL_OFFSET_X, CHANNEL_Y)
            in CHANNEL_RTN1..CHANNEL_RTN4 ->
                click(CHANNEL1_X + (currentChannel - CHANNEL_RTN1 + 1 + CHANNEL_COUNT) * CHANNEL_OFFSET_X, CHANNEL_Y)
            in CHANNEL_BUS1..CHANNEL_BUS6 ->
                selectMainFader()
            in CHANNEL_FX1..CHANNEL_FX4 ->
                selectMainFader()
            CHANNEL_MAIN ->
                selectMainFader()
        }
    }

    private fun selectMainFader() =
        click(MAIN_FADER_X, MAIN_FADER_Y)

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