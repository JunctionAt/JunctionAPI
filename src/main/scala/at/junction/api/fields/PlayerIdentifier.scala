package at.junction.api.fields

import java.util.UUID
import org.bukkit.entity.Player
import at.junction.api.serializers.MojangUUID


object PlayerIdentifier {
  def apply(player: Player): PlayerIdentifier = new PlayerIdentifier(player.getUniqueId, player.getName)
}

case class PlayerIdentifier(uuid: UUID, name: String = null) {
  lazy val mojangUUID = MojangUUID.UUID2MojangUUID(uuid)

  @throws(classOf[IllegalArgumentException])
  def requireName() = if (name == null) throw new IllegalArgumentException()
}
