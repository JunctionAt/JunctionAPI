package at.junction.api.rest

import scalaj.http.{HttpOptions, Http}
import org.json4s.native.JsonMethods._
import org.json4s.JsonAST._
import at.junction.api.fields.PlayerIdentifier

/**
 * User: HansiHE
 * Date: 3/2/14
 * Time: 1:27 AM
 */
class RestApi(val apiUrl: String, val apiKey: String, val serverId: String) {

  val alts = new AltApi(this)
  val bans = new BansApi(this)
  val players = new PlayersApi(this)

  def getServer: String = serverId

  def formatResource(resource: String) = {
    apiUrl + resource
  }

}

abstract class ApiModule(restApi: RestApi) extends JsonFields {

  def request(method: String, url: String, asUser: String = null, asPlayer: PlayerIdentifier = null): Http.Request = {
    val reqFunc: Http.HttpExec = (req, conn) => conn.connect()
    var request = Http.Request(reqFunc, Http.appendQsHttpUrl(url), method).option(HttpOptions.connTimeout(1000)).option(HttpOptions.readTimeout(5000)).header("ApiKey", restApi.apiKey)
    if (asUser != null)
      request = request.header("AsUser", asUser)
    if (asPlayer != null)
      request = request.header("AsPlayer", asPlayer.mojangUUID)
    request
  }

  def GET(resource: String, asUser: String = null, asPlayer: PlayerIdentifier = null): Http.Request =
    request("GET", restApi.formatResource(resource), asUser, asPlayer)
  def POST(resource: String, asUser: String = null, asPlayer: PlayerIdentifier = null): Http.Request =
    request("POST", restApi.formatResource(resource), asUser, asPlayer)
  def PUT(resource: String, asUser: String = null, asPlayer: PlayerIdentifier = null): Http.Request =
    request("PUT", restApi.formatResource(resource), asUser, asPlayer)
  def DELETE(resource: String, asUser: String = null, asPlayer: PlayerIdentifier = null): Http.Request =
    request("DELETE", restApi.formatResource(resource), asUser, asPlayer)

  class ApiError(e: String) extends Exception(e)

  @throws(classOf[ApiError])
  def parseApiResponse(response: String) = {
    val json = parse(response)

    if (json \ "error" != JNothing) {
      throw new ApiError(response)
    }

    json
  }

}