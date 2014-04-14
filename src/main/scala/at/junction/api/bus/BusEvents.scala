package at.junction.api.bus

import org.json4s._
import scala.collection.mutable
import org.json4s.native.Serialization
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write
import org.json4s.JsonDSL._
import at.junction.api.bus.events.{PlayerJoinBusEventMarshaller, PlayerJoinBusEvent}
import java.util.NoSuchElementException
import at.junction.api.bus.serializers.UUIDSerializer

object BusEvents {
  val types = mutable.Map[String, EventMarshaller]()

  def getType(event_type: String): EventMarshaller = {
    try {
      types(event_type)
    } catch {
      case e: NoSuchElementException => throw new NoSuchTypeException(event_type)
    }
  }

  implicit val formats = org.json4s.DefaultFormats

  def deserialize(raw: String): BusEvent = {
    val json = parse(raw)
    val event_type: String = (json \ "type").extract[String]

    val result = getType(event_type).deserialize(json)
    result.source = (json \ "source").extract[String]
    result
  }

  def serialize(event: BusEvent, source: String=""): String = {
    val identifier = event.eventIdentifier

    var json = getType(identifier).serialize(event)
    json = json merge (("type" -> identifier) ~ ("source" -> source))
    write(json)
  }
}

trait EventMarshaller {
  /**
   * Each event object must have an EventMarshaller to take care of serialization and deserialization.
   * Each EventMarshaller must implement at least three methods:
   *  * deserialize(input: JValue): BusEvent
   *  * serialize(input: BusEvent): JValue
   *  * register()
   */
  implicit var formats: Formats = org.json4s.DefaultFormats + new UUIDSerializer
  def deserialize(input: JValue): BusEvent
  def serialize(input: BusEvent): JValue
  def register()
}

case class NoSuchTypeException(event_type: String) extends Exception