import scala.collection.mutable._

class AggregateSnapshot {

 val stacks = ListBuffer.empty[Stack]

 def addStack(stack: Stack): Unit = {
  stacks += stack
 }

 case class Stack(stackId: String, content: String)

}
