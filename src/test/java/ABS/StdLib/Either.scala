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

abstract class Either[A<%Ordered[_ >: A], B<%Ordered[_ >: B]] extends Ordered[Either[A, B]] {
  private final var serialVersionUID : java.lang.Long =  1L;

  var rank : Int;
}
case class Left[A<%Ordered[_ >: A], B<%Ordered[_ >: B]](var left : A) extends Either[A, B] {
  final var rank : Int =  0;
  def compare( that : Either[A, B]): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Right[A<%Ordered[_ >: A], B<%Ordered[_ >: B]](var right : B) extends Either[A, B] {
  final var rank : Int =  1;
  def compare( that : Either[A, B]): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
