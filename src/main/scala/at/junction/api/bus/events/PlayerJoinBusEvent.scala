package at.junction.api.bus.events

import at.junction.api.bus.{EventMarshaller, BusEvents, BusEventObject, BusEvent}
import org.bukkit.event.HandlerList
import org.json4s._
import java.util.UUID
import at.junction.api.bus.fields.PlayerState
import org.bukkit.entity.Player

case class PlayerJoinBusEvent(server: String, player: PlayerState) extends BusEvent {
  override def getHandlers: HandlerList = PlayerJoinBusEvent.getHandlerList
  override def eventIdentifier: String = PlayerJoinBusEvent.eventIdentifier
}

object PlayerJoinBusEvent extends BusEventObject {
  override def eventIdentifier: String = "player_join"

  def apply(player: Player)(implicit serverId: String): PlayerJoinBusEvent = {
    new PlayerJoinBusEvent(serverId, PlayerState(player))
  }
}

object PlayerJoinBusEventMarshaller extends EventMarshaller {

  def serialize(input: BusEvent): JValue = {
    Extraction.decompose(input.asInstanceOf[PlayerJoinBusEvent])
  }

  def deserialize(input: JValue): BusEvent = {
    input.extract[PlayerJoinBusEvent]
  }

  def register() = BusEvents.types += PlayerJoinBusEvent.eventIdentifier -> PlayerJoinBusEventMarshaller
}