package com.tjors.xtouch

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address

fun main() {
    val port = 10111
    val xtouch = "192.168.0.3"
    val xtouchAddress = Inet4Address.getByName(xtouch)
    val xair = "192.168.0.238"
    val xairAddress = Inet4Address.getByName(xair)

    val socket = DatagramSocket(port)
    val pingThread = Thread {
        val pingData = byteArrayOf(0xF0.toByte(), 0x00, 0x00, 0x66, 0x14, 0x00, 0XF7.toByte())
        val outPacket = DatagramPacket(pingData, 0, pingData.size, xtouchAddress, port)
        do {
            println("ping")
            socket.send(outPacket)
            Thread.sleep(6_000L)
        } while (true)
    }
    //pingThread.start()
    try {
        val buffer = ByteArray(256)
        do {
            val inPacket = DatagramPacket(buffer, buffer.size)
            socket.receive(inPacket)
            print("${inPacket.address}/${inPacket.port} ")
            printData(inPacket)

            val outPacket = if (xair == inPacket.address.hostAddress) {
                DatagramPacket(inPacket.data, inPacket.offset, inPacket.length, xtouchAddress, port)
            } else {
                DatagramPacket(inPacket.data, inPacket.offset, inPacket.length, xairAddress, port)
            }
            socket.send(outPacket)
        } while (true)
    }
    finally {
        socket.close()
    }
}

fun printData(packet: DatagramPacket) {
    for (i in  0 until packet.length) {
        val entry = packet.data[packet.offset + i];
        val entryInt = entry.toUByte()
        val entryHex = String.format("%02X", entry)
        print("$entryInt($entryHex) ")
    }
    println()
}