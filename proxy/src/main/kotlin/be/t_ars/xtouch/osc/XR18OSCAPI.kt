package be.t_ars.xtouch.osc

import be.t_ars.xtouch.util.SuspendingListeners
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicBoolean

class XR18OSCAPI(private var host: InetAddress) {
	companion object {
		const val PORT = 10024

		const val CHANNEL_COUNT = 17
		const val AUX_CHANNEL = 17
		const val BUS_COUNT = 6
		const val RETURN_COUNT = 4
		const val FXSEND_COUNT = 4
		private const val DEBUG = false
	}

	private val socket = DatagramSocket()
	private val running = AtomicBoolean(true)
	private val listeners = SuspendingListeners<IOSCListener>()

	fun addListener(listener: IOSCListener) {
		listeners.add(listener)
	}

	fun removeListener(listener: IOSCListener) {
		listeners.remove(listener)
	}

	fun run() {
		startRequestChanges()

		val buffer = ByteArray(256)
		val udpPacket = DatagramPacket(buffer, buffer.size)
		while (running.get()) {
			socket.receive(udpPacket)
			printPacket("IN", udpPacket.data)
			try {
				val packet = parsePacket(udpPacket.data, udpPacket.length)
				processPacket(packet)
			} catch (e: Exception) {
				val data = String(udpPacket.data, 0, udpPacket.length)
				println("Could not process packet '$data': ${e.message}")
				e.printStackTrace()
			}
		}
	}

	fun setHost(host: InetAddress) {
		this.host = host
	}

	fun stop() {
		running.set(false)
	}

	private fun processPacket(packet: IOSCPacket) {
		when (packet) {
			is OSCBundle ->
				packet.packets.forEach { processPacket(it) }
			is OSCMessage ->
				processMessage(packet)
		}
	}

	private fun processMessage(message: OSCMessage) {
		val parts = message.address.split('/')
		when (parts[0]) {
			"" -> {
				when (parts[1]) {
					"ch" -> {
						val channel = (parts[2][0] - '0') * 10 + (parts[2][1] - '0')
						if (channel in 1..CHANNEL_COUNT)
							processChannelMessage(channel, parts, message)
					}
					"rtn" -> {
						when (parts[2]) {
							"aux" -> processChannelMessage(AUX_CHANNEL, parts, message)
							else -> {
								val returnChannel = parts[2][0] - '0'
								if (returnChannel in 1..RETURN_COUNT)
									processReturnMessage(returnChannel, parts, message)
							}
						}
					}
					"lr" -> processLRMessage(parts, message)
					"bus" -> {
						val bus = parts[2][0] - '0'
						if (bus in 1..BUS_COUNT) {
							processBusMessage(bus, parts, message)
						}
					}
					"fxsend" -> {
						val fxSend = parts[2][0] - '0'
						if (fxSend in 1..FXSEND_COUNT) {
							processFXSendMessage(fxSend, parts, message)
						}
					}
					"config" -> processConfigMessage(parts, message)
				}
			}
		}
	}

	private fun processChannelMessage(channel: Int, parts: List<String>, message: OSCMessage) {
		when (parts[3]) {
			"config" -> {
				when (parts[4]) {
					"name" -> {
						val name = message.getString(0)
						if (name != null) {
							listeners.broadcast { it.channelName(channel, name) }
						}
					}
					"color" -> {
						val color = message.getInt(0)
						if (color != null) {
							listeners.broadcast { it.channelColor(channel, color) }
						}
					}
				}
			}
			"mix" -> {
				when (parts[4]) {
					"01",
					"02",
					"03",
					"04",
					"05",
					"06" -> {
						val bus = parts[4].toInt()
						when (parts[5]) {
							"level" -> {
								val level = message.getFloat(0)
								if (level != null) {
									listeners.broadcast { it.channelBusLevel(channel, bus, level) }
								}
							}
						}
					}
				}
			}
		}
	}

	private fun processReturnMessage(returnChannel: Int, parts: List<String>, message: OSCMessage) {
		when (parts[3]) {
			"mix" -> {
				when (parts[4]) {
					"on" -> {
						val on = message.getInt(0) == 1
						listeners.broadcast { it.returnMixOn(returnChannel, on) }
					}
					"fader" -> {
						val level = message.getFloat(0)
						if (level != null) {
							listeners.broadcast { it.returnLevel(returnChannel, level) }
						}
					}
				}
			}
			"config" -> {
				when (parts[4]) {
					"name" -> {
						val name = message.getString(0)
						if (name != null) {
							listeners.broadcast { it.returnName(returnChannel, name) }
						}
					}
					"color" -> {
						val color = message.getInt(0)
						if (color != null) {
							listeners.broadcast { it.returnColor(returnChannel, color) }
						}
					}
				}
			}
		}
	}

	private fun processFXSendMessage(fxSend: Int, parts: List<String>, message: OSCMessage) {
		when (parts[3]) {
			"mix" -> {
				when (parts[4]) {
					"on" -> {
						val on = message.getInt(0) == 1
						listeners.broadcast { it.fxSendMixOn(fxSend, on) }
					}
					"fader" -> {
						val level = message.getFloat(0)
						if (level != null) {
							listeners.broadcast { it.fxSendLevel(fxSend, level) }
						}
					}
				}
			}
			"config" -> {
				when (parts[4]) {
					"name" -> {
						val name = message.getString(0)
						if (name != null) {
							listeners.broadcast { it.fxSendName(fxSend, name) }
						}
					}
					"color" -> {
						val color = message.getInt(0)
						if (color != null) {
							listeners.broadcast { it.fxSendColor(fxSend, color) }
						}
					}
				}
			}
		}
	}

	private fun processLRMessage(parts: List<String>, message: OSCMessage) {
		when (parts[2]) {
			"mix" -> {
				when (parts[3]) {
					"on" -> {
						val on = message.getInt(0) == 1
						listeners.broadcast { it.lrMixOn(on) }
					}
				}
			}
		}
	}

	private fun processBusMessage(bus: Int, parts: List<String>, message: OSCMessage) {
		when (parts[3]) {
			"mix" -> {
				when (parts[4]) {
					"on" -> {
						val on = message.getInt(0) == 1
						listeners.broadcast { it.busMixOn(bus, on) }
					}
					"fader" -> {
						val level = message.getFloat(0)
						if (level != null) {
							listeners.broadcast { it.busLevel(bus, level) }
						}
					}
				}
			}
			"config" -> {
				when (parts[4]) {
					"name" -> {
						val name = message.getString(0)
						if (name != null) {
							listeners.broadcast { it.busName(bus, name) }
						}
					}
					"color" -> {
						val color = message.getInt(0)
						if (color != null) {
							listeners.broadcast { it.busColor(bus, color) }
						}
					}
				}
			}
		}
	}

	private fun processConfigMessage(parts: List<String>, message: OSCMessage) {
		when (parts[2]) {
			"solo" -> {
				when (parts[3]) {
					"source" -> {
						val source = IOSCListener.ESoloSource.getSoloSource(message.getInt(0))
						listeners.broadcast { it.soloSource(source) }
					}
				}
			}
			"buslink" -> {
				val buslink = IOSCListener.EBusLink.getBusLink(parts[3])
				if (buslink != null) {
					val on = message.getInt(0) == 1
					listeners.broadcast { it.busLink(buslink, on) }
				}
			}
		}
	}

	private fun startRequestChanges() {
		GlobalScope.launch {
			while (running.get()) {
				try {
					send("/xremote")
				} catch (e: IOException) {
					// retry later
				}
				delay(7500)
			}
		}
	}

	fun requestBusesMixOn() {
		for (i in 1..BUS_COUNT) {
			requestBusMixOn(i)
		}
	}

	fun requestBusMixOn(bus: Int) {
		validateBus(bus)
		send("/bus/$bus/mix/on")
	}

