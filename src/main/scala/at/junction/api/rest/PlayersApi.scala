package at.junction.api.rest

import java.util.UUID
import at.junction.api.fields.PlayerIdentifier
import at.junction.api.serializers.UUIDImplicits._

class PlayersApi(api: RestApi) extends ApiModule(api) {

  def getPlayer(uuid: UUID = null, name: String = null): PlayerIdentifier = {
    var request = GET("/uuid")

    if(uuid == null && name == null) throw new ApiError("getPlayer called with both uuid and username as null. Please supply at least one.")
    if(uuid != null) request = request.param("uuid", uuid)
    if(name != null) request = request.param("name", name)

    val json = parseApiResponse(request.asString)

    json.extract[PlayerIdentifier]
  }

  def getPlayerByName(name: String): PlayerIdentifier = getPlayer(name = name)
  def getPlayerByUUID(uuid: UUID): PlayerIdentifier = getPlayer(uuid = uuid)
  def getPlayerByPlayer(player: PlayerIdentifier): PlayerIdentifier = getPlayer(uuid = player.uuid)

}
