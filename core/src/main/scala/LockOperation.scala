package helm

import java.util.UUID

import helm.LockOperation.LockOperationType

final case class LockOperation(operation: LockOperationType, id: UUID)

object LockOperation {

  sealed abstract class LockOperationType extends Product with Serializable
  final case object Acquire extends LockOperationType
  final case object Release extends LockOperationType

  object LockOperationType {
    def toString(lockOperationType: LockOperationType): String =
      lockOperationType match {
        case LockOperation.Acquire => "acquire"
        case LockOperation.Release => "release"
      }
  }


  def acquire(id: UUID) = LockOperation(Acquire, id)

  def release(id: UUID) = LockOperation(Release, id)
}
