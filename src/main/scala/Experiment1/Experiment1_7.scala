import scala.util.hashing.MurmurHash3
import scala.util.matching.Regex
import com.han.ATree
object Experiment1_7 {
  def generateID(_expression: String): Long = {
    // val predicatesAndOperators: List[Char] = _expression.toList
    // var id: Int = 0
    
    
    // for (char <- predicatesAndOperators) {
    //   id += char.hashCode() * char.hashCode()
    // }
    val seed = 0 // Seed value for the hash function
    val components = _expression.split('^').sorted.mkString("^") // Sort components for consistent ordering
    val hash:Long = MurmurHash3.stringHash(components, seed)

    hash

    //id
  }

  def main(args: Array[String]): Unit = {
    val tree = new ATree("Experiment_1")
    val query1 = "P1^P2^P3^P4^P5^P6^P7"
    val query2 = "Seq(P1,P2,P3,P4,P5,P6,P7)"
    val query3 = "Seq(P1,P2,P3)^P4^Seq(P5,P6)^P7"
    val query4 = "Seq(P1,P2,P4)^P3^Seq(P5,P6,P7)^P8"
    val query5 = "P1^Seq(P2,P3)^P4^P5^P6^P7^P8"
    val query6 = "Seq(P1,P2,P4)^P3^Seq(P5,P6,P8)^P7^P9"
    
    //val query3 = "Seq(P1,P2,P3)^P4^Seq(P5,P6)^P7"
    
    
    //please help me generate 100 random queries like above and add them to query_set
    
    val query_set = Array(query1, query2, query3, query4, query5, query6)
    val startTime = System.currentTimeMillis()
    query_set.foreach(x => tree.add_query(x))
    // tree.add_query(query1)
    // tree.add_query(query2)
    // tree.add_query(query3)


    //tree.hen.foreach(x => tree.checkNodeChildsParent(x._2))
    tree.from_hen_collect_leaf_Node_to_ArrayBuffer(tree.hen, tree.leafNodeArrayBuffer)
    val endTime = System.currentTimeMillis()
    println("------------------Start----------------------------")
    // tree.leafNodeArrayBuffer.foreach(x => println(x.expression))
    // val regex = new Regex("([A-Za-z]+)[<>]\\d+")
    // val groupMap = tree.leafNodeArrayBuffer.groupBy(x => x.expression.takeWhile(_ != '>').takeWhile(_ != '<'))
    // val groupMap2 = groupMap.map(x => (x._1, x._2.map(_.expression)))
    // println(groupMap2)
    // println("-----------------------")
    println(tree.hen.size +  "vvvvvvvvvvvvvvvvvvvvvvvvvv")
    val expcollectopn = tree.hen.map(x => x._2.expression).toList
    expcollectopn.sortBy(x => x.length).foreach(x => println(x))
    
    println("-----------------------")
    println(s"Program Run Time: ${endTime - startTime} ms")
    println(query1 + "'s childs: "  +tree.hen(generateID(query1)).childs)
    println(query2 + "'s childs: "  +tree.hen(generateID(query2)).childs)
    println(query3 + "'s childs: "  +tree.hen(generateID(query3)).childs)
    println(query4 + "'s childs: "  +tree.hen(generateID(query4)).childs)
    println(query5 + "'s childs: "  +tree.hen(generateID(query5)).childs)
    println(query6 + "'s childs: "  +tree.hen(generateID(query6)).childs)
    println("P1^P2^P3's childs: " + tree.hen(generateID("P1^P2^P3")).childs)
    println("P1^P2^P4's childs: " + tree.hen(generateID("P1^P2^P4")).childs)
    println("P2^P3's parent: " + tree.hen(generateID("P1^P2^P3")).parents)
    println("P5^P6^P7's childs: " + tree.hen(generateID("P5^P6^P7")).childs)
    println("Seq(P5,P6)'s childs: " + tree.hen(generateID("Seq(P5,P6)")).childs)
    println("Seq(P5,P6)'s parents: " + tree.hen(generateID("Seq(P5,P6)")).parents)
    println("P5^P6's parents: " + tree.hen(generateID("P5^P6")).parents)
    println("P5^P6^P8's childs: " + tree.hen(generateID("P5^P6^P8")).childs + "::::::::::::" + tree.hen(generateID("P5^P6^P8")).childExprs)
    println("P1^P2^P4's childs: "  +tree.hen(generateID("P1^P2^P4")).childs)
    println("P1^P2^P4's parents: "  +tree.hen(generateID("P1^P2^P4")).parents)
    println(query1 + "'s parents: "  +tree.hen(generateID(query1)).parents)
    println("P5^P6's parent: " + tree.hen(generateID("P5^P6")).parents)
    //println("P5^P1^P7^P4^P6' child:" + tree.hen(generateID("P5^P1^P7^P4^P6")).childs) 
    //println(query7 + "'s parent" + tree.hen(generateID(query7)).parents)
    //println("P4^P5^P6's childs: "  +tree.hen(generateID("P4^P5^P6")).childs)
    // println(generateID("P5^P6^P8"))
    // println(generateID("P5^P6"))
    println("all node size:" + tree.hen.size)
    println("no leaf node size" + tree.non_leaf_node.size)
    tree.non_leaf_node.foreach(x => println(x._2.expression))
    println("---new=----")
    tree.nodes_ancestors_expression_Map.foreach(x => println("Node" + x._1 + "'s ancestors: " + x._2.mkString(",")))

  }





}
