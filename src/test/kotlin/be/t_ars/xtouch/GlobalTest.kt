package be.t_ars.xtouch

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