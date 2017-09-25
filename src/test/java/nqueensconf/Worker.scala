package nqueensconf

import abs.api.cwi._
import common.Functions

class Worker(var master: IMaster, var threshold: Int, var size: Int) extends LocalActor with IWorker {
  {
    println("Worker started")
  }

  def nqueensKernelPar(board: Array[Int], depth: Int, priority: Int): ABSFuture[Void] = {
//    println(s"Par $depth $size $priority ${board.length}")
    if (size != depth) {
      if (depth >= threshold) {
        this.nqueensKernelSeq(board, depth)
      } else {
        val newDepth: Int = depth + 1
        (0 until size)
          .map(Functions.copyBoard(board, depth, _))
          .filter(Functions.boardValid(_, newDepth))
          .foreach(b => {
            master.send(() => master.sendWork(b, newDepth, priority - 1))
          })
      }
    } else {
      println("solution")
      board.foreach(print)
      master.send(() => master.success())
    }
    ABSFuture.done()
  }

  // internal method called only sequentially
  private def nqueensKernelSeq(board: Array[Int], depth: Int): Unit = {
//    println(s"Seq $depth")
    if (size != depth) {
      (0 until size)
        .map(Functions.copyBoard(board, depth, _))
        .filter(Functions.boardValid(_, depth + 1))
        .foreach(nqueensKernelSeq(_, depth + 1))
    } else {
      master.send(() => master.success())
    }
//    println(s"Seq $depth is done")
  }
}



//        var i: Int = 0
//        while (i < size) {
//          val b: Array[Int] = new Array[Int](newDepth)
//          System.arraycopy(board, 0, b, 0, depth)
//          b(depth) = i
//          if (Functions.boardValid(b, newDepth)) {
//            println(s"new depth: $newDepth")
//            master.send(() => master.sendWork(b, newDepth, priority - 1))
//          }
//          i += 1
//        }
