package at.junction.api.bus.events

import at.junction.api.bus.Event
import org.json4s.native.Serialization.{read, write}

/**
 * User: HansiHE
 * Date: 3/2/14
 * Time: 6:43 PM
 */
class PlayerJoinEvent(name: String) extends Event {

  def getName: String = name

  def topicName: String = "minecraft.player.join"

  def serialize(event: Any): String = {
    write(event)
  }

  def deserialize(string: String) = {
    read[PlayerJoinEvent](string)
  }

  def getEvent: PlayerJoinEvent#Event = ???

}

/* object PlayerJoinEventSerializer extends EventSerializer[PlayerJoinEvent] {

  EventTypes.registerType(this)

  def topicName = "minecraft.player.join"

  def serialize(event: PlayerJoinEvent): String = {
    write(event)
  }

  def deserialize(string: String): PlayerJoinEvent = {
    read[PlayerJoinEvent](string)
  }

}         */