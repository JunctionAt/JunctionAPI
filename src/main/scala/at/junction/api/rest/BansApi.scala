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

  @Deprecated
  def getBans(username: String, active: BanStatus): util.List[Ban] = {
    val request = GET("/anathema/bans")
      .param("username", username)
      .param("scope", "local")
      .param("active", active match {
        case BanStatus.Both => "none"
        case BanStatus.Active => "true"
        case BanStatus.Inactive => "false"
      })

    println(request.toString)

    val json = parseApiResponse(request.toString)

    (json \ "bans").extract[List[Ban]]
  }

  def getBans(target: PlayerIdentifier, active: BanStatus = BanStatus.Active): util.List[Ban] = {
    val request = GET("/anathema/bans")
    .param("uuid", target.mojangUUID)
    .param("scope", "local")
    .param("active", active match {
      case BanStatus.Both => "none"
      case BanStatus.Active => "true"
      case BanStatus.Inactive => "false"
    })

    println(request.toString)

    val json = parseApiResponse(request.toString)

    (json \ "bans").extract[List[Ban]]
  }

  @Deprecated
  def getNotes(username: String, active: BanStatus): util.List[Note] = {
    val request = GET("/anathema/notes")
      .param("username", username)
      .param("scope", "local")
      .param("active", active match {
        case BanStatus.Both => "none"
        case BanStatus.Active => "true"
        case BanStatus.Inactive => "false"
      })

    println(request.toString)

    val json = parseApiResponse(request.toString)

    (json \ "notes").extract[List[Note]]
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

    println(request.toString)

    val json = parseApiResponse(request.toString)

    (json \ "notes").extract[List[Note]]
  }

  @Deprecated
  def addNote(username: String, issuer: String, message: String) = {
    val request = POST("/anathema/notes", asUser = issuer)
      .param("server", api.getServer)
      .param("username", username)
      .param("note", message)

    println(request.toString)

    val json = parseApiResponse(request.toString)
  }


  def addNote(target: PlayerIdentifier, issuer: PlayerIdentifier, message: String) = {
    val request = POST("/anathema/notes", asPlayer = issuer)
      .param("server", api.getServer)
      .param("uuid", target.mojangUUID)
      .param("note", message)

    println(request.toString)

    val json = parseApiResponse(request.toString)
  }

  @Deprecated
  def addBan(username: String, issuer: String, reason: String) = {
    val request = POST("/anathema/bans", asUser = issuer)
      .param("server", api.getServer)
      .param("username", username)
      .param("reason", reason)

    println(request.toString)

    val json = parseApiResponse(request.toString)
  }

  def addBan(target: PlayerIdentifier, issuer: PlayerIdentifier, reason: String) = {
    val request = POST("/anathema/bans", asPlayer = issuer)
      .param("server", api.getServer)
      .param("uuid", target.mojangUUID)
      .param("reason", reason)

    println(request.toString)

    val json = parseApiResponse(request.toString)
  }

  def delNote(id: Integer, issuer: PlayerIdentifier) = {
    val request = DELETE("/anathema/notes", asPlayer = issuer)
      .param("id", id.toString)

    val json = parseApiResponse(request.toString)
  }

  @Deprecated
  def delBan(username: String, issuer: String) = {
    val request = DELETE("/anathema/bans", asUser = issuer)
      .param("username", username)

    println(request.toString)

    val json = parseApiResponse(request.toString)
  }

  def delBan(target: PlayerIdentifier, issuer: PlayerIdentifier) = {
    val request = DELETE("/anathema/bans", asPlayer = issuer)
      .param("uuid", target.mojangUUID)

    println(request.toString)

    val json = parseApiResponse(request.toString)
  }

}
