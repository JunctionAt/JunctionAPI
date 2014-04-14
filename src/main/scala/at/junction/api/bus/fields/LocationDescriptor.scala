package at.junction.api.bus.fields

import org.bukkit.Location

case class LocationDescriptor(world: String, x: Double, y: Double, z: Double, pitch: Float, yaw: Float) {}

object LocationDescriptor {
  def apply(loc: Location): LocationDescriptor = {
    new LocationDescriptor(loc.getWorld.getName, loc.getX, loc.getY, loc.getZ, loc.getPitch, loc.getYaw)
  }
}