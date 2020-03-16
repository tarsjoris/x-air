package be.t_ars.xtouch

interface IXTouchSessionListener {
	fun selectionChanged(output: Int, channel: Int, encoder: XTouchSession.EEncoder?, dynamicEncoder: XTouchSession.EDynamicEncoder) {}
	fun effectsSettingsChanged(effectsSettings: Int?) {}
}