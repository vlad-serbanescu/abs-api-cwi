package coopNoLoadBalancing

import abs.api.cwi._


class Master(var numWorkers : Int,var priorities : Int,var solutionsLimit : Int,var threshold : Int,var size : Int) extends LocalActor with IMaster {

  private val t1 = System.currentTimeMillis()

  def sendWork(list: Array[Int], depth: Int, priorities: Int): ABSFuture[List[Array[Int]]] = {
//    println(s"Work $depth")
    val worker = new Worker(threshold, size)
    worker.send(() => worker.nqueensKernelPar(list, depth, priorities))
  }

  def init: ABSFuture[Void] = {
    println(s"COOP NO-LB: Boardsize = ${size.toString}, number of solutions should be ${solutionsLimit.toString}")
    val inArray: Array[Int] = new Array[Int](0)
    val fut: ABSFuture[List[Array[Int]]] = this.send(() => this.sendWork(inArray, 0, priorities))
    getSpawn(fut, (result: List[Array[Int]]) => {
      println(s"Found ${result.size} solutions")
      println("-------------------------------- Program successfully completed! in " + (System.currentTimeMillis() - t1))
      ActorSystem.shutdown()
      ABSFuture.done()
    })
  }

  {
    this.send(() => this.init)
  }
}
