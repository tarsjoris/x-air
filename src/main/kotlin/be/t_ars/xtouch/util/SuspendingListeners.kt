package be.t_ars.xtouch.util

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SuspendingListeners<T> {
	private val listeners = mutableListOf<T>()

	fun add(listener: T) {
		synchronized(listeners) {
			listeners.add(listener)
		}
	}

	fun remove(listener: T) {
		synchronized(listeners) {
			listeners.remove(listener)
		}
	}

	fun broadcast(eventSender: suspend (T) -> Unit) {
		synchronized(listeners) {
			for (l in listeners) {
				GlobalScope.launch {
					eventSender(l)
				}
			}
		}
	}
}