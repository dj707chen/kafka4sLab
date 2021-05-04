package kafka4s

import com.sksamuel.avro4s._

final case class CustomerId(id: String)

object CustomerId {
  implicit def customerIdRecordFormat = RecordFormat[CustomerId]
}
