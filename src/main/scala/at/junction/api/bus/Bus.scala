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

case class BusOptions(host: String, exchange: String, source: String)

class Bus(busOptions: BusOptions, actorSystem: ActorSystem, pm: PluginManager) {

  final val exchange_name = "jmc-bus"

  val config: Config = new Config()
    .withRecoveryPolicy(RecoveryPolicies.recoverAlways())
    .withRetryPolicy(new RetryPolicy()
      .withBackoff(Duration.seconds(1), Duration.seconds(30)))
  val options: ConnectionOptions = new ConnectionOptions().withHost("localhost")

  val connection: Connection = Connections.create(options, config)
  val channel: Channel = connection.createChannel()

  channel.exchangeDeclare(exchange_name, "topic")

  val queueName = channel.queueDeclare().getQueue
  channel.queueBind(queueName, exchange_name, "#")

  actorSystem.actorOf(Props(new ListeningEventDispatchActor(channel))) ! Unit

  def publish(event: BusEvent) = {
    val raw = BusEvents.serialize(event, source = busOptions.source)
    println("OUT: " + raw)
    channel.basicPublish(exchange_name, event.eventIdentifier, false, null, raw.getBytes)
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
            println("IN: " + raw_event)
            var event: BusEvent = null
            try {
              event = BusEvents.deserialize(raw_event)
            } catch {
              case e: Exception => e.printStackTrace()
            }
            println(event)
            pm.callEvent(event)
          }
        })) ! msg
      }
    }
  }

}

