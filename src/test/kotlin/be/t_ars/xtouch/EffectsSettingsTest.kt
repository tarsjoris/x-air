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
				it.functionPressed(settings)
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
			it.functionPressed(1)
			it.functionPressed(1)
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
			it.functionPressed(1)
			it.functionPressed(2)
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
			it.encoderEqPressed()
			it.functionPressed(1)
			it.functionPressed(1)
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
			it.encoderInstPressed()
			it.knobRotated(2, true)
			it.functionPressed(1)
			it.functionPressed(1)
			it.encoderInstPressed()
		}
	}
}