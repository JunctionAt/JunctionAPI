package at.junction.api.bus.serializers

import org.json4s._
import org.json4s.JsonDSL._
import java.util.UUID

object MojangUUID {
  def UUID2MojangUUID(uuid: UUID): String = { // D:
    uuid.toString.replaceAll("-", "")
  }

  def MojangUUID2UUID(mojangUUID: String): UUID = {
    if (mojangUUID.length != 32) {
      throw new IllegalArgumentException("Invalid UUID string: " + mojangUUID)
    }

    UUID.fromString(mojangUUID.substring(0, 8) + "-" +
      mojangUUID.substring(8, 12) + "-" +
      mojangUUID.substring(12, 16) + "-" +
      mojangUUID.substring(16, 20) + "-" +
      mojangUUID.substring(20, 32)
    )
  }
}

class UUIDSerializer extends Serializer[UUID]{
  val UUIDClass = classOf[UUID]

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: UUID => MojangUUID.UUID2MojangUUID(x)
  }

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), UUID] = {
    case (TypeInfo(UUIDClass, _), json) => json match {
      case JString(id) =>
        if (id.length != 32)
          new MappingException("Can't convert " + id + "to Java UUID object, invalid length (should be 32, is " + id.length + ")")
        MojangUUID.MojangUUID2UUID(id)
      case x => throw new MappingException("Can't convert " + x + "to Java UUID object")
    }
  }
}
