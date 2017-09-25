package coopNoLoadBalancing

import abs.api.cwi
import abs.api.cwi._
import common.{FastFunctions, Functions}

class Worker(var threshold: Int, var size: Int) extends LocalActor with IWorker {

  def sendWork(list: Array[Int], depth: Int, priorities: Int): ABSFuture[List[Array[Int]]] = {
    //    println(s"Work $depth")
    val worker = new Worker(threshold, size)
    worker.send(() => worker.nqueensKernelPar(list, depth, priorities))
  }

  def nqueensKernelPar(board: Array[Int], depth: Int, priority: Int): ABSFuture[List[Array[Int]]] = {
//    println(s"Par $depth $size $priority ${board.length} $this")
    if (size != depth) {
      if (depth >= threshold) {
        ABSFuture.of(this.nqueensKernelSeq(board, depth))
      } else {
        val newDepth: Int = depth + 1
        val futures = (0 until size)
          .map(Functions.copyBoard(board, depth, _))
          .filter(FastFunctions.boardValid(_, newDepth))
          .map(this.sendWork(_, newDepth, priority - 1))
        val seqFut: ABSFuture[List[List[Array[Int]]]] = cwi.ABSFutureSugar.sequence(futures)
//        println(s"awaiting seqFut $seqFut")
        getSpawn(seqFut, (list: List[List[Array[Int]]]) => {
          ABSFuture.of(list.flatten)
        })
      }
    } else {
      ABSFuture.of(List(board))
    }
  }

  def nqueensKernelSeq(board: Array[Int], depth: Int): List[Array[Int]] = {
    val outer = this
//    println(s"Seq $depth $this")
    val result = if (size != depth) {
//      println(s"S1 $this")
      (0 until size)
        .map(Functions.copyBoard(board, depth, _))
        .filter(FastFunctions.boardValid(_, depth + 1))
        .flatMap(nqueensKernelSeq(_, depth + 1))
        .toList

      /*
        .map(b => {val r = Functions.copyBoard(board, depth, b); println(s"mapped $b $r $outer"); r})
        .filter(b => {val r = Functions.boardValid(b, depth + 1); println(s"mapped $b $r $outer"); r})
        .flatMap(b => {val r = nqueensKernelSeq(b, depth + 1); println(s"mapped $b $r $outer"); r})
       */

    } else {
//      println(s"solution ${board.toList} $this")
      List(board) // solution
    }
//    println(s"Seq done. $result $this")
    result
  }
}