	fun setBusMixOn(bus: Int, on: Boolean) {
		validateBus(bus)
		send("/bus/$bus/mix/on", OSCArgInt(if (on) 1 else 0))
	}

	fun requestLRMixOn() =
		send("/lr/mix/on")

	fun setLRMixOn(on: Boolean) =
		send("/lr/mix/on", OSCArgInt(if (on) 1 else 0))

	fun requestConfigs() {
		for (i in 1..CHANNEL_COUNT) {
			requestChannelName(i)
			requestChannelColor(i)
		}
		for (i in 1..BUS_COUNT) {
			requestBusName(i)
			requestBusColor(i)
		}
		for (i in 1..FXSEND_COUNT) {
			requestFXSendName(i)
			requestFXSendColor(i)
		}
	}

	fun requestChannelName(channel: Int) {
		validateChannel(channel)
		val channelPrefix = getChannelPrefix(channel)
		requestName(channelPrefix)
	}

	fun requestChannelColor(channel: Int) {
		validateChannel(channel)
		val channelPrefix = getChannelPrefix(channel)
		requestColor(channelPrefix)
	}

	fun requestBusName(bus: Int) {
		validateBus(bus)
		requestName("/bus/$bus")
	}

	fun requestBusColor(bus: Int) {
		validateBus(bus)
		requestColor("/bus/$bus")
	}

	fun requestReturnName(returnChannel: Int) {
		validateReturn(returnChannel)
		requestName("/rtn/$returnChannel")
	}

	fun requestReturnColor(returnChannel: Int) {
		validateReturn(returnChannel)
		requestColor("/bus/$returnChannel")
	}

	fun requestFXSendName(fxSend: Int) {
		validateFXSend(fxSend)
		requestName("/fxsend/$fxSend")
	}

	fun requestFXSendColor(fxSend: Int) {
		validateFXSend(fxSend)
		requestColor("/fxsend/$fxSend")
	}

	private fun requestName(prefix: String) =
		send("$prefix/config/name")

	private fun requestColor(prefix: String) =
		send("$prefix/config/color")

	fun requestSoloSource() {
		send("/config/solo/source")
	}

	fun setSoloSource(source: IOSCListener.ESoloSource) {
		send("/config/solo/source", OSCArgInt(source.id))
	}

	fun requestBusLink(busLink: IOSCListener.EBusLink) {
		send("/config/buslink/${busLink.id}")
	}

	fun requestBusLevel(bus: Int) {
		send("/bus/$bus/mix/fader")
	}

	fun requestChannelBusLevel(channel: Int, bus: Int) {
		send(getChannelPrefix(channel) + "/mix/" + pad(bus) + "/level")
	}

	fun setChannelBusLevel(channel: Int, bus: Int, level: Float) {
		send(getChannelPrefix(channel) + "/mix/" + pad(bus) + "/level", OSCArgFloat(level))
	}

	private fun getChannelPrefix(channel: Int) =
		when (channel) {
			AUX_CHANNEL -> "/rtn/aux"
			else -> "/ch/" + pad(channel)
		}

	private fun send(address: String, vararg args: IOSCArg) =
		send(OSCMessage(address, args))

	private fun send(packet: IOSCPacket) {
		val payload = packet.serialize()
		printPacket("OUT->$host", payload)
		socket.send(DatagramPacket(payload, payload.size, host, PORT))
	}

	private fun validateChannel(channel: Int) =
		assert(channel in 1..CHANNEL_COUNT)

	private fun validateBus(bus: Int) =
		assert(bus in 1..BUS_COUNT)

	private fun validateReturn(returnChannel: Int) =
		assert(returnChannel in 1..RETURN_COUNT)

	private fun validateFXSend(fxSend: Int) =
		assert(fxSend in 1..FXSEND_COUNT)

	private fun pad(number: Int) =
		number.toString().padStart(2, '0')

	private fun printPacket(prefix: String, data: ByteArray) {
		if (DEBUG) {
			println("[$prefix] ${String(data)}")
			//println(data.joinToString(separator = ",", transform = { it.toString() }))
		}
	}
}