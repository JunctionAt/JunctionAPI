package at.junction.api.fields

import org.bukkit.Location

case class LocationDescriptor(server: String, world: String, x: Double, y: Double, z: Double, pitch: Float, yaw: Float) {}

object LocationDescriptor {
  def apply(loc: Location)(implicit serverId: String): LocationDescriptor = {
    new LocationDescriptor(serverId, loc.getWorld.getName, loc.getX, loc.getY, loc.getZ, loc.getPitch, loc.getYaw)
  }
}