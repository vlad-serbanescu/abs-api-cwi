package NQueens.confNoLoadBalancing

import abs.api.cwi.ABSFutureSugar.VoidFuture
import abs.api.cwi._

trait IMaster extends Actor {
  def sendWork( list : Array[Int],  depth : Int,  priorities : Int): VoidFuture
  def success(solution: Array[Int]): VoidFuture
}
