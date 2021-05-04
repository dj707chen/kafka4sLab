package kafka4s

import cats.effect._
import cats.implicits._
import com.banno.kafka.admin.AdminApi
import com.banno.kafka.producer.ProducerApi
import com.banno.kafka.{BootstrapServers, intSerializer}
import fs2.Stream
import kafka4s.Kafka4sCommon._
import org.apache.kafka.clients.producer._

import scala.concurrent.duration.DurationInt
import scala.util.Random

final class StreamProducer[F[_]: Concurrent: Timer] {

  val example: F[Unit] =
    for {
      _ <- Sync[F].delay(println("Starting kafka4s example"))
      _ <- AdminApi.createTopicsIdempotent[F](kafkaBootstrapServers, topic)
      writeStream = Stream
        .resource(ProducerApi.resource[F, Int, Int](BootstrapServers(kafkaBootstrapServers)))
        .flatMap { producer =>
          Stream
            .awakeDelay[F](1.second)
            .evalMap { _ =>
              Sync[F].delay(Random.nextInt()).flatMap { i =>
                producer.sendAndForget(new ProducerRecord(topic.name, i, i))
              }
            }
        }
    } yield ()

  Stream
    .resource(ProducerApi.resource[F, Int, Int](BootstrapServers(kafkaBootstrapServers)))
    .flatMap { producer =>
      Stream.awakeDelay[F](1.second)
        .evalMap { _ =>
          Sync[F].delay(Random.nextInt()).flatMap { i =>
            producer.sendAndForget(
              new ProducerRecord(topic.name, i, i)
            )
          }
        }
    }
}
