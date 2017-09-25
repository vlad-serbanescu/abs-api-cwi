package nqueenscoop

import java.util

import abs.api.cwi._
import common.Functions

import scala.collection.JavaConverters._

class Worker(var master: IMaster, var threshold: Int, var size: Int) extends LocalActor with IWorker {
  {
    println("Worker started")
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
          .filter(Functions.boardValid(_, newDepth))
          .map(b => {
            val value: ABSFuture[List[Array[Int]]] = master.send(() => master.sendWork(b, newDepth, priority - 1))
//            println(s"Sent $newDepth")
            value
          })
        val seqFut: ABSFuture[util.List[List[Array[Int]]]] = ABSFuture.sequence(futures.asJava)
//        println(s"awaiting seqFut $seqFut")
        getSpawn(seqFut, (list: util.List[List[Array[Int]]]) => {
          val value = ABSFuture.of(list.asScala.flatten.toList)
//          println(s"sequence complete $depth $value $this")
          value
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
        .filter(Functions.boardValid(_, depth + 1))
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
