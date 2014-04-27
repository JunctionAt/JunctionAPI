package at.junction.api.rest

import org.json4s._
import scala.throws
import at.junction.api.fields.PlayerIdentifier
import java.util
import scala.collection.JavaConversions._
import java.util.UUID

/**
 * User: HansiHE
 * Date: 3/2/14
 * Time: 1:47 AM
 */

case class Alt(player: PlayerIdentifier, last_login: String)

class AltApi(api: RestApi) extends ApiModule(api) {

  @throws(classOf[ApiError])
  def getAlts(target: PlayerIdentifier): util.List[Alt] = {
    val request = GET("/anathema/alts")
      .param("uuid", target.mojangUUID)

    val json = parseApiResponse(request.asString)

    (json \ "alts").extract[List[Alt]]
  }

  @throws(classOf[ApiError])
  def ensurePlayerData(ip: String, target: PlayerIdentifier, allowed: Boolean) = {
    target.requireName()

    val request = POST("/anathema/alts")
      .param("ip", ip)
      .param("username", target.name)
      .param("uuid", target.mojangUUID)
      .param("allowed", allowed.toString)

    val json = parseApiResponse(request.asString)
  }

}
