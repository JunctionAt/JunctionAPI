package at.junction.api

import com.joshcough.minecraft.{ListenersPlugin, ScalaPlugin}
import at.junction.api.rest.RestApi
import at.junction.api.bus.Bus
import org.bukkit.event.Listener
import at.junction.api.bus.events.PlayerJoinEvent

/**
 * User: HansiHE
 * Date: 3/1/14
 * Time: 4:26 PM
 */
class Main extends ScalaPlugin with ListenersPlugin{

  var restApi: RestApi = null
  var messageBus: Bus = null

  override def onEnable(): Unit = {
    super.onEnable()
    this.saveDefaultConfig()

    restApi = new RestApi(
      this.getConfig.getString("api-url"),
      this.getConfig.getString("api-key"),
      this.getConfig.getString("server-revision-id"))

    //messageBus = new Bus()
  }

  override def onDisable(): Unit = {
    super.onDisable()
    //messageBus.close()
  }

  def listeners: List[Listener] = List(
  //  OnPlayerJoin{ (player, event) => messageBus.publish(new PlayerJoinEvent(player.getPlayerListName)) }
  )
}