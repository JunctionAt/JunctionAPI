package at.junction.api.bus


import net.jodah.lyra.{Connections, ConnectionOptions}
import net.jodah.lyra.config.{RetryPolicy, RecoveryPolicies, Config}
import net.jodah.lyra.util.Duration
import com.rabbitmq.client.{Channel, Connection, QueueingConsumer}
import net.jodah.lyra.event.ConnectionListener

import akka.actor.{ActorSystem, Actor, Props}
import org.bukkit.plugin.PluginManager

/**
 * User: HansiHE
 * Date: 3/2/14
 * Time: 5:06 PM
 */

case class BusOptions(host: String, exchange: String, source: String, debug: Boolean)

class Bus(busOptions: BusOptions, actorSystem: ActorSystem, pm: PluginManager) {

  val config: Config = new Config()
    .withRecoveryPolicy(RecoveryPolicies.recoverAlways())
    .withRetryPolicy(new RetryPolicy()
      .withBackoff(Duration.seconds(1), Duration.seconds(30)))
  val options: ConnectionOptions = new ConnectionOptions().withHost(busOptions.host)

  val connection: Connection = Connections.create(options, config)
  val channel: Channel = connection.createChannel()

  channel.exchangeDeclare(busOptions.exchange, "topic")

  val queueName = channel.queueDeclare().getQueue
  channel.queueBind(queueName, busOptions.exchange, "#")

  actorSystem.actorOf(Props(new ListeningEventDispatchActor(channel))) ! Unit

  def publish(event: BusEvent, channel: Channel=this.channel) = {
    val raw = BusEvents.serialize(event, source = busOptions.source)
    if (busOptions.debug) println("Debug: Bus Out: " + raw)
    channel.basicPublish(busOptions.exchange, event.eventIdentifier, false, null, raw.getBytes)
  }

  def close() = {
    connection.close()
  }

  class ListeningEventDispatchActor(channel: Channel) extends Actor {
    override def receive: Receive = {
      case _ => startReceving()
    }

    def startReceving() = {
      val consumer = new QueueingConsumer(channel)
      channel.basicConsume(queueName, true, consumer)

      while (true) {
        val delivery = consumer.nextDelivery()
        val msg = new String(delivery.getBody)

        context.actorOf(Props(new Actor {
          def receive = {
            case raw_event: String => deserializeAndSend(raw_event)
          }

          def deserializeAndSend(raw_event: String) = {
            if (busOptions.debug) println("Debug: Bus In: " + raw_event)
            var event: BusEvent = null
            try {
              event = BusEvents.deserialize(raw_event)
            } catch {
              case e: NoSuchTypeException => if (busOptions.debug) println("Debug: Event type " + e.event_type + " has not been registered. Ignoring.")
              case e: Exception => e.printStackTrace()
            }
            if (busOptions.debug) println("Debug: Event In: " + event)
            pm.callEvent(event)
          }
        })) ! msg
      }
    }
  }

}

