case class EntityId(id: String)

case class Entity(value: Int)

class StateBuilder(snapshotStore: SnapshotStore, eventStore: EventStore, numberOfEventsThatTriggersASnapshot: Int) {

 def reconstitute(entityId: EntityId): Entity = ???

}
