package at.junction.api

import com.joshcough.minecraft.{ListenersPlugin, ScalaPlugin}
import at.junction.api.rest.RestApi
import at.junction.api.bus.{BusOptions, BusEvents, Bus}
import org.bukkit.event.{EventHandler, Listener}
import at.junction.api.bus.events._
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

//import at.junction.api.bus.events.PlayerJoinEvent

/**
 * User: HansiHE
 * Date: 3/1/14
 * Time: 4:26 PM
 */
class Main extends ScalaPlugin with ListenersPlugin {

  val akkaConfig = ConfigFactory.load(getClassLoader)
  val actorSystem = ActorSystem("JunctionAPI", akkaConfig, getClassLoader)

  var restApi: RestApi = null
  var messageBus: Bus = null

  implicit var serverId: String = null

  override def onEnable = {
    super.onEnable()
    this.saveDefaultConfig()

    serverId = getConfig.getString("server-revision-id")

    restApi = new RestApi(
      getConfig.getString("api-url"),
      getConfig.getString("api-key"),
      serverId)

    registerEvents()
    messageBus = new Bus(new BusOptions("localhost", "test", serverId), actorSystem, this.pluginManager)
  }

  override def onDisable = {
    super.onDisable()
    //messageBus.close()
  }

  def registerEvents() = {
    PlayerJoinBusEventMarshaller.register()
    PlayerQuitBusEventMarshaller.register()
  }

  def listeners: List[Listener] = List(
    OnPlayerJoin{ (player, event) => messageBus.publish(PlayerJoinBusEvent(player)) },
    OnPlayerQuit{ (player, event) => messageBus.publish(PlayerQuitBusEvent(player)) },
    new Listener {
      @EventHandler def on(e: PlayerJoinBusEvent) = println("YEEE: " + e.player.name)
    }
  )
}