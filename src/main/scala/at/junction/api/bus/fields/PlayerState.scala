package at.junction.api.bus.fields

import java.util.UUID
import org.bukkit.entity.Player

case class PlayerState(name: String, uuid: UUID, location: LocationDescriptor) {}

object PlayerState {
  def apply(player: Player): PlayerState = {
    PlayerState(player.getName, player.getUniqueId, LocationDescriptor(player.getLocation))
  }
}