package be.t_ars.xtouch

import org.junit.jupiter.api.Test

class SelectBusOutputTest {
	@Test
	fun testSelectBus() {
		for (bus in 1..6) {
			performTest(
				1,
				XAirEditInteractorMock.OUTPUT_BUS1 + bus - 1
			) {
				it.automationPressed(bus)
			}
		}
	}

	@Test
	fun testSelectBusFromFxChannel() {
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.nextBankPressed()
			it.nextBankPressed()
			it.nextBankPressed()
			it.nextBankPressed()
			it.channelSelectPressed(1)
			it.automationPressed(1)
		}
	}

	@Test
	fun testStayOnChannel() {
		performTest(
			5,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.channelSelectPressed(5)
			it.automationPressed(1)
		}
	}

	@Test
	fun testMixerSelectedInsteadOfEncoder() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_BUS1,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderEqPressed()
			it.automationPressed(1)
		}
	}

	@Test
	fun testDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.automationPressed(1)
			it.automationPressed(1)
		}
	}

	@Test
	fun testRememberEncoder() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.EQ
		) {
			it.encoderEqPressed()
			it.automationPressed(1)
			it.automationPressed(1)
		}
	}

	@Test
	fun testSwitchBus() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_BUS2
		) {
			it.automationPressed(1)
			it.automationPressed(2)
		}
	}

	@Test
	fun testSelectBusFromFxOutput() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.modifyPressed(1)
			it.automationPressed(1)
		}
	}

	@Test
	fun testSwitchToMainOutputForUpperBanks() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.automationPressed(1)
			it.nextBankPressed()
			it.nextBankPressed()
			it.nextBankPressed()
		}
	}

	@Test
	fun testResetBankForUpperBanks() {
		performTest(
			2,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.nextBankPressed()
			it.nextBankPressed()
			it.nextBankPressed()
			it.automationPressed(1)
			// state resets to first bank
			it.channelSelectPressed(2)
		}
	}

	@Test
	fun testDoNotChangeChannnelForEncodeWhenOutputIsBus() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.encoderEqPressed()
			it.automationPressed(1)
			it.nextBankPressed()
		}
	}

	@Test
	fun testGoBackToFxOutputAfterDeselect() {
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.nextBankPressed()
			it.nextBankPressed()
			it.nextBankPressed()
			it.nextBankPressed()
			it.channelSelectPressed(1)
			it.automationPressed(1)
			it.automationPressed(1)
		}
	}
}