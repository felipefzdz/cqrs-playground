case class EntityId(id: String)

case class Entity(value: Int)

class StateBuilder(snapshotStore: SnapshotStore, eventStore: EventStore, numberOfEventsThatTriggersASnapshot: Int) {

 def reconstitute(entityId: EntityId): Entity =
  snapshotStore.retrieve(entityId) match {
   case Some(snapshot) => reconstituteWith(snapshot)
   case None => reconstituteFromEventStore(entityId)
  }

 def reconstituteFromEventStore(entityId: EntityId, offset: Int = 0): Entity = {
  val events = eventStore.consume(entityId, offset)
    .map(_.value)
  val state = events
    .foldLeft(0)(_ + _)
  saveSnapshotIfNeeded(state, offset, events.size, entityId)
  Entity(state)
 }

 def reconstituteWith(snapshot: Snapshot) = {
  val stateFromEventStore = reconstituteFromEventStore(snapshot.entityId, snapshot.offset)
  Entity(stateFromEventStore.value + snapshot.value)
 }

 def saveSnapshotIfNeeded(state: Int, offset: Int, numberOfEvents: Int, entityId: EntityId) = {
  if(numberOfEvents >= numberOfEventsThatTriggersASnapshot) {
   snapshotStore.save(Snapshot(offset + numberOfEvents, state, entityId))
  }
 }
}
