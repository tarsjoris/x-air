package be.t_ars.xtouch.osc

interface IOSCListener {
	enum class ESoloSource(val id: Int) {
		OFF(0),
		LR(1),
		LRPFL(2),
		LRAFL(3),
		AUX(4),
		U1718(5),
		BUS1(6),
		BUS2(7),
		BUS3(8),
		BUS4(9),
		BUS5(10),
		BUS6(11),
		BUS12(12),
		BUS34(13),
		BUS56(14);

		companion object {
			fun getSoloSource(id: Int?): ESoloSource {
				for (v in values()) {
					if (v.id == id) {
						return v
					}
				}
				return OFF
			}
		}
	}

	enum class EBusLink(val id: String) {
		BUS12("1-2"),
		BUS34("3-4"),
		BUS56("5-6");

		companion object {
			fun getBusLink(id: String?): EBusLink? {
				for (v in values()) {
					if (v.id == id) {
						return v
					}
				}
				return null
			}
		}
	}

	suspend fun lrMixOn(on: Boolean) {}
	suspend fun busMixOn(bus: Int, on: Boolean) {}
	suspend fun channelName(channel: Int, name: String) {}
	suspend fun channelColor(channel: Int, color: Int) {}
	suspend fun channelBusLevel(channel: Int, bus: Int, level: Float) {}
	suspend fun busName(bus: Int, name: String) {}
	suspend fun busColor(bus: Int, color: Int) {}
	suspend fun busLevel(bus: Int, level: Float) {}
	suspend fun soloSource(source: ESoloSource) {}
	suspend fun busLink(busLink: EBusLink, on: Boolean) {}
}