package ABS.StdLib;

import abs.api.cwi.Functions._;
import abs.api.cwi._;
import java.util.Objects;
import java.util.concurrent._;
import java.util.concurrent.atomic._;
import java.util.concurrent.locks._;
import java.util.function._;
import ABS.StdLib.Functions._;
import ABS.StdLib.Exceptions._;
import ABS.StdLib.Exceptions.Functions._;

abstract class Map[A<%Ordered[_ >: A], B<%Ordered[_ >: B]] extends Ordered[Map[A, B]] {
  private final var serialVersionUID : java.lang.Long =  1L;

  var rank : Int;
}
case class EmptyMap[A<%Ordered[_ >: A], B<%Ordered[_ >: B]]() extends Map[A, B] {
  final var rank : Int =  0;
  def compare( that : Map[A, B]): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class InsertAssoc[A<%Ordered[_ >: A], B<%Ordered[_ >: B]](var pair0 : Pair[A, B],var map1 : Map[A, B]) extends Map[A, B] {
  final var rank : Int =  1;
  def compare( that : Map[A, B]): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
