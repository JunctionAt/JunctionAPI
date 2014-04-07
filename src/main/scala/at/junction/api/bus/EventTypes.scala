package at.junction.api.bus

import scala.collection.mutable.ListBuffer
import scala.collection.mutable
import at.junction.api.bus.Event

/**
 * User: HansiHE
 * Date: 3/3/14
 * Time: 1:42 AM
 */
object EventTypes {

  val events: ListBuffer[EventSerializer] = ListBuffer[EventSerializer]()
  val eventsTopics: mutable.HashMap[String, EventSerializer] = mutable.HashMap[String, EventSerializer]()
  val eventsClasses: mutable.HashMap[Class[_ <: Event], EventSerializer] = mutable.HashMap[Class[_ <: Event], EventSerializer]()

  def registerType(serializer: EventSerializer) = {
    events += serializer
    eventsTopics += ((serializer.topicName, serializer))
    eventsClasses += ((serializer.eventType, serializer))
  }

  def getSerializerFromEvent(event: Event): EventSerializer = {
    eventsClasses.get(event.getClass).get
  }

  def getSerializerFromTopic(topic: String): EventSerializer = {
    eventsTopics.get(topic).get
  }

}
