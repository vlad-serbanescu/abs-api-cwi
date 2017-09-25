package nqueenscoop

import abs.api.cwi
import abs.api.cwi._
import common.FastFunctions

class Worker(var master: IMaster, var threshold: Int, var size: Int) extends LocalActor with IWorker {
  {
    println("Worker started")
  }

  def nqueensKernelPar(board: Array[Int], depth: Int, priority: Int): ABSFuture[List[Array[Int]]] = {
//    println(s"Par $depth $size $priority ${board.length} $this")
    if (size != depth) {
      if (depth >= threshold) {
        ABSFuture.of(this.nqueensKernelSeq(board, depth).toList)
      } else {
        val newDepth: Int = depth + 1
        var i: Int = 0
        var futures: List[ABSFuture[List[Array[Int]]]] = List[ABSFuture[List[Array[Int]]]]()
        while (i < size) {
          val b: Array[Int] = new Array[Int](newDepth)
          System.arraycopy(board, 0, b, 0, depth)
          b(depth) = i
          if (FastFunctions.boardValid(b,newDepth)) {
            val fut: ABSFuture[List[Array[Int]]] = master.send(() => master.sendWork(b, newDepth, priority - 1))
            futures = fut +: futures
          }
          i += 1
        }
        val seqFut: ABSFuture[List[List[Array[Int]]]] = cwi.ABSFutureSugar.sequence(futures)
        getSpawn(seqFut, (list: List[List[Array[Int]]]) => {
          ABSFuture.of(list.flatten)
        })
      }
    } else {
      ABSFuture.of(List(board))
    }
  }

  def nqueensKernelSeq(board: Array[Int], depth: Int): Vector[Array[Int]] = {
//    println(s"Seq $depth $this")
    if (size != depth) {
      var result = Vector[Array[Int]]()
      val b: Array[Int] = new Array[Int](depth + 1)

      var i: Int = 0
      while (i < size) {
        System.arraycopy(board, 0, b, 0, depth)
        b(depth) = i
        if (FastFunctions.boardValid(b, depth + 1)) {
          result = nqueensKernelSeq(b, depth + 1) ++ result
        }
        i += 1
      }
      result
    } else {
      Vector(board) // solution
    }
  }
}
