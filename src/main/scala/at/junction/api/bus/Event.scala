package at.junction.api.bus

/**
 * User: HansiHE
 * Date: 3/3/14
 * Time: 1:26 AM
 */
trait Event {

  abstract class Event

  def topicName: String

  def serialize(event: A): String

  def deserialize(string: String): A

  def getEvent: Event

}
