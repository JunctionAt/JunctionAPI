package at.junction.api.rest

import java.util.Date

/**
 * User: HansiHE
 * Date: 3/2/14
 * Time: 3:14 AM
 */
class BansApi(api: RestApi) extends JsonFields {

  object BanStatus extends Enumeration {
    type BanStatus = Value
    val Both, Active, Inactive = Value
  }
  import BanStatus._

  case class Ban(id: Integer, issuer: String, username: String, reason: String, server: String,
            time: Date = null, active: Boolean, remove_time: Date = null, remove_user: String, source: String)

  case class Note(id: Integer, issuer: String, username: String, server: String,
             time: Date = null, active: Boolean, note: String)

  def getBans(username: String, active: BanStatus) = {
    val request = api.get("/anathema/bans")
      .param("username", username)
      .param("scope", "local")
      .param("active", active match {
        case Both => "none"
        case Active => "true"
        case Inactive => "false"
      })

    val json = api.parseApiResponse(request.toString)

    (json \ "bans").extract[List[Ban]]
  }

  def getNotes(username: String, active: BanStatus) = {
    val request = api.get("/anathema/notes")
      .param("username", username)
      .param("scope", "local")
      .param("active", active match {
        case Both => "none"
        case Active => "true"
        case Inactive => "false"
      })

    val json = api.parseApiResponse(request.toString)

    (json \ "notes").extract[List[Note]]
  }

  def addNote(username: String, issuer: String, message: String) = {
    val request = api.post("/anathema/notes", asUser = issuer)
      .param("server", api.getServer)
      .param("username", username)
      .param("note", message)

    val json = api.parseApiResponse(request.toString)
  }

  def addBan(username: String, issuer: String, reason: String) = {
    val request = api.post("/anathema/bans", asUser = issuer)
      .param("server", api.getServer)
      .param("username", username)
      .param("reason", reason)

    val json = api.parseApiResponse(request.toString)
  }

  def delNote(id: Integer, issuer: String) = {
    val request = api.delete("/anathema/notes", asUser = issuer)
      .param("id", id.toString)

    val json = api.parseApiResponse(request.toString)
  }

  def delBan(username: String, issuer: String) = {
    val request = api.delete("/anathema/bans", asUser = issuer)
      .param("username", username)

    val json = api.parseApiResponse(request.toString)
  }

}
