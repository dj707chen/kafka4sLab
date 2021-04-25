package kafka4s

import com.sksamuel.avro4s.RecordFormat

case class Customer(name: String, address: String)

case class CustomerId(id: String)

object Kafka4sCommon {
  implicit val CustomerRecordFormat: RecordFormat[Customer] = com.sksamuel.avro4s.RecordFormat[Customer]
  implicit val CustomerIdRecordFormat: RecordFormat[CustomerId] = com.sksamuel.avro4s.RecordFormat[CustomerId]
}