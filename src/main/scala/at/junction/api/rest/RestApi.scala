package at.junction.api.rest

import scalaj.http.{HttpException, Http}
import org.json4s.native.JsonMethods._
import org.json4s.JsonAST._

/**
 * User: HansiHE
 * Date: 3/2/14
 * Time: 1:27 AM
 */
class RestApi(val apiUrl: String, val apiKey: String, val serverId: String) {

  val alts = new AltApi(this)
  val bans = new BansApi(this)

  def getServer: String = serverId

  def formatResource(resource: String) = {
    apiUrl + resource
  }

}

abstract class ApiModule(restApi: RestApi) extends JsonFields {

  def request(method: String, url: String, asUser: String = null): Http.Request = {
    val reqFunc: Http.HttpExec = (req, conn) => conn.connect()
    val request = Http.Request(reqFunc, Http.appendQsHttpUrl(url), method).header("ApiKey", restApi.apiKey)
    if (asUser != null)
      request.header("AsUser", asUser)
    request
  }

  def GET(resource: String, asUser: String = null): Http.Request = request("GET", restApi.formatResource(resource), asUser)
  def POST(resource: String, asUser: String = null): Http.Request = request("POST", restApi.formatResource(resource), asUser)
  def PUT(resource: String, asUser: String = null): Http.Request = request("PUT", restApi.formatResource(resource), asUser)
  def DELETE(resource: String, asUser: String = null): Http.Request = request("DELETE", restApi.formatResource(resource), asUser)

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