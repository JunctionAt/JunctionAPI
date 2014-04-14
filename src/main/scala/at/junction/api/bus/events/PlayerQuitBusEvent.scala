package at.junction.api.bus.events

import at.junction.api.bus.{BusEvents, EventMarshaller, BusEventObject, BusEvent}
import org.bukkit.event.HandlerList
import org.json4s._
import java.util.UUID
import org.bukkit.entity.Player
import at.junction.api.bus.fields.PlayerState

case class PlayerQuitBusEvent(server: String, player: PlayerState) extends BusEvent {
  override def getHandlers: HandlerList = PlayerQuitBusEvent.getHandlerList
  override def eventIdentifier: String = PlayerQuitBusEvent.eventIdentifier
}

object PlayerQuitBusEvent extends BusEventObject {
  override def eventIdentifier: String = "player_quit"

  def apply(player: Player)(implicit serverId: String): PlayerQuitBusEvent = {
    new PlayerQuitBusEvent(serverId, PlayerState(player))
  }
}

object PlayerQuitBusEventMarshaller extends EventMarshaller {

  def serialize(input: BusEvent): JValue = {
    Extraction.decompose(input.asInstanceOf[PlayerQuitBusEvent])
  }

  def deserialize(input: JValue): BusEvent = {
    input.extract[PlayerQuitBusEvent]
  }

  def register() = BusEvents.types += PlayerQuitBusEvent.eventIdentifier -> PlayerQuitBusEventMarshaller
}