package Generator;

import abs.api.cwi.Functions._;
import abs.api.cwi._;
import java.util.Objects;
import java.util.concurrent._;
import java.util.concurrent.atomic._;
import java.util.concurrent.locks._;
import java.util.function._;
import Generator.Functions._;
import Graph._;
import Graph.Functions._;
import Util._;
import Util.Functions._;
import TrackElements._;
import TrackElements.Functions._;
import Stellwerk._;
import Stellwerk.Functions._;
import Run._;
import Run.Functions._;
import Train._;
import Train.Functions._;
import ABS.StdLib._;
import ABS.StdLib.Functions._;

abstract class PZBState extends Ordered[PZBState] {
  private final var serialVersionUID : java.lang.Long =  1L;

  var rank : Int;
}
case class Last1000() extends PZBState {
  final var rank : Int =  0;
  def compare( that : PZBState): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Last500() extends PZBState {
  final var rank : Int =  1;
  def compare( that : PZBState): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class LastNone() extends PZBState {
  final var rank : Int =  2;
  def compare( that : PZBState): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
