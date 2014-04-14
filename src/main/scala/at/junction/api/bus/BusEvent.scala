
package at.junction.api.bus

import org.bukkit.event.{HandlerList, Event}

/**
 * User: HansiHE
 * Date: 3/3/14
 * Time: 1:26 AM
 */

trait BusEvent extends Event {

  var source: String = null

  def eventIdentifier: String

}

trait BusEventObject {
  val handlers: HandlerList = new HandlerList()

  def getHandlerList = handlers

  def eventIdentifier: String

}