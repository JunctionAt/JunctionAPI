package at.junction.api.bus

import net.jodah.lyra.{Connections, ConnectionOptions}
import net.jodah.lyra.config.{RetryPolicy, RecoveryPolicies, Config}
import net.jodah.lyra.util.Duration
import com.rabbitmq.client.{Channel, Connection}
import net.jodah.lyra.event.ConnectionListener

/**
 * User: HansiHE
 * Date: 3/2/14
 * Time: 5:06 PM
 */
class Bus {

  final val exchange_name = "jmc-bus"

  val config: Config = new Config()
    .withRecoveryPolicy(RecoveryPolicies.recoverAlways())
    .withRetryPolicy(new RetryPolicy()
      .withBackoff(Duration.seconds(1), Duration.seconds(30)))
  val options: ConnectionOptions = new ConnectionOptions().withHost("localhost")

  val connection: Connection = Connections.create(options, config)
  val channel: Channel = connection.createChannel()

  def publish(event: Event) = {
    val serializer = EventTypes.getSerializerFromEvent(event)
    channel.basicPublish(exchange_name, serializer.topicName, null, serializer.serialize(event).getBytes)
  }

  def close() = {
    connection.close()
  }

}
