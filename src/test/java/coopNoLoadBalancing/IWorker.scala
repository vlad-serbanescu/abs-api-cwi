package coopNoLoadBalancing

import abs.api.cwi._

trait IWorker extends Actor {
  def nqueensKernelPar( list : Array[Int],  depth : Int,  priority : Int): ABSFuture[List[Array[Int]]]
}
