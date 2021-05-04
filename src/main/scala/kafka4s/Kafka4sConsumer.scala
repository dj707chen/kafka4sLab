package kafka4s

import cats.effect._
import com.banno.kafka._
import com.banno.kafka.consumer._
import kafka4s.Kafka4sCommon._
import org.apache.kafka.common.TopicPartition

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object Kafka4sConsumer extends App {
  receive()

  def receive(): Unit = {
    println("\nKafka4sConsumer")

    implicit val CS: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val consumer = ConsumerApi.Avro4s.resource[IO, CustomerId, Customer](
      BootstrapServers(kafkaBootstrapServers),
      SchemaRegistryUrl(schemaRegistryUri),
      ClientId("consumer-example"),
      GroupId("consumer-example-group")
    )

    val initialOffsets = Map.empty[TopicPartition, Long] // Start from beginning

    val messages = consumer.use(c =>
      c.assign(Kafka4sCommon.topicName, initialOffsets) *> c.recordStream(1.second).take(5).compile.toVector
    ).unsafeRunSync
    pprint.log(messages)
  }
}
