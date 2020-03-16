package be.t_ars.xtouch

import org.junit.jupiter.api.Test

class SelectFxOutputTest {
	@Test
	fun testSelectFx() {
		for (fx in 1..4) {
			performTest(
				1,
				XAirEditInteractorMock.OUTPUT_FX1 + fx - 1
			) {
				it.modifyPressed(fx)
			}
		}
	}

	@Test
	fun testSelectFxFromBusChannel() {
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.nextBankPressed()
			it.nextBankPressed()
			it.nextBankPressed()
			it.channelSelectPressed(1)
			it.modifyPressed(1)
		}
	}

	@Test
	fun testStayOnChannel() {
		performTest(
			5,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.channelSelectPressed(5)
			it.modifyPressed(1)
		}
	}

	@Test
	fun testMixerSelectedInsteadOfEncoder() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_FX1,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderEqPressed()
			it.modifyPressed(1)
		}
	}

	@Test
	fun testDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.modifyPressed(1)
			it.modifyPressed(1)
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
			it.modifyPressed(1)
			it.modifyPressed(1)
		}
	}

	@Test
	fun testSwitchFx() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_FX2
		) {
			it.modifyPressed(1)
			it.modifyPressed(2)
		}
	}

	@Test
	fun testSelectFxFromBus() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.automationPressed(1)
			it.modifyPressed(1)
		}
	}

	@Test
	fun testSwitchToMainOutputForUpperBanks() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR
		) {
			it.modifyPressed(1)
			it.nextBankPressed()
			it.nextBankPressed()
			it.nextBankPressed()
		}
	}

	@Test
	fun testResetBankForUpperBanks() {
		performTest(
			2,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.nextBankPressed()
			it.nextBankPressed()
			it.nextBankPressed()
			it.modifyPressed(1)
			// state resets to first bank
			it.channelSelectPressed(2)
		}
	}

	@Test
	fun testDoNotChangeChannnelForEncodeWhenOutputIsFx() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_FX1
		) {
			it.encoderEqPressed()
			it.modifyPressed(1)
			it.nextBankPressed()
		}
	}

	@Test
	fun testGoBackToBusOutputAfterDeselect() {
		performTest(
			XAirEditInteractorMock.CHANNEL_MAIN,
			XAirEditInteractorMock.OUTPUT_BUS1
		) {
			it.nextBankPressed()
			it.nextBankPressed()
			it.nextBankPressed()
			it.channelSelectPressed(1)
			it.modifyPressed(1)
			it.modifyPressed(1)
		}
	}
}