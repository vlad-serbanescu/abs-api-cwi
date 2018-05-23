package Util;

import abs.api.cwi.Functions._;
import abs.api.cwi._;
import java.util.Objects;
import java.util.concurrent._;
import java.util.concurrent.atomic._;
import java.util.concurrent.locks._;
import java.util.function._;
import Util.Functions._;
import TrackElements._;
import TrackElements.Functions._;
import SwElements._;
import SwElements.Functions._;
import Stellwerk._;
import Stellwerk.Functions._;
import ABS.StdLib._;
import ABS.StdLib.Functions._;

abstract class AccelState extends Ordered[AccelState] {
  private final var serialVersionUID : java.lang.Long =  1L;

  var rank : Int;
}
case class Break(var double0 : Double) extends AccelState {
  final var rank : Int =  0;
  def compare( that : AccelState): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Emergency() extends AccelState {
  final var rank : Int =  1;
  def compare( that : AccelState): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Accel(var double1 : Double) extends AccelState {
  final var rank : Int =  2;
  def compare( that : AccelState): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Stable() extends AccelState {
  final var rank : Int =  3;
  def compare( that : AccelState): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
