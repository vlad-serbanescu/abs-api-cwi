package confNoLoadBalancing

import abs.api.cwi.ABSFutureSugar.VoidFuture
import abs.api.cwi._
import abs.api.cwi.ABSFuture.done


class Master(var numWorkers : Int,var priorities : Int,var solutionsLimit : Int,var threshold : Int,var size : Int) extends LocalActor with IMaster {
  var result: List[Array[Int]] = List()
  var resultCounter = 0
  private val t1 = System.currentTimeMillis()

  def success(solution: Array[Int]): VoidFuture = {
    result = solution +: result
    resultCounter += 1
    if (solutionsLimit == resultCounter) {
      println(s"Found ${result.size} solutions")
      println("-------------------------------- Program successfully completed! in " + (System.currentTimeMillis() - t1))
      ActorSystem.shutdown()
    }
    done
  }

  def sendWork(list: Array[Int], depth: Int, priorities: Int): VoidFuture = {
//    println(s"Work $depth")
    val worker = new Worker(this, threshold, size)
    worker.send(() => worker.nqueensKernelPar(list, depth, priorities))
    done
  }

  override def init = {
    println(s"NON-COOP-NOLB: Boardsize = ${size.toString}, number of solutions should be ${solutionsLimit.toString}")
    val inArray: Array[Int] = new Array[Int](0)
    this.send(() => this.sendWork(inArray, 0, priorities))
  }
}
