package com.tjors.xtouch

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicBoolean

private const val PORT = 10111
private val INIT_EVENT = byteArrayOf(0xF0.toByte(), 0x00, 0x20, 0x32, 0x58, 0x54, 0x00, 0xF7.toByte())
private val PING_EVENT = byteArrayOf(0xF0.toByte(), 0x00, 0x00, 0x66, 0x14, 0x00, 0XF7.toByte())

private const val DEBUG = false

class XctlDevice(private val listener: IXctlListener, private val proxyFor: InetAddress?) : AutoCloseable {
    private var xTouchAddress: InetAddress? = null
    private var running = AtomicBoolean(true)
    private val socket = DatagramSocket(PORT)

    constructor(listener: IXctlListener) : this(listener, null)

    init {
        Thread(this::run).start()
        if (proxyFor == null) {
            Thread(this::ping).start()
        }
    }

    override fun close() {
        running.set(false)
    }

    private fun processPacket(packet: DatagramPacket) {
        if (DEBUG) printPacket(packet)
        when (packet.data[packet.offset]) {
            0x90.toByte() ->
                if (packet.data[packet.offset + 2] == 0x7F.toByte()) {
                    when (val note = packet.data[packet.offset + 1]) {
                        in 0x00..0x07 ->
                            listener.channelRec(note - 0x00 + 1)
                        in 0x08..0x0F ->
                            listener.channelSolo(note - 0x08 + 1)
                        in 0x10..0x17 ->
                            listener.channelMute(note - 0x10 + 1)
                        in 0x18..0x1F ->
                            listener.channelSelect(note - 0x18 + 1)
                        0x28.toByte() ->
                            listener.track()
                        0x29.toByte() ->
                            listener.send()
                        0x2A.toByte() ->
                            listener.pan()
                        0x2B.toByte() ->
                            listener.plugin()
                        0x2C.toByte() ->
                            listener.eq()
                        0x2D.toByte() ->
                            listener.inst()
                        0x2E.toByte() ->
                            listener.previousBank()
                        0x2F.toByte() ->
                            listener.nextBank()
                        0x30.toByte() ->
                            listener.previousChannel()
                        0x31.toByte() ->
                            listener.nextChannel()
                        0x32.toByte() ->
                            listener.flip()
                        0x33.toByte() ->
                            listener.globalView()
                        in 0x46..0x49 ->
                            listener.fxSelect(note - 0x46 + 1)
                        in 0x4A..0x4F ->
                            listener.busSelect(note - 0x4A + 1)
                    }
                }
        }
    }

    private fun printPacket(packet: DatagramPacket) {
        for (i in packet.offset until packet.offset + packet.length) {
            val entry = packet.data[i];
            val entryInt = entry.toUByte()
            val entryHex = String.format("%02X", entry)
            print("$entryInt($entryHex) ")
        }
        println()
    }

    private fun run() {
        val buffer = ByteArray(256)
        val packet = DatagramPacket(buffer, buffer.size)
        while (running.get()) {
            socket.receive(packet)
            if (xTouchAddress == null) {
                if (isInitEvent(packet)) {
                    xTouchAddress = packet.address
                    println("XTouch connected from address $xTouchAddress")
                }
            } else {
                if (packet.address == xTouchAddress) {
                    processPacket(packet)
                    if (proxyFor != null && packet.address == xTouchAddress) {
                        packet.address = proxyFor
                        socket.send(packet)
                    }
                } else if (packet.address == proxyFor) {
                    packet.address = xTouchAddress
                    socket.send(packet)
                }
            }
        }
    }

    private fun ping() {
        val packet = DatagramPacket(PING_EVENT, 0, PING_EVENT.size)
        packet.port = PORT
        while (true) {
            if (xTouchAddress != null) {
                packet.address = xTouchAddress
                socket.send(packet)
            }
            Thread.sleep(6_000L)
        }
    }
}

private fun isInitEvent(packet: DatagramPacket): Boolean {
    if (packet.length != INIT_EVENT.size) {
        return false
    }
    for (i in INIT_EVENT.indices) {
        if (packet.data[packet.offset + i] != INIT_EVENT[i]) {
            return false
        }
    }
    return true
}