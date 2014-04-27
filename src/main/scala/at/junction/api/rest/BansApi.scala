package at.junction.api.rest

import java.util.Date
import at.junction.api.fields.PlayerIdentifier
import java.util
import scala.collection.JavaConversions._
import at.junction.api.BanStatus


/**
 * User: HansiHE
 * Date: 3/2/14
 * Time: 3:14 AM
 */
class BansApi(api: RestApi) extends ApiModule(api) {

  case class Ban(id: Integer, issuer: PlayerIdentifier, target: PlayerIdentifier, reason: String,
              server: String, time: Date = null, active: Boolean, remove_time: Date = null, remove_user: String,
              source: String)

  case class Note(id: Integer, issuer: PlayerIdentifier, target: PlayerIdentifier, server: String,
              time: Date = null, active: Boolean, note: String)


  def getBans(target: PlayerIdentifier, active: BanStatus = BanStatus.Active): util.List[Ban] = {
    val request = GET("/anathema/bans")
    .param("uuid", target.mojangUUID)
    .param("scope", "local")
    .param("active", active match {
      case BanStatus.Both => "none"
      case BanStatus.Active => "true"
      case BanStatus.Inactive => "false"
    })

    val res = request.asString
    println("RES: " + res)
    val json = parseApiResponse(res)

    (json \ "bans").extract[List[Ban]]
  }

  def getNotes(target: PlayerIdentifier, active: BanStatus): util.List[Note] = {
    val request = GET("/anathema/notes")
      .param("uuid", target.mojangUUID)
      .param("scope", "local")
      .param("active", active match {
      case BanStatus.Both => "none"
      case BanStatus.Active => "true"
      case BanStatus.Inactive => "false"
    })

    val res = request.asString
    println("RES: " + res)
    val json = parseApiResponse(res)

    (json \ "notes").extract[List[Note]]
  }

  def addNote(target: PlayerIdentifier, issuer: PlayerIdentifier, message: String) = {
    val request = POST("/anathema/notes", asPlayer = issuer)
      .param("server", api.getServer)
      .param("uuid", target.mojangUUID)
      .param("note", message)

    val json = parseApiResponse(request.asString)
  }

  def addBan(target: PlayerIdentifier, issuer: PlayerIdentifier, reason: String) = {
    val request = POST("/anathema/bans", asPlayer = issuer)
      .param("server", api.getServer)
      .param("uuid", target.mojangUUID)
      .param("reason", reason)

    val json = parseApiResponse(request.asString)
  }

  def delNote(id: Integer, issuer: PlayerIdentifier) = {
    val request = DELETE("/anathema/notes", asPlayer = issuer)
      .param("id", id.toString)

    val json = parseApiResponse(request.asString)
  }

  def delBan(target: PlayerIdentifier, issuer: PlayerIdentifier) = {
    val request = DELETE("/anathema/bans", asPlayer = issuer)
      .param("uuid", target.mojangUUID)

    val json = parseApiResponse(request.asString)
  }

}
