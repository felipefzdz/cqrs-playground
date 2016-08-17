import org.mockito.BDDMockito._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}


class StateBuilderShould extends WordSpec
  with Matchers
  with BeforeAndAfterAll
  with MockitoSugar {


 var stateBuilder: StateBuilder = _
 val snapshotStore: SnapshotStore = mock[SnapshotStore]
 val eventStore: EventStore = mock[EventStore]
 val entityId: EntityId = EntityId("1234")
 val numberOfEventsThatTriggersASnapshot = 3

 override protected def beforeAll() = {
  stateBuilder = new StateBuilder(snapshotStore, eventStore, numberOfEventsThatTriggersASnapshot)
 }

 "State Builder" when {

  "state is just contained in event store" should {
   "reconstitute state from the beginning" in {
    val offset = 0
    given(snapshotStore.retrieve(entityId)).willReturn(None)
    given(eventStore.consume(entityId, offset)).willReturn(List(Event(9), Event(-5)))
    stateBuilder.reconstitute(entityId).value shouldBe 4
   }
  }

  "state is contained in event and snapshot store" should {
   "reconstitute state from both stores" in {
    val offset = 2
    val snapshot = Snapshot(offset, 10, entityId)
    given(snapshotStore.retrieve(entityId)).willReturn(Some(snapshot))
    given(eventStore.consume(entityId, offset)).willReturn(List(Event(3), Event(-5)))
    stateBuilder.reconstitute(entityId).value shouldBe 8
   }
  }

  "the limit of events has been exceeded" should {
   "create a new snapshot" in {
    val offset = 3
    val value = 6
    val snapshot = Snapshot(offset, value, entityId)

    given(snapshotStore.retrieve(entityId)).willReturn(None)
    given(eventStore.consume(entityId, 0)).willReturn(List(Event(5), Event(6), Event(-5)))

    stateBuilder.reconstitute(entityId)

    verify(snapshotStore).save(snapshot)
   }
  }
 }

}
