package at.junction.api.bus.serializers

import org.json4s._
import org.json4s.JsonDSL._
import java.util.UUID

class UUIDSerializer extends Serializer[UUID]{
  val UUIDClass = classOf[UUID]

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: UUID => x.toString//.replaceAll("-", "")
  }

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), UUID] = {
    case (TypeInfo(UUIDClass, _), json) => json match {
      case JString(id) => UUID.fromString(id)
      case x => throw new MappingException("Can't convert " + x + "to Java UUID object")
    }
  }
}
