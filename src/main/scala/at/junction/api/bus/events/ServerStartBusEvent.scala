package at.junction.api.bus.events

import at.junction.api.bus.{SimpleEventMarshaller, BusEventObject, BusEvent}
import org.bukkit.event.HandlerList

case class ServerStartBusEvent(server: String, nanotime: Long) extends BusEvent {
  override def getHandlers: HandlerList = ServerStartBusEvent.getHandlerList
  override def eventIdentifier: String = ServerStartBusEvent.eventIdentifier
}

object ServerStartBusEvent extends BusEventObject {
  override def eventIdentifier: String = "server_start"

  def apply()(implicit serverId: String): ServerStartBusEvent = {
    new ServerStartBusEvent(serverId, System.nanoTime())
  }
}

object ServerStartBusEventMarshaller extends SimpleEventMarshaller[ServerStartBusEvent](ServerStartBusEvent.eventIdentifier) {}