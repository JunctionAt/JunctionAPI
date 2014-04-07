package at.junction.api.rest

import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, CustomSerializer, NoTypeHints}
import java.util.Date
import java.text.SimpleDateFormat

/**
 * User: HansiHE
 * Date: 3/2/14
 * Time: 12:53 PM
 */
trait JsonFields {

  implicit val formats = new DefaultFormats {
    override val dateFormatter = new SimpleDateFormat("hh:mm dd/MM/yyyy aa")
  }

}
