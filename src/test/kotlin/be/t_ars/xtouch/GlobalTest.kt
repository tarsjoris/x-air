package be.t_ars.xtouch

import be.t_ars.xtouch.xairedit.IXAirEditInteractor
import org.junit.jupiter.api.Test

class GlobalTest {
	@Test
	fun testInitialState() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER,
			null
		) {
		}
	}
}