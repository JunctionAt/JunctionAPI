package at.junction.api.bus.events

import at.junction.api.bus._
import org.bukkit.event.HandlerList
import org.bukkit.entity.Player
import at.junction.api.fields.PlayerState

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

object PlayerQuitBusEventMarshaller extends SimpleEventMarshaller[PlayerQuitBusEvent](PlayerQuitBusEvent.eventIdentifier) {}