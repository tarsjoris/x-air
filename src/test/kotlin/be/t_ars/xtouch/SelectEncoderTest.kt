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
			it.encoderTrackPressed( true)
		}
	}
	@Test
	fun testEncoderTrackDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderTrackPressed( true)
			it.encoderTrackPressed( true)
		}
	}

	@Test
	fun testEncoderSend() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.SENDS
		) {
			it.encoderSendPressed( true)
		}
	}

	@Test
	fun testEncoderSendDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderSendPressed( true)
			it.encoderSendPressed( true)
		}
	}

	@Test
	fun testEncoderPan() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderPanPressed( true)
		}
	}

	@Test
	fun testEncoderPanDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderPanPressed( true)
			it.encoderPanPressed( true)
		}
	}

	@Test
	fun testEncoderPlugin() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.SENDS
		) {
			it.encoderPluginPressed( true)
		}
	}

	@Test
	fun testEncoderPluginDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderPluginPressed( true)
			it.encoderPluginPressed( true)
		}
	}

	@Test
	fun testEncoderEq() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.EQ
		) {
			it.encoderEqPressed( true)
		}
	}

	@Test
	fun testEncoderEqDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderEqPressed( true)
			it.encoderEqPressed( true)
		}
	}

	@Test
	fun testEncoderInst() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.GATE
		) {
			it.encoderInstPressed( true)
		}
	}

	@Test
	fun testEncoderInstDeselect() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.MIXER
		) {
			it.encoderInstPressed( true)
			it.encoderInstPressed( true)
		}
	}

	@Test
	fun testEncoderInstNextComp() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.COMP
		) {
			it.encoderInstPressed( true)
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
			it.encoderInstPressed( true)
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
			it.encoderInstPressed( true)
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
			it.encoderInstPressed( true)
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
			it.encoderInstPressed( true)
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
			it.encoderInstPressed( true)
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
			it.encoderInstPressed( true)
			it.knobRotated(2, true)
			it.encoderEqPressed( true)
			it.encoderInstPressed( true)
		}
	}

	@Test
	fun testSwitchEncoder() {
		performTest(
			1,
			XAirEditInteractorMock.OUTPUT_MAINLR,
			IXAirEditInteractor.ETab.EQ
		) {
			it.encoderSendPressed( true)
			it.encoderEqPressed( true)
		}
	}
}