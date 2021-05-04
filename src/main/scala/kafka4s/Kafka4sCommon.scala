package kafka4s

import org.apache.kafka.clients.admin.NewTopic

object Kafka4sCommon {

  // topic: NewTopic(name=customers.v1, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs=null)
  val topic = new NewTopic("customers.v1", 1, 1.toShort)
  val topicName: String = topic.name

  val kafkaBootstrapServers = "localhost:9092"
  val schemaRegistryUri = "http://localhost:8081"

}