case class Event(value: Int)

class EventStore {
 def consume(entityId: EntityId, offset: Int): List[Event] = ???

}
