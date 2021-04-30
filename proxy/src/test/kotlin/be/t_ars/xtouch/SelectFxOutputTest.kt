package be.t_ars.xtouch

import be.t_ars.xtouch.xairedit.IXAirEditInteractor
import org.junit.jupiter.api.Test

class SelectFxOutputTest {
	@Test
	fun testSelectFx() {
		for (fx in 1..4) {
			performTest(
				1,
				XAirEditInteractorMock.OUTPUT_FX1 + fx - 1
			) {
				it.modifyPressed(fx, true)
			}
		}
	}

	@Test
	fun testSelectFxFromBusChannel() {
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(1, true)
			it.modifyPressed(1, true)
		}
	}

	@Test
	fun testStayOnChannel() {
		performTest(
			5,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.channelSelectPressed(5, true)
			it.modifyPressed(1, true)
		}
	}

	@Test
	fun testMixerSelectedInsteadOfEncoder() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_FX1,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderEqPressed(true)
			it.modifyPressed(1, true)
		}
	}

	@Test
	fun testDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.modifyPressed(1, true)
			it.modifyPressed(1, true)
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
			it.modifyPressed(1, true)
			it.modifyPressed(1, true)
		}
	}

	@Test
	fun testSwitchFx() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_FX2
		) {
			it.modifyPressed(1, true)
			it.modifyPressed(2, true)
		}
	}

	@Test
	fun testSelectFxFromBus() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.automationPressed(1, true)
			it.modifyPressed(1, true)
		}
	}

	@Test
	fun testSwitchToMainOutputForUpperBanks() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.modifyPressed(1, true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
		}
	}

	@Test
	fun testResetBankForUpperBanks() {
		performTest(
			2,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.modifyPressed(1, true)
			// state resets to first bank
			it.channelSelectPressed(2, true)
		}
	}

	@Test
	fun testDoNotChangeChannnelForEncodeWhenOutputIsFx() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.encoderEqPressed(true)
			it.modifyPressed(1, true)
			it.nextBankPressed(true)
		}
	}

	@Test
	fun testGoBackToBusOutputAfterDeselect() {
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.nextBankPressed(true)
			it.channelSelectPressed(1, true)
			it.modifyPressed(1, true)
			it.modifyPressed(1, true)
		}
	}
}