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

abstract class NextEvent extends Ordered[NextEvent] {
  private final var serialVersionUID : java.lang.Long =  1L;

  var rank : Int;
}
case class Ev(var moment : Double,var ll : Double,var newState : AccelState,var vnew : Double,var counter : Int,var fahrCount : Int,var position : Pos,var start : Time,var vold : Double,var pzbOneLess : Boolean) extends NextEvent {
  final var rank : Int =  0;
  def compare( that : NextEvent): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class NoEvent() extends NextEvent {
  final var rank : Int =  1;
  def compare( that : NextEvent): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
