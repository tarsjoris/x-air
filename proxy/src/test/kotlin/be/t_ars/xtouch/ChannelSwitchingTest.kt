package be.t_ars.xtouch

import org.junit.jupiter.api.Test

class ChannelSwitchingTest {
	@Test
	fun testSelectChannelBank1() {
		for (channel in 1..8) {
			performTest(
				channel,
				XAirEditInteractorMock.OUTPUT_MAINLR
			) {
				it.channelSelectPressed(channel, true)
			}
		}
	}

	@Test
	fun testSelectChannelBank2() {
		for (channel in 1..8) {
			performTest(
				channel + 8,
				XAirEditInteractorMock.OUTPUT_MAINLR
			) {
				it.nextBankPressed(true)
				it.channelSelectPressed(channel, true)
			}
		}
	}

	@Test
	fun testSelectChannelAux() {
		performTest(
			XAirEditInteractorMock.CHANNEL_AUX,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(1, true)
		}
	}

	@Test
	fun testSelectChannelRtn() {
		for (rtn in 1..4) {
			performTest(
				XAirEditInteractorMock.CHANNEL_RTN1 + rtn - 1,
				XAirEditInteractorMock.OUTPUT_MAINLR
			) {
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.channelSelectPressed(4 + rtn, true)
			}
		}
	}

	@Test
	fun testSelectChannelBus() {
		for (bus in 1..6) {
			performTest(
				XAirEditInteractorMock.CHANNEL_MAIN,
				XAirEditInteractorMock.OUTPUT_BUS1 + bus - 1
			) {
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.channelSelectPressed(bus, true)
			}
		}
	}

	@Test
	fun testSelectChannelMain() {
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(8, true)
		}
	}

	@Test
	fun testSelectChannelFx() {
		for (fx in 1..4) {
			performTest(
				XAirEditInteractorMock.CHANNEL_MAIN,
				XAirEditInteractorMock.OUTPUT_FX1 + fx - 1
			) {
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.channelSelectPressed(fx, true)
			}
		}
	}

	@Test
	fun testNextChannelBank1() {
		for (channel in 1..8) {
			performTest(
				channel + 1,
				XAirEditInteractorMock.OUTPUT_MAINLR
			) {
				it.channelSelectPressed(channel, true)
				it.nextChannelPressed(true)
			}
		}
	}

	@Test
	fun testNextChannelBank2() {
		for (channel in 1..7) {
			performTest(
				channel + 8 + 1,
				XAirEditInteractorMock.OUTPUT_MAINLR
			) {
				it.nextBankPressed(true)
				it.channelSelectPressed(channel, true)
				it.nextChannelPressed(true)
			}
		}
		// from channel16 to aux
		performTest(
			XAirEditInteractorMock.CHANNEL_AUX,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.nextBankPressed(true)
			it.channelSelectPressed(8, true)
			it.nextChannelPressed(true)
		}
	}

	@Test
	fun testNextChannelAux() {
		// from aux to rtn1
		performTest(
			XAirEditInteractorMock.CHANNEL_RTN1,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(1, true)
			it.nextChannelPressed(true)
		}
	}

	@Test
	fun testNextChannelRtn() {
		for (rtn in 1..3) {
			performTest(
				XAirEditInteractorMock.CHANNEL_RTN1 + rtn - 1 + 1,
				XAirEditInteractorMock.OUTPUT_MAINLR
			) {
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.channelSelectPressed(4 + rtn, true)
				it.nextChannelPressed(true)
			}
		}
		// from rtn4 to bus1
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(8, true)
			it.nextChannelPressed(true)
		}
	}

	@Test
	fun testNextChannelBus() {
		for (bus in 1..5) {
			performTest(
				XAirEditInteractorMock.CHANNEL_MAIN,
				XAirEditInteractorMock.OUTPUT_BUS1 + bus - 1 + 1
			) {
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.channelSelectPressed(bus, true)
				it.nextChannelPressed(true)
			}
		}
		// from bus6 to fx1
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(6, true)
			it.nextChannelPressed(true)
		}
	}

	@Test
	fun testNextChannelFx() {
		for (fx in 1..3) {
			performTest(
				XAirEditInteractorMock.CHANNEL_MAIN,
				XAirEditInteractorMock.OUTPUT_FX1 + fx - 1 + 1
			) {
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.channelSelectPressed(fx, true)
				it.nextChannelPressed(true)
			}
		}
		// from fx4 to main
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(4, true)
			it.nextChannelPressed(true)
		}
	}

	@Test
	fun testNextChannelMain() {
		// no next channel
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(8, true)
			it.nextChannelPressed(true)
		}
	}

	@Test
	fun testPreviousChannelBank1() {
		// no previous channel
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.channelSelectPressed(1, true)
			it.previousChannelPressed(true)
		}
		for (channel in 2..8) {
			performTest(
				channel - 1,
				XAirEditInteractorMock.OUTPUT_MAINLR
			) {
				it.channelSelectPressed(channel, true)
				it.previousChannelPressed(true)
			}
		}
	}

	@Test
	fun testPreviousChannelBank2() {
		for (channel in 1..8) {
			performTest(
				channel + 8 - 1,
				XAirEditInteractorMock.OUTPUT_MAINLR
			) {
				it.nextBankPressed(true)
				it.channelSelectPressed(channel, true)
				it.previousChannelPressed(true)
			}
		}
	}

	@Test
	fun testPreviousChannelAux() {
		// from aux to channel16
		performTest(
			16,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(1, true)
			it.previousChannelPressed(true)
		}
	}

	@Test
	fun testPreviousChannelRtn() {
		// from rtn1 to aux
		performTest(
			XAirEditInteractorMock.CHANNEL_AUX,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(5, true)
			it.previousChannelPressed(true)
		}
		for (rtn in 2..4) {
			performTest(
				XAirEditInteractorMock.CHANNEL_RTN1 + rtn - 1 - 1,
				XAirEditInteractorMock.OUTPUT_MAINLR
			) {
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.channelSelectPressed(4 + rtn, true)
				it.previousChannelPressed(true)
			}
		}
	}

	@Test
	fun testPreviousChannelBus() {
		// from bus1 to rtn4
		performTest(
			XAirEditInteractorMock.CHANNEL_RTN4,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(1, true)
			it.previousChannelPressed(true)
		}
		for (bus in 2..6) {
			performTest(
				XAirEditInteractorMock.CHANNEL_MAIN,
				XAirEditInteractorMock.OUTPUT_BUS1 + bus - 1 - 1
			) {
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.channelSelectPressed(bus, true)
				it.previousChannelPressed(true)
			}
		}
	}

	@Test
	fun testPreviousChannelFx() {
		// from fx1 to bus6
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_BUS6
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(1, true)
			it.previousChannelPressed(true)
		}
		for (fx in 2..4) {
			performTest(
				XAirEditInteractorMock.CHANNEL_MAIN,
				XAirEditInteractorMock.OUTPUT_FX1 + fx - 1 - 1
			) {
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.nextBankPressed(true)
				it.channelSelectPressed(fx, true)
				it.previousChannelPressed(true)
			}
		}
	}

	@Test
	fun testPreviousChannelMain() {
		// from main to fx4
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_FX4
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(8, true)
			it.previousChannelPressed(true)
		}
	}
}