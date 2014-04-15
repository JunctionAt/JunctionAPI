package at.junction.api.bus.events

import at.junction.api.bus.{SimpleEventMarshaller, BusEventObject, BusEvent}
import org.bukkit.event.HandlerList

case class ServerHeartbeatBusEvent(server: String, nanotime: Long) extends BusEvent {
  override def getHandlers: HandlerList = ServerHeartbeatBusEvent.getHandlerList
  override def eventIdentifier: String = ServerHeartbeatBusEvent.eventIdentifier
}

object ServerHeartbeatBusEvent extends BusEventObject {
  override def eventIdentifier: String = "server_heartbeat"

  def apply()(implicit serverId: String): ServerHeartbeatBusEvent = {
    new ServerHeartbeatBusEvent(serverId, System.nanoTime())
  }
}

object ServerHeartbeatBusEventMarshaller extends SimpleEventMarshaller[ServerHeartbeatBusEvent](ServerHeartbeatBusEvent.eventIdentifier) {}