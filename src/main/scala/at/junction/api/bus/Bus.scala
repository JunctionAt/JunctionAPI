package at.junction.api.bus


import net.jodah.lyra.{Connections, ConnectionOptions}
import net.jodah.lyra.config.{RetryPolicy, RecoveryPolicies, Config}
import com.rabbitmq.client.{ShutdownSignalException, Channel, Connection, QueueingConsumer}

import akka.actor._
import akka.pattern.ask
import org.bukkit.plugin.PluginManager
import akka.routing.SmallestMailboxRouter
import com.rabbitmq.client.QueueingConsumer.Delivery
import scala.concurrent.Await
import akka.util.Timeout
import scala.concurrent.duration._

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
      .withBackoff(net.jodah.lyra.util.Duration.seconds(1), net.jodah.lyra.util.Duration.seconds(30)))
  val options: ConnectionOptions = new ConnectionOptions().withHost(busOptions.host)

  val connection: Connection = Connections.create(options, config)
  val channel: Channel = connection.createChannel()

  channel.exchangeDeclare(busOptions.exchange, "topic")

  val inboundProcessor = actorSystem.actorOf(InboundEventActor.props(channel, busOptions, publishIncoming))
  inboundProcessor ! Unit // Kickstart it

  val outboundProcessor = actorSystem.actorOf(OutboundEventActor.props(channel, busOptions))

  def publish(event: BusEvent) = {
    /* val raw = BusEvents.serialize(event, source = busOptions.source)
    if (busOptions.debug) println("Debug: Bus Out: " + raw)
    channel.basicPublish(busOptions.exchange, event.eventIdentifier, false, null, raw.getBytes) */
    outboundProcessor ! event
  }

  def close() = {
    implicit val timeout = Timeout(5 seconds)

    inboundProcessor ! Kill
    val futureI = inboundProcessor ? Finish
    Await.result(futureI, timeout.duration)

    val futureO = outboundProcessor ? Finish
    Await.result(futureO, timeout.duration) // Wait until PoisonPill is processed before closing connection

    connection.close()
  }

  private def publishIncoming(event: BusEvent) = {
    //pm.callEvent(event)
  }

}

object Finish

object InboundEventActor {
  def props(channel: Channel, busOptions: BusOptions, publishIncoming: (BusEvent) => Unit): Props = Props(new InboundEventActor(channel, busOptions, publishIncoming))
}

class InboundEventActor(channel: Channel, busOptions: BusOptions, publishIncoming: (BusEvent) => Unit) extends Actor {
  var eventProcessor: ActorRef = context.actorOf(Props(classOf[InboundEventDispatchActor], publishIncoming, busOptions.debug))

  val queueName = channel.queueDeclare().getQueue
  channel.queueBind(queueName, busOptions.exchange, "#")

  def receive: Actor.Receive = {
    case Finish =>
      println("YAY")
      context stop self
      sender ! Unit
    case _ => startReceving()
  }

  def startReceving(): Unit = {
    val consumer = new QueueingConsumer(channel)
    channel.basicConsume(queueName, true, consumer)

    while (true) {
      var delivery: Delivery = null
      try {
        delivery = consumer.nextDelivery()
      } catch {
        case e: ShutdownSignalException => // If the connection is closing, we should kill ourselves (D:)
          context stop self
          return
      }
      val event = new String(delivery.getBody)

      eventProcessor ! event
    }
  }
}

class InboundEventDispatchActor(publishIncoming: (BusEvent) => Unit, debug: Boolean) extends Actor {
  def receive: Actor.Receive = {
    case event: String => processIncomingEvent(event)
  }

  def processIncomingEvent(raw_event: String) = {
    if (debug) println("Debug: Bus In: " + raw_event)
    var event: BusEvent = null
    try {
      event = BusEvents.deserialize(raw_event)
    } catch {
      case e: NoSuchTypeException => if (debug) println("Debug: Event type " + e.event_type + " has not been registered. Ignoring.")
      case e: Exception => println("Exception while deserializing: "); e.printStackTrace()
    }
    if (debug) println("Debug: Event In: " + event)
    publishIncoming(event)
  }
}

object OutboundEventActor {
  def props(channel: Channel, busOptions: BusOptions): Props = Props(new OutboundEventActor(channel, busOptions))
}

class OutboundEventActor(channel: Channel, busOptions: BusOptions) extends Actor {
  def receive: Actor.Receive = {
    case event: BusEvent => processOutgoingEvent(event)
    case Finish =>
      context stop self
      sender ! Unit
  }

  def processOutgoingEvent(event: BusEvent) = {
    val raw = BusEvents.serialize(event, source = busOptions.source)
    if (busOptions.debug) println("Debug: Bus Out: " + raw)
    channel.basicPublish(busOptions.exchange, event.eventIdentifier, false, null, raw.getBytes)
  }
}