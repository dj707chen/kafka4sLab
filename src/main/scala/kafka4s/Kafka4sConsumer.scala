package kafka4s

import cats.effect._
import com.banno.kafka._
import com.banno.kafka.consumer._
import kafka4s.Kafka4sCommon.{CustomerIdRecordFormat, CustomerRecordFormat}
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.TopicPartition

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object Kafka4sConsumer extends App {
  receive()

  def receive(): Unit = {
    println("\nKafka4sConsumer")
    val topic = new NewTopic("customers.v1", 1, 1.toShort)
    // topic: NewTopic = (name=customers.v1, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs=null)
    val topicName = topic.name

    implicit val CS: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val kafkaBootstrapServers = "localhost:9092"
    val schemaRegistryUri = "http://localhost:8081"

    val consumer = ConsumerApi.Avro4s.resource[IO, CustomerId, Customer](
      BootstrapServers(kafkaBootstrapServers),
      SchemaRegistryUrl(schemaRegistryUri),
      ClientId("consumer-example"),
      GroupId("consumer-example-group")
    )

    val initialOffsets = Map.empty[TopicPartition, Long] // Start from beginning

    val messages = consumer.use(c =>
      c.assign(topicName, initialOffsets) *> c.recordStream(1.second).take(5).compile.toVector
    ).unsafeRunSync
    pprint.log(messages)
  }
}
