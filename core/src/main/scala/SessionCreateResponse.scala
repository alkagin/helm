package helm

import java.util.UUID
import argonaut._, Argonaut._

final case class SessionCreateResponse(id: UUID)

object SessionCreateResponse {
  implicit def SessionCreateResponseDecoder: DecodeJson[SessionCreateResponse] =
    DecodeJson(j =>
      for {
        id <- (j --\ "ID").as[UUID]
      } yield SessionCreateResponse(id)
    )
}

