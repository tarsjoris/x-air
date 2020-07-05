package be.t_ars.xtouch

import be.t_ars.xtouch.xairedit.IXAirEditInteractor
import org.junit.jupiter.api.Test

class SelectEncoderTest {
	@Test
	fun testEncoderTrack() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderTrackPressed()
		}
	}
	@Test
	fun testEncoderTrackDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderTrackPressed()
			it.encoderTrackPressed()
		}
	}

	@Test
	fun testEncoderSend() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.SENDS
		) {
			it.encoderSendPressed()
		}
	}

	@Test
	fun testEncoderSendDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderSendPressed()
			it.encoderSendPressed()
		}
	}

	@Test
	fun testEncoderPan() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderPanPressed()
		}
	}

	@Test
	fun testEncoderPanDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderPanPressed()
			it.encoderPanPressed()
		}
	}

	@Test
	fun testEncoderPlugin() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.SENDS
		) {
			it.encoderPluginPressed()
		}
	}

	@Test
	fun testEncoderPluginDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderPluginPressed()
			it.encoderPluginPressed()
		}
	}

	@Test
	fun testEncoderEq() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.EQ
		) {
			it.encoderEqPressed()
		}
	}

	@Test
	fun testEncoderEqDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderEqPressed()
			it.encoderEqPressed()
		}
	}

	@Test
	fun testEncoderInst() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.GATE
		) {
			it.encoderInstPressed()
		}
	}

	@Test
	fun testEncoderInstDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderInstPressed()
			it.encoderInstPressed()
		}
	}

	@Test
	fun testEncoderInstNextComp() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.COMP
		) {
			it.encoderInstPressed()
			it.knobRotated(2, true)
		}
	}

	@Test
	fun testEncoderInstNextAutomix() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MAIN
		) {
			it.encoderInstPressed()
			it.knobRotated(2, true)
			it.knobRotated(2, true)
		}
	}

	@Test
	fun testEncoderInstNextLast() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MAIN
		) {
			it.encoderInstPressed()
			it.knobRotated(2, true)
			it.knobRotated(2, true)
			it.knobRotated(2, true)
		}
	}

	@Test
	fun testEncoderInstPreviousFirst() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.GATE
		) {
			it.encoderInstPressed()
			it.knobRotated(2, false)
		}
	}

	@Test
	fun testEncoderInstPreviousGate() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.GATE
		) {
			it.encoderInstPressed()
			it.knobRotated(2, true)
			it.knobRotated(2, false)
		}
	}

	@Test
	fun testEncoderInstPreviousComp() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.COMP
		) {
			it.encoderInstPressed()
			it.knobRotated(2, true)
			it.knobRotated(2, true)
			it.knobRotated(2, false)
		}
	}

	@Test
	fun testEncoderRememberInst() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.COMP
		) {
			it.encoderInstPressed()
			it.knobRotated(2, true)
			it.encoderEqPressed()
			it.encoderInstPressed()
		}
	}

	@Test
	fun testSwitchEncoder() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.EQ
		) {
			it.encoderSendPressed()
			it.encoderEqPressed()
		}
	}
}