package be.t_ars.xtouch.xctl

import be.t_ars.xtouch.util.Listeners
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

open class XctlConnectionStub {
	protected val connectionWithXTouch =
		ConnectionWithXTouchImpl(this::xTouchHeartbeatReceived, this::routeEventFromXTouch)

	private val xctlConnectionEventProcessors = Listeners<EventProcessor<IXctlConnectionListener>>()
	private val xTouchEventProcessors = Listeners<EventProcessor<IXTouchEvents>>()

	private val running = AtomicBoolean(true)
	protected val connectionLock = Object()
	private var xTouchConnected = false
	private var lastXTouchHeartbeat: Long = 0

	fun addConnectionListener(listener: IXctlConnectionListener) {
		addConnectionEventProcessor { event -> event(listener) }
	}

	fun addConnectionEventProcessor(processor: EventProcessor<IXctlConnectionListener>) {
		xctlConnectionEventProcessors.add(processor)
	}

	fun addXTouchListener(listener: IXTouchEvents) {
		addXTouchEventProcessor { event -> event(listener) }
	}

	fun addXTouchEventProcessor(processor: EventProcessor<IXTouchEvents>) {
		xTouchEventProcessors.add(processor)
	}

	fun getConnectionToXTouch(): IConnectionToXTouch {
		return connectionWithXTouch.getConnectionToXTouch()
	}

	open fun run() {
		startMonitorConnection()
		startScheduleHeartbeat()
		connectionWithXTouch.run()
	}

	protected fun startMonitorConnection() {
		GlobalScope.launch {
			while (running.get()) {
				checkConnection()
				delay(2_000L)
			}
		}
	}

	private fun startScheduleHeartbeat() {
		GlobalScope.launch {
			while (running.get()) {
				connectionWithXTouch.getConnectionToXTouch().sendHeartbeat()
				delay(HEARTBEAT_INTERVAL)
			}
		}
	}

	open fun stop() {
		running.set(false)
		connectionWithXTouch.stop()
	}

	private fun routeEventFromXTouch(event: Event<IXTouchEvents>) {
		xTouchEventProcessors.broadcast { it(event) }
	}

	protected open fun checkConnection() {
		synchronized(connectionLock) {
			if (xTouchConnected && System.currentTimeMillis() - lastXTouchHeartbeat > HEARTBEAT_TIMEOUT) {
				println("XTouch disconnected")
				broadcastIfWillDisconnect()
				xTouchConnected = false
			}
		}
	}

	private fun xTouchHeartbeatReceived() {
		routeHeartbeatFromXTouch()
		synchronized(connectionLock) {
			lastXTouchHeartbeat = System.currentTimeMillis()
			if (!xTouchConnected) {
				xTouchConnected = true
				println("XTouch connected")
				broadcastIfConnected()
			}
		}
	}

	protected open fun routeHeartbeatFromXTouch() {}

	protected open fun isConnected() =
		xTouchConnected

	protected fun broadcastIfWillDisconnect() {
		synchronized(connectionLock) {
			if (isConnected()) {
				xctlConnectionEventProcessors.broadcast { it(IXctlConnectionListener::disconnected) }
			}
		}
	}

	protected fun broadcastIfConnected() {
		synchronized(connectionLock) {
			if (isConnected()) {
				xctlConnectionEventProcessors.broadcast { it(IXctlConnectionListener::connected) }
			}
		}
	}
}