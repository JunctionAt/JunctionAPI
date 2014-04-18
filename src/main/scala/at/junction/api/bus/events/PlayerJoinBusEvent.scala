package at.junction.api.bus.events

import at.junction.api.bus._
import org.bukkit.event.HandlerList
import at.junction.api.fields.PlayerState
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

object PlayerJoinBusEventMarshaller extends SimpleEventMarshaller[PlayerJoinBusEvent](PlayerJoinBusEvent.eventIdentifier) {}