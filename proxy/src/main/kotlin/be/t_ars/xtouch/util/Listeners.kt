package be.t_ars.xtouch.util

class Listeners<T> {
	private val listeners = mutableListOf<T>()

	fun add(listener: T) {
		listeners.add(listener)
	}

	fun broadcast(eventSender: (T) -> Unit) {
		listeners.forEach(eventSender)
	}
}