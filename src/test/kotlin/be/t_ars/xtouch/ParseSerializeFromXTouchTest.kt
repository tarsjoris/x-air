package be.t_ars.xtouch

import be.t_ars.xtouch.xctl.FromXTouch
import be.t_ars.xtouch.xctl.ToXR18
import org.junit.jupiter.api.Test

class ParseSerializeFromXTouchTest : AbstractParseSerializeTest() {
	private val toXR18 = ToXR18(this::receivePayload)
	private val fromXTouch = FromXTouch { event ->
		event(toXR18)
	}

	@Test
	fun testButtonDown() {
		for (button in 0x00..0x53) {
			verify(byteArrayOf(0x90.toByte(), button.toByte(), 0x7F.toByte()))
		}
	}

	@Test
	fun testButtonUp() {
		for (button in 0x00..0x53) {
			verify(byteArrayOf(0x90.toByte(), button.toByte(), 0x00.toByte()))
		}
	}

	@Test
	fun sniffRegularly() {
		verify(
			byteArrayOf(
				0xF0.toByte(),
				0x00.toByte(), 0x00.toByte(), 0x66.toByte(), 0x58.toByte(), 0x01.toByte(), 0x30.toByte(), 0x31.toByte(),
				0x35.toByte(), 0x36.toByte(), 0x34.toByte(), 0x30.toByte(), 0x35.toByte(), 0x45.toByte(), 0x43.toByte(),
				0x31.toByte(), 0x38.toByte(), 0xF7.toByte()
			)
		)
	}

	@Test
	fun sniffRotateKnob() {
		verify(byteArrayOf(0xB0.toByte(), 0x10.toByte(), 0x01.toByte()))
	}

	@Test
	fun sniffKnobPress() {
		verify(byteArrayOf(0x90.toByte(), 0x20.toByte(), 0x7F.toByte()))
		verify(byteArrayOf(0x90.toByte(), 0x20.toByte(), 0x00.toByte()))
	}

	@Test
	fun sniffFaderMove() {
		verify(byteArrayOf(0x90.toByte(), 0x68.toByte(), 0x7F.toByte()))
		verify(byteArrayOf(0xE0.toByte(), 0x50.toByte(), 0x22.toByte()))
		verify(byteArrayOf(0x90.toByte(), 0x68.toByte(), 0x00.toByte()))
	}

	@Test
	fun sniffMainFaderMove() {
		verify(byteArrayOf(0xE8.toByte(), 0x04.toByte(), 0x01.toByte()))
		verify(byteArrayOf(0x90.toByte(), 0x70.toByte(), 0x7F.toByte()))
		verify(byteArrayOf(0xE8.toByte(), 0x4C.toByte(), 0x18.toByte()))
		verify(byteArrayOf(0x90.toByte(), 0x70.toByte(), 0x00.toByte()))
	}

	@Test
	fun sniffButtonpress() {
		verify(byteArrayOf(0x90.toByte(), 0x19.toByte(), 0x7F.toByte()))
		verify(byteArrayOf(0x90.toByte(), 0x19.toByte(), 0x00.toByte()))
	}

	@Test
	fun sniffSwitchbank() {
		verify(byteArrayOf(0x90.toByte(), 0x2E.toByte(), 0x7F.toByte()))
		verify(byteArrayOf(0x90.toByte(), 0x2E.toByte(), 0x00.toByte()))
	}

	override fun doAction(data: ByteArray) {
		fromXTouch.processPacket(data)
	}
}