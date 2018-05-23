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

abstract class Triple[A<%Ordered[_ >: A], B<%Ordered[_ >: B], C<%Ordered[_ >: C]] extends Ordered[Triple[A, B, C]] {
  private final var serialVersionUID : java.lang.Long =  1L;

  var rank : Int;
}
case class TripleOf[A<%Ordered[_ >: A], B<%Ordered[_ >: B], C<%Ordered[_ >: C]](var fstT : A,var sndT : B,var trdT : C) extends Triple[A, B, C] {
  final var rank : Int =  0;
  def compare( that : Triple[A, B, C]): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
