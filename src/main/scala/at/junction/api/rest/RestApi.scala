package at.junction.api.rest

import scalaj.http.{HttpException, Http}
import org.json4s.native.JsonMethods._
import org.json4s.JsonAST._

/**
 * User: HansiHE
 * Date: 3/2/14
 * Time: 1:27 AM
 */
class RestApi(apiUrl: String, apiKey: String, serverId: String) {

  val alts = new AltApi(this)
  val bans = new BansApi(this)

  def getServer: String = serverId

  def request(method: String, url: String, asUser: String = null): Http.Request = {
    val reqFunc: Http.HttpExec = (req, conn) => conn.connect()
    val request = Http.Request(reqFunc, Http.appendQsHttpUrl(url), method).header("ApiKey", apiKey)
    if (asUser != null)
      request.header("AsUser", asUser)
    request
  }

  def formatResource(resource: String) = {
    apiUrl + resource
  }

  def get(resource: String, asUser: String = null): Http.Request = request("GET", formatResource(resource), asUser)
  def post(resource: String, asUser: String = null): Http.Request = request("POST", formatResource(resource), asUser)
  def put(resource: String, asUser: String = null): Http.Request = request("PUT", formatResource(resource), asUser)
  def delete(resource: String, asUser: String = null): Http.Request = request("DELETE", formatResource(resource), asUser)

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
