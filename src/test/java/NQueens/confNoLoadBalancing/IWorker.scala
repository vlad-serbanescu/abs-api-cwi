package NQueens.confNoLoadBalancing

import abs.api.cwi.ABSFutureSugar.VoidFuture
import abs.api.cwi._

trait IWorker extends Actor {
  def nqueensKernelPar( list : Array[Int],  depth : Int,  priority : Int): VoidFuture
}
