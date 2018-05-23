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

abstract class Duration extends Ordered[Duration] {
  private final var serialVersionUID : java.lang.Long =  1L;

  var rank : Int;
}
case class DurationOf(var durationValue : Double) extends Duration {
  final var rank : Int =  0;
  def compare( that : Duration): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class InfDuration() extends Duration {
  final var rank : Int =  1;
  def compare( that : Duration): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
