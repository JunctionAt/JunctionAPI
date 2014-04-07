package at.junction.api.rest

import org.json4s._
import org.json4s.native.JsonMethods._
import scala.throws

/**
 * User: HansiHE
 * Date: 3/2/14
 * Time: 1:47 AM
 */
class AltApi(api: RestApi) extends JsonFields {

  class Alt(alt: String, last_login: String)

  @throws(classOf[api.ApiError])
  def getAlts(username: String): List[Alt] = {
    val request = api.get("/anathema/alts")
      .param("username", username)

    val json = api.parseApiResponse(request.toString)

    (json \ "alts").extract[List[Alt]]
  }

  @throws(classOf[api.ApiError])
  def addAlt(ip: String, username: String, allowed: Boolean) = {
    val request = api.post("/anathema/alts")
      .param("ip", ip)
      .param("username", username)
      .param("allowed", allowed.toString)

    val json = api.parseApiResponse(request.toString)
  }

}
