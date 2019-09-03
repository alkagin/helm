package helm

import argonaut._, Argonaut._

/** Case class representing the response to a KV "Read Key" API call to Consul */
final case class SessionCreateResult(id: String)


object SessionCreateResult {
  implicit def SessionCreateResultDecoder: DecodeJson[SessionCreateResult] =
    DecodeJson(j =>
      for {
        id <- (j --\ "ID").as[String]
      } yield SessionCreateResult(id)
    )
}

