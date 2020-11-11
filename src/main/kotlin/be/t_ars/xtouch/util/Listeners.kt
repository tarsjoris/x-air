package be.t_ars.xtouch.util

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Listeners<T> {
	private val listeners = mutableListOf<T>()

	fun add(listener: T) {
		listeners.add(listener)
	}

	fun broadcast(eventSender: (T) -> Unit) {
		listeners.forEach(eventSender)
	}

	fun broadcastSuspend(eventSender: suspend (T) -> Unit) {
		GlobalScope.launch {
			for (l in listeners) {
				eventSender.invoke(l)
			}
		}
	}
}