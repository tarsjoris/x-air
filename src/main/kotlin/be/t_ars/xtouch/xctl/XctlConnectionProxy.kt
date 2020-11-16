package be.t_ars.xtouch.xctl

import be.t_ars.xtouch.util.Listeners
import java.net.InetAddress

class XctlConnectionProxy(xr18Address: InetAddress) : XctlConnectionStub() {
	private val connectionWithXR18 =
		ConnectionWithXR18Impl(xr18Address, this::xr18HeartbeatReceived, this::routeEventFromXR18)

	private val xr18EventProcessors = Listeners<EventProcessor<IXR18Events>>()

	private var xr18Connected = false
	private var lastXR18Heartbeat: Long = 0

	fun addXR18EventProcessor(processor: EventProcessor<IXR18Events>) {
		xr18EventProcessors.add(processor)
	}

	fun getConnectionToXR18(): IConnectionToXR18 {
		return connectionWithXR18.getConnectionToXR18()
	}

	override fun run() {
		startMonitorConnection()
		Thread(connectionWithXR18::run).start()
		connectionWithXTouch.run()
	}

	override fun stop() {
		super.stop()
		connectionWithXR18.stop()
	}

	override fun checkConnection() {
		synchronized(connectionLock) {
			if (xr18Connected && System.currentTimeMillis() - lastXR18Heartbeat > HEARTBEAT_TIMEOUT) {
				println("XR18 disconnected")
				broadcastIfWillDisconnect()
				xr18Connected = false
			}
		}
	}

	private fun routeEventFromXR18(event: Event<IXR18Events>) {
		xr18EventProcessors.broadcast { it(event) }
	}

	override fun routeHeartbeatFromXTouch() {
		getConnectionToXR18().sendHeartbeat()
	}

	private fun xr18HeartbeatReceived() {
		routeHeartbeatFromXR18()
		synchronized(connectionLock) {
			lastXR18Heartbeat = System.currentTimeMillis()
			if (!xr18Connected) {
				xr18Connected = true
				println("XR18 connected")
				broadcastIfConnected()
			}
		}
	}

	private fun routeHeartbeatFromXR18() {
		getConnectionToXTouch().sendHeartbeat()
	}

	override fun isConnected() =
		super.isConnected() && xr18Connected
}