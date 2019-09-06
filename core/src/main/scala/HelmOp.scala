package helm

import java.util.UUID

import scala.collection.immutable.{Set => SSet}
import argonaut.{DecodeJson, EncodeJson, StringWrap}, StringWrap.StringToParseWrap
import cats.data.NonEmptyList
import cats.free.Free
import cats.free.Free.liftF

sealed abstract class ConsulOp[A] extends Product with Serializable

object ConsulOp {

  final case class KVGet(
    key:        Key,
    recurse:    Option[Boolean],
    datacenter: Option[String],
    separator:  Option[String],
    index:      Option[Long],
    maxWait:    Option[Interval]
  ) extends ConsulOp[QueryResponse[List[KVGetResult]]]

  final case class KVGetRaw(
    key:     Key,
    index:   Option[Long],
    maxWait: Option[Interval]
  ) extends ConsulOp[QueryResponse[Option[Array[Byte]]]]

  final case class KVSet(key: Key, value: Array[Byte], lockOperation: Option[LockOperation]) extends ConsulOp[Boolean]

  final case class KVDelete(key: Key) extends ConsulOp[Unit]

  final case class KVListKeys(prefix: Key) extends ConsulOp[SSet[String]]

  final case class HealthListChecksForService(
    service:    String,
    datacenter: Option[String],
    near:       Option[String],
    nodeMeta:   Option[String],
    index:      Option[Long],
    maxWait:    Option[Interval]
  ) extends ConsulOp[QueryResponse[List[HealthCheckResponse]]]

  final case class HealthListChecksForNode(
    node:       String,
    datacenter: Option[String],
    index:      Option[Long],
    maxWait:    Option[Interval]
  ) extends ConsulOp[QueryResponse[List[HealthCheckResponse]]]

  final case class HealthListChecksInState(
    state:      HealthStatus,
    datacenter: Option[String],
    near:       Option[String],
    nodeMeta:   Option[String],
    index:      Option[Long],
    maxWait:    Option[Interval]
  ) extends ConsulOp[QueryResponse[List[HealthCheckResponse]]]

  // There's also a Catalog function called List Nodes for Service
  final case class HealthListNodesForService(
    service:     String,
    datacenter:  Option[String],
    near:        Option[String],
    nodeMeta:    Option[String],
    tag:         Option[String],
    passingOnly: Option[Boolean],
    index:       Option[Long],
    maxWait:     Option[Interval]
  ) extends ConsulOp[QueryResponse[List[HealthNodesForServiceResponse]]]

  final case object AgentListServices extends ConsulOp[Map[String, ServiceResponse]]

  final case class AgentRegisterService(
    service:           String,
    id:                Option[String],
    tags:              Option[NonEmptyList[String]],
    address:           Option[String],
    port:              Option[Int],
    enableTagOverride: Option[Boolean],
    check:             Option[HealthCheckParameter],
    checks:            Option[NonEmptyList[HealthCheckParameter]]
  ) extends ConsulOp[Unit]

  final case class AgentDeregisterService(id: String) extends ConsulOp[Unit]

  final case class AgentEnableMaintenanceMode(id: String, enable: Boolean, reason: Option[String]) extends ConsulOp[Unit]

  final case class SessionCreate(
    datacenter: Option[String],
    lockDelay:  Option[String],
    node:       Option[String],
    name:       Option[String],
    checks:     Option[NonEmptyList[String]],
    behavior:   Option[Behavior],
    ttl:        Option[Interval]
  ) extends ConsulOp[SessionCreateResponse]

  final case class SessionDestroy(uuid: UUID) extends ConsulOp[Unit]

  final case class SessionInfo(uuid: UUID) extends ConsulOp[QueryResponse[List[SessionInfoResponse]]]

  type ConsulOpF[A] = Free[ConsulOp, A]

  def kvGet(
    key:        Key,
    recurse:    Option[Boolean],
    datacenter: Option[String],
    separator:  Option[String],
    index:      Option[Long],
    maxWait:    Option[Interval]
  ): ConsulOpF[QueryResponse[List[KVGetResult]]] =
    liftF(KVGet(key, recurse, datacenter, separator, index, maxWait))

  def kvGetRaw(
    key:   Key,
    index: Option[Long],
    maxWait:    Option[Interval]
  ): ConsulOpF[QueryResponse[Option[Array[Byte]]]] =
    liftF(KVGetRaw(key, index, maxWait))

  def kvGetJson[A:DecodeJson](
    key:     Key,
    index:   Option[Long],
    maxWait: Option[Interval]
  ): ConsulOpF[Either[Err, QueryResponse[Option[A]]]] =
    kvGetRaw(key, index, maxWait).map { response =>
      response.value match {
        case Some(bytes) =>
          new String(bytes, "UTF-8").decodeEither[A].right.map(decoded => response.copy(value = Some(decoded)))
        case None =>
          Right(response.copy(value = None))
      }
    }

  def kvSet(key: Key, value: Array[Byte], lockOperation: Option[LockOperation]): ConsulOpF[Boolean] =
    liftF(KVSet(key, value, lockOperation))

  def kvSetJson[A](key: Key, value: A, lockOperation: Option[LockOperation])(implicit A: EncodeJson[A]): ConsulOpF[Boolean] =
    kvSet(key, A.encode(value).toString.getBytes("UTF-8"), lockOperation)

  def kvDelete(key: Key): ConsulOpF[Unit] =
    liftF(KVDelete(key))

  def kvListKeys(prefix: Key): ConsulOpF[SSet[String]] =
    liftF(KVListKeys(prefix))

  def healthListChecksForService(
    service:    String,
    datacenter: Option[String],
    near:       Option[String],
    nodeMeta:   Option[String],
    index:      Option[Long],
    maxWait:    Option[Interval]
  ): ConsulOpF[QueryResponse[List[HealthCheckResponse]]] =
    liftF(HealthListChecksForService(service, datacenter, near, nodeMeta, index, maxWait))

  def healthListChecksForNode(
    node:       String,
    datacenter: Option[String],
    index:      Option[Long],
    maxWait:    Option[Interval]
  ): ConsulOpF[QueryResponse[List[HealthCheckResponse]]] =
    liftF(HealthListChecksForNode(node, datacenter, index, maxWait))

  def healthListChecksInState(
    state:      HealthStatus,
    datacenter: Option[String],
    near:       Option[String],
    nodeMeta:   Option[String],
    index:      Option[Long],
    maxWait:    Option[Interval]
  ): ConsulOpF[QueryResponse[List[HealthCheckResponse]]] =
    liftF(HealthListChecksInState(state, datacenter, near, nodeMeta, index, maxWait))

  def healthListNodesForService(
    service:     String,
    datacenter:  Option[String],
    near:        Option[String],
    nodeMeta:    Option[String],
    tag:         Option[String],
    passingOnly: Option[Boolean],
    index:       Option[Long],
    maxWait:     Option[Interval]
  ): ConsulOpF[QueryResponse[List[HealthNodesForServiceResponse]]] =
    liftF(HealthListNodesForService(service, datacenter, near, nodeMeta, tag, passingOnly, index, maxWait))

  def agentListServices(): ConsulOpF[Map[String, ServiceResponse]] =
    liftF(AgentListServices)

  def agentRegisterService(
    service:           String,
    id:                Option[String],
    tags:              Option[NonEmptyList[String]],
    address:           Option[String],
    port:              Option[Int],
    enableTagOverride: Option[Boolean],
    check:             Option[HealthCheckParameter],
    checks:            Option[NonEmptyList[HealthCheckParameter]]
  ): ConsulOpF[Unit] =
    liftF(AgentRegisterService(service, id, tags, address, port, enableTagOverride, check, checks))

  def agentDeregisterService(id: String): ConsulOpF[Unit] =
    liftF(AgentDeregisterService(id))

  def agentEnableMaintenanceMode(id: String, enable: Boolean, reason: Option[String]): ConsulOpF[Unit] =
    liftF(AgentEnableMaintenanceMode(id, enable, reason))

  def sessionCreate(
    datacenter: Option[String],
    lockDelay:  Option[String],
    node:       Option[String],
    name:       Option[String],
    checks:     Option[NonEmptyList[String]],
    behavior:   Option[Behavior],
    ttl:        Option[Interval]
  ): ConsulOpF[SessionCreateResponse] =
    liftF(SessionCreate(datacenter, lockDelay, node, name, checks, behavior, ttl))

  def sessionDestroy(uuid: UUID): ConsulOpF[Unit] =
    liftF(SessionDestroy(uuid))

  def sessionInfo(uuid: UUID): ConsulOpF[QueryResponse[List[SessionInfoResponse]]] =
    liftF(SessionInfo(uuid))
}
