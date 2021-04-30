package be.t_ars.xtouch

import be.t_ars.xtouch.xairedit.IXAirEditInteractor
import org.junit.jupiter.api.Test

class SelectBusOutputTest {
	@Test
	fun testSelectBus() {
		for (bus in 1..6) {
			performTest(
				1,
				XAirEditInteractorMock.OUTPUT_BUS1 + bus - 1
			) {
				it.automationPressed(bus, true)
			}
		}
	}

	@Test
	fun testSelectBusFromFxChannel() {
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(1, true)
			it.automationPressed(1, true)
		}
	}

	@Test
	fun testStayOnChannel() {
		performTest(
			5,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.channelSelectPressed(5, true)
			it.automationPressed(1, true)
		}
	}

	@Test
	fun testMixerSelectedInsteadOfEncoder() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_BUS1,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderEqPressed(true)
			it.automationPressed(1, true)
		}
	}

	@Test
	fun testDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.automationPressed(1, true)
			it.automationPressed(1, true)
		}
	}

	@Test
	fun testRememberEncoder() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.EQ
		) {
			it.encoderEqPressed(true)
			it.automationPressed(1, true)
			it.automationPressed(1, true)
		}
	}

	@Test
	fun testSwitchBus() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_BUS2
		) {
			it.automationPressed(1, true)
			it.automationPressed(2, true)
		}
	}

	@Test
	fun testSelectBusFromFxOutput() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.modifyPressed(1, true)
			it.automationPressed(1, true)
		}
	}

	@Test
	fun testSwitchToMainOutputForUpperBanks() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.automationPressed(1, true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
		}
	}

	@Test
	fun testResetBankForUpperBanks() {
		performTest(
			2,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.automationPressed(1, true)
			// state resets to first bank
			it.channelSelectPressed(2, true)
		}
	}

	@Test
	fun testDoNotChangeChannnelForEncodeWhenOutputIsBus() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.encoderEqPressed(true)
			it.automationPressed(1, true)
			it.nextBankPressed(true)
		}
	}

	@Test
	fun testGoBackToFxOutputAfterDeselect() {
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(1, true)
			it.automationPressed(1, true)
			it.automationPressed(1, true)
		}
	}
}