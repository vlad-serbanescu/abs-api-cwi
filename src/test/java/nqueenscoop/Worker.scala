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
    println(s"Par $depth $size $priority ${board.length}")
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
            println(s"Sent $newDepth")
            value
          })
        val seqFut: ABSFuture[util.List[List[Array[Int]]]] = ABSFuture.sequence(futures.asJava)
        getSpawn(seqFut, (list: util.List[List[Array[Int]]]) => {
          println(s"sequence complete $depth")
          ABSFuture.of(list.asScala.flatten.toList)
        })
      }
    } else {
      ABSFuture.of(List(board))
    }
  }

  def nqueensKernelSeq(board: Array[Int], depth: Int): List[Array[Int]] = {
    println(s"Seq $depth")
    if (size != depth) {
      (0 until size)
        .map(Functions.copyBoard(board, depth, _))
        .filter(Functions.boardValid(_, depth + 1))
        .flatMap(nqueensKernelSeq(_, depth + 1))
        .toList
    } else {
      println(s"solution ${board.toList}")
      List(board) // solution
    }
  }
}
