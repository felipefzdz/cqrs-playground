case class Snapshot(offset: Int, value: Int, entityId: EntityId)

class SnapshotStore {
 def save(snapshot: Snapshot): Unit = ???

 def retrieve(entityId: EntityId): Option[Snapshot] = ???

}
