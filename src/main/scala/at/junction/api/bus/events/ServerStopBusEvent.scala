package at.junction.api.bus.events

import at.junction.api.bus.{SimpleEventMarshaller, BusEventObject, BusEvent}
import org.bukkit.event.HandlerList

case class ServerStopBusEvent(server: String, nanotime: Long) extends BusEvent {
  override def getHandlers: HandlerList = ServerStopBusEvent.getHandlerList
  override def eventIdentifier: String = ServerStopBusEvent.eventIdentifier
}

object ServerStopBusEvent extends BusEventObject {
  override def eventIdentifier: String = "server_stop"

  def apply()(implicit serverId: String): ServerStopBusEvent = {
    new ServerStopBusEvent(serverId, System.nanoTime())
  }
}

object ServerStopBusEventMarshaller extends SimpleEventMarshaller[ServerStopBusEvent](ServerStopBusEvent.eventIdentifier) {}