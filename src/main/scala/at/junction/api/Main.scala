package at.junction.api

import com.joshcough.minecraft.{ListenersPlugin, ScalaPlugin}
import at.junction.api.rest.RestApi
import at.junction.api.bus.{BusOptions, BusEvents, Bus}
import org.bukkit.event.{EventHandler, Listener}
import at.junction.api.bus.events._
import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

//import at.junction.api.bus.events.PlayerJoinEvent

/**
 * User: HansiHE
 * Date: 3/1/14
 * Time: 4:26 PM
 */
class Main extends ScalaPlugin with ListenersPlugin {

  var akkaConfig: Config = null
  var actorSystem: ActorSystem = null

  var restApi: RestApi = null
  var messageBus: Bus = null

  implicit var serverId: String = null

  override def onEnable = {
    super.onEnable()
    this.saveDefaultConfig()

    akkaConfig = ConfigFactory.load(getClassLoader)
    actorSystem = ActorSystem("JunctionAPI", akkaConfig, getClassLoader)

    serverId = getConfig.getString("server-revision-id")

    restApi = new RestApi(
      getConfig.getString("api-url"),
      getConfig.getString("api-key"),
      serverId)

    registerEvents()
    messageBus = new Bus(
      new BusOptions(
        getConfig.getString("rabbitmq-host"),
        getConfig.getString("rabbitmq-exchange"),
        serverId,
        getConfig.getBoolean("bus-debug")),
      actorSystem, this.pluginManager)

    getServer.getScheduler.runTaskTimerAsynchronously(this, new Runnable {
      def run() = {
        messageBus.publish(ServerHeartbeatBusEvent())
      }
    }, 0, 20 * 5)

    messageBus.publish(ServerStartBusEvent())
  }

  override def onDisable = {

    messageBus.publish(ServerStopBusEvent())
    messageBus.close()

    //actorSystem.shutdown()

    super.onDisable()

    //messageBus.publish(ServerStopBusEvent())
    //messageBus.close()

    //actorSystem.shutdown()
  }

  def registerEvents() = {
    PlayerJoinBusEventMarshaller.register()
    PlayerQuitBusEventMarshaller.register()
    PlayerChatBusEventMarshaller.register()

    ServerHeartbeatBusEventMarshaller.register()
    ServerStartBusEventMarshaller.register()
    ServerStopBusEventMarshaller.register()
  }

  def listeners: List[Listener] = List(
    OnPlayerJoin{ (player, event) => messageBus.publish(PlayerJoinBusEvent(player)) },
    OnPlayerQuit{ (player, event) => messageBus.publish(PlayerQuitBusEvent(player)) },
    OnAsyncPlayerChat{ (player, event) => messageBus.publish(PlayerChatBusEvent(player, event.getMessage)) }
  )
}