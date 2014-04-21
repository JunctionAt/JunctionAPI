package at.junction.api.rest

import java.util.UUID
import at.junction.api.fields.PlayerIdentifier
import at.junction.api.serializers.UUIDImplicits._

class UUIDApi(api: RestApi) extends ApiModule(api) {

  def getPlayer(uuid: UUID = null, name: String = null): PlayerIdentifier = {
    val request = GET("/uuid")
    if(uuid != null) request.param("uuid", uuid)
    if(name != null) request.param("name", name)

    val json = parseApiResponse(request.asString)

    json.extract[PlayerIdentifier]
  }

  def getPlayerByName(name: String): PlayerIdentifier = getPlayer(name = name)
  def getPlayerByUUID(uuid: UUID): PlayerIdentifier = getPlayer(uuid = uuid)
  def getPlayerByPlayer(player: PlayerIdentifier): PlayerIdentifier = getPlayer(uuid = player.uuid)

}
