package be.t_ars.xtouch.session

interface IXTouchSessionListener {
	fun selectionChanged(output: Int, channel: Int, encoder: XTouchSessionState.EEncoder?, dynamicEncoder: XTouchSessionState.EDynamicEncoder) {}
	fun effectsSettingsChanged(effectsSettings: Int?) {}
}