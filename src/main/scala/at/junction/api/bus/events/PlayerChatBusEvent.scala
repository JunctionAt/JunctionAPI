package at.junction.api.bus.events

import at.junction.api.bus.fields.PlayerState
import at.junction.api.bus._
import org.bukkit.event.HandlerList
import org.bukkit.entity.Player

case class PlayerChatBusEvent(server: String, player: PlayerState, message: String) extends BusEvent {
  override def getHandlers: HandlerList = PlayerChatBusEvent.getHandlerList
  override def eventIdentifier: String = PlayerChatBusEvent.eventIdentifier
}

object PlayerChatBusEvent extends BusEventObject {
  override def eventIdentifier: String = "player_chat"

  def apply(player: Player, message: String)(implicit serverId: String): PlayerChatBusEvent = {
    new PlayerChatBusEvent(serverId, PlayerState(player), message)
  }
}

object PlayerChatBusEventMarshaller extends SimpleEventMarshaller[PlayerChatBusEvent](PlayerChatBusEvent.eventIdentifier) {}