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

abstract class SignalState extends Ordered[SignalState] {
  private final var serialVersionUID : java.lang.Long =  1L;

  var rank : Int;
}
case class FAHRT() extends SignalState {
  final var rank : Int =  0;
  def compare( that : SignalState): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class STOP() extends SignalState {
  final var rank : Int =  1;
  def compare( that : SignalState): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class SLOW() extends SignalState {
  final var rank : Int =  2;
  def compare( that : SignalState): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class NOSIG() extends SignalState {
  final var rank : Int =  3;
  def compare( that : SignalState): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class INVALID() extends SignalState {
  final var rank : Int =  4;
  def compare( that : SignalState): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
