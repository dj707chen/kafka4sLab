package kafka4s

object Kafka4sCallPandC extends App {
  println("Kafka4sCallPandC")

  Kafka4sProducer.send()

  Kafka4sConsumer.receive()

}
