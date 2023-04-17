import org.apache.spark.sql.streaming.{GroupState, GroupStateTimeout}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object MainTest extends  App{
  // val node1 = new StreamingNode("node1","iot-data")
  // //node1.run()
  val parent = new NotificationNode("parent", "https://maker.ifttt.com/trigger/scala_event/json/with/key/cyMr3y7V3Np-gzMAhWE8HM")
  val child1 = new NotificationNode("child1", "")
  val child2 = new NotificationNode("child2", "")
  child1.setParent(parent)
  child2.setParent(parent)
  
  val node2 = new StreamingNode("node2", "Test", child1, child2)
  //val parent = new NotificationNode("parent", "https://maker.ifttt.com/trigger/scala_event/json/with/key/cyMr3y7V3Np-gzMAhWE8HM")
  // node2.child1.setParent(parent)
  // node2.child2.setParent(parent)
  //val a = new Thread{ for (i <- 0 to 100 ) {println(parent.trueCount);Thread.sleep(5000) }; }
  //val parent = new NotificationNode("parent")
  node2.run()
  
   def stateUpdateFunction(node: NotificationNode, data: Iterator[String], state: GroupState[NodeState]): NodeState = {
    val currentState = state.getOption.getOrElse(NodeState(0))
    val newTrueCount = data.count(_.toDouble > 5.0)
    val newState = currentState.copy(trueCount = currentState.trueCount + newTrueCount)
    state.update(newState)
    node.trueCount = newState.trueCount
    newState
  }
  
  

  //node1.run()
  // node2.run()
  
  


  

}
