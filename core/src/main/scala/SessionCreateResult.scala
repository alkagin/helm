package helm

import java.util.UUID
import argonaut._, Argonaut._

/** Case class representing the response to a KV "Read Key" API call to Consul */
final case class SessionCreateResult(id: UUID)


object SessionCreateResult {
  implicit def SessionCreateResultDecoder: DecodeJson[SessionCreateResult] =
    DecodeJson(j =>
      for {
        id <- (j --\ "ID").as[UUID]
      } yield SessionCreateResult(id)
    )
}

