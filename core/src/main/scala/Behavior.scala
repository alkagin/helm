package helm

import argonaut.Argonaut.jString
import argonaut.{DecodeJson, DecodeResult, EncodeJson}

sealed abstract class Behavior extends Product with Serializable

object Behavior {

  final case object Release extends Behavior
  final case object Delete extends Behavior

  def fromString(s: String): Option[Behavior] =
    s.toLowerCase match {
      case "release"  => Some(Release)
      case "delete"  => Some(Delete)
      case _          => None
    }

  def toString(b: Behavior): String =
    b match {
      case Release => "release"
      case Delete  => "delete"
    }

  implicit val BehaviorDecoder: DecodeJson[Behavior] =
    DecodeJson[Behavior] { c =>
      c.as[String].flatMap { s =>
        fromString(s) match {
          case Some(r) => DecodeResult.ok(r)
          case None => DecodeResult.fail(s"invalid behavior: $s", c.history)
        }
      }
    }

  implicit val BehaviorEncoder: EncodeJson[Behavior] =
    EncodeJson[Behavior] { hs => jString(toString(hs)) }
}