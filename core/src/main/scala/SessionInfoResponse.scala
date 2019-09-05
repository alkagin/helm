package helm

import java.util.UUID

import argonaut._, Argonaut._

final case class SessionInfoResponse(
  id:          UUID,
  name:        Option[String],
  node:        String,
  checks:      List[String],
  lockDelay:   Long,
  behavior:    Behavior,
  ttl:         Option[String],
  createIndex: Long,
  modifyIndex: Long
)

object SessionInfoResponse {
  implicit def SessionInfoResponseDecoder: DecodeJson[SessionInfoResponse] =
    DecodeJson(j =>
      for {
        id          <- (j --\ "ID").as[UUID]
        name        <- (j --\ "Name").as[Option[String]]
        node        <- (j --\ "Node").as[String]
        checks      <- (j --\ "Checks").as[List[String]]
        lockDelay   <- (j --\ "LockDelay").as[Long]
        behavior    <- (j --\ "Behavior").as[Behavior]
        ttl         <- (j --\ "TTL").as[Option[String]]
        createIndex <- (j --\ "CreateIndex").as[Long]
        modifyIndex <- (j --\ "ModifyIndex").as[Long]
      } yield SessionInfoResponse(
        id,
        name,
        node,
        checks,
        lockDelay,
        behavior,
        ttl,
        createIndex,
        modifyIndex)
    )
}

