package be.t_ars.xtouch

import be.t_ars.xtouch.util.matchesData
import org.junit.jupiter.api.Assertions

abstract class AbstractParseSerializeTest {
	private var payload: ByteArray? = null

	protected fun verify(data: ByteArray) {
		payload = null
		doAction(data)
		payload?.also {
			Assertions.assertTrue(matchesData(it, data))
		} ?: Assertions.fail()
	}

	protected abstract fun doAction(data: ByteArray)

	protected fun receivePayload(data: ByteArray) {
		payload = data
	}
}