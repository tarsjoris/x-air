package be.t_ars.xtouch

import be.t_ars.xtouch.xairedit.IXAirEditInteractor
import org.junit.jupiter.api.Test

class EffectsSettingsTest {
	@Test
	fun testOpenSettings() {
		for (settings in 1..4) {
			performTest(
				1,
				XAirEditInteractorMock.OUTPUT_MAINLR,
				IXAirEditInteractor.ETab.MIXER,
				settings
			) {
				it.functionPressed(settings, true)
			}
		}
	}

	@Test
	fun testCloseSettings() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER,
			null
		) {
			it.functionPressed(1, true)
			it.functionPressed(1, true)
		}
	}

	@Test
	fun testSwitchSettings() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER,
			2
		) {
			it.functionPressed(1, true)
			it.functionPressed(2, true)
		}
	}

	@Test
	fun testResetEncoder() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER,
			null
		) {
			it.encoderEqPressed(true)
			it.functionPressed(1, true)
			it.functionPressed(1, true)
		}
	}

	@Test
	fun testRememberInstEncoder() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.COMP,
			null
		) {
			it.encoderInstPressed(true)
			it.knobRotated(2, true)
			it.functionPressed(1, true)
			it.functionPressed(1, true)
			it.encoderInstPressed(true)
		}
	}
}