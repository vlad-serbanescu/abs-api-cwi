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

abstract class LocationType extends Ordered[LocationType] {
  private final var serialVersionUID : java.lang.Long =  1L;

  var rank : Int;
}
case class Far() extends LocationType {
  final var rank : Int =  0;
  def compare( that : LocationType): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Near() extends LocationType {
  final var rank : Int =  1;
  def compare( that : LocationType): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Somewhere() extends LocationType {
  final var rank : Int =  2;
  def compare( that : LocationType): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Infer() extends LocationType {
  final var rank : Int =  3;
  def compare( that : LocationType): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
