package be.t_ars.xtouch.session

interface IXTouchSessionListener {
	suspend fun selectionChanged(output: Int, channel: Int, encoder: XTouchSession.EEncoder?, dynamicEncoder: XTouchSession.EDynamicEncoder) {}
	suspend fun effectsSettingsChanged(effectsSettings: Int?) {}
}