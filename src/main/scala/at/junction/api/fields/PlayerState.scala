package at.junction.api.fields

import java.util.UUID
import org.bukkit.entity.Player

case class PlayerState(name: String, uuid: UUID, location: LocationDescriptor) {}

object PlayerState {
  def apply(player: Player)(implicit serverId: String): PlayerState = {
    PlayerState(player.getName, player.getUniqueId, LocationDescriptor(player.getLocation))
  }
}