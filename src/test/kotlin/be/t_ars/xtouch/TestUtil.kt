package be.t_ars.xtouch

import be.t_ars.xtouch.xairedit.IXAirEditInteractor
import be.t_ars.xtouch.xairedit.XAirEditController
import be.t_ars.xtouch.xctl.IXTouchListener
import org.junit.jupiter.api.Assertions

fun performTest(
	expectedChannel: Int,
	expectedOutput: Int,
	whenPart: (IXTouchListener) -> Unit
) {
	performTest(expectedChannel, expectedOutput, IXAirEditInteractor.ETab.MIXER, null, whenPart)
}

fun performTest(
	expectedChannel: Int,
	expectedOutput: Int,
	expectedTab: IXAirEditInteractor.ETab,
	whenPart: (IXTouchListener) -> Unit
) {
	performTest(expectedChannel, expectedOutput, expectedTab, null, whenPart)
}

fun performTest(
	expectedChannel: Int,
	expectedOutput: Int,
	expectedTab: IXAirEditInteractor.ETab,
	expectedEffectsSettingsDialog: Int?,
	whenPart: (IXTouchListener) -> Unit
) {
	val mock = XAirEditInteractorMock()
	val controller = XAirEditController(mock)
	val session = XTouchSession()
	session.addListener(controller)

	whenPart.invoke(session)

	Assertions.assertEquals(expectedChannel, mock.currentChannel, "Unexpected channel")
	Assertions.assertEquals(expectedOutput, mock.currentOutput, "Unexpected output")
	Assertions.assertEquals(expectedTab, mock.currenTab, "Unexpected tab")
	Assertions.assertEquals(
		expectedEffectsSettingsDialog,
		mock.currentEffectsSettingsDialog,
		"Unexpected effects settings dialog"
	)
}