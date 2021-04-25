package kafka4s

import cats.effect._
import cats.implicits._
import com.banno.kafka._
import com.banno.kafka.admin._
import com.banno.kafka.producer._
import com.banno.kafka.schemaregistry._
import kafka4s.Kafka4sCommon.{CustomerIdRecordFormat, CustomerRecordFormat}
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerRecord

object Kafka4sProducer extends App {
  send()

  def send(): Unit = {
    println("\nKafka4sProducer")
    val topic = new NewTopic("customers.v1", 1, 1.toShort)
    // topic: NewTopic = (name=customers.v1, numPartitions=1, replicationFactor=1, replicasAssignments=null, configs=null)
    val topicName = topic.name

    val kafkaBootstrapServers = "localhost:9092"
    val schemaRegistryUri = "http://localhost:8081"

    AdminApi.createTopicsIdempotent[IO](kafkaBootstrapServers, topic :: Nil).unsafeRunSync

    SchemaRegistryApi.register[IO, CustomerId, Customer](
      schemaRegistryUri, topicName
    ).unsafeRunSync()

    val producer = ProducerApi.Avro.Generic.resource[IO](
      BootstrapServers(kafkaBootstrapServers),
      SchemaRegistryUrl(schemaRegistryUri),
      ClientId("producer-example")
    )
    // producer: Resource[IO, ProducerApi[IO, org.apache.avro.generic.GenericRecord, org.apache.avro.generic.GenericRecord]] = Allocate(
    //   Map(
    //     Map(
    //       Map(
    //         Delay(
    //           com.banno.kafka.producer.ProducerApi$$$Lambda$9728/2114273733@23a47bef
    //         ),
    //         com.banno.kafka.producer.ProducerApi$Avro$$$Lambda$9729/41440890@7b6c46d7,
    //         StackTrace(
    //           List(
    //             cats.effect.internals.IOTracing$.buildFrame(IOTracing.scala:48),

    val recordsToBeWritten = (1 to 10).map(a =>
      new ProducerRecord(
        topicName,
        CustomerId(a.toString),
        Customer(s"name-$a", s"address-$a")
      )
    ).toVector

    // producer.use(p => recordsToBeWritten.traverse_(p.sendSync))
    // error: type mismatch;
    //  found   : org.apache.kafka.clients.producer.ProducerRecord[org.apache.avro.generic.GenericRecord,org.apache.avro.generic.GenericRecord] => cats.effect.IO[org.apache.kafka.clients.producer.RecordMetadata]
    //  required: org.apache.kafka.clients.producer.ProducerRecord[repl.MdocSession.App.CustomerId,repl.MdocSession.App.Customer] => ?
    // producer.use(p => recordsToBeWritten.traverse_(p.sendSync))
    //

    val avro4sProducer = producer.map(_.toAvro4s[CustomerId, Customer])

    avro4sProducer.use(p =>
      recordsToBeWritten.traverse_(r =>
        p.sendSync(r).flatMap(rmd =>
          IO(println(s"Wrote record to $rmd"))
        )
      )
    ).unsafeRunSync
  }
}
