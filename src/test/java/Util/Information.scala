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

abstract class Information extends Ordered[Information] {
  private final var serialVersionUID : java.lang.Long =  1L;

  var rank : Int;
}
case class NoInfo() extends Information {
  final var rank : Int =  0;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class TestInfo() extends Information {
  final var rank : Int =  1;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Info(var si : SignalState) extends Information {
  final var rank : Int =  2;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Prepare(var sp : SignalState) extends Information {
  final var rank : Int =  3;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class StartPrepare(var spp : SignalState) extends Information {
  final var rank : Int =  4;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Limit(var int0 : Int) extends Information {
  final var rank : Int =  5;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class LimitPrepare(var int1 : Int) extends Information {
  final var rank : Int =  6;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class WeichenbereichVerlassen() extends Information {
  final var rank : Int =  7;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Crash() extends Information {
  final var rank : Int =  8;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class ChangeResp(var zugfolge2 : ZugFolge) extends Information {
  final var rank : Int =  9;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Mhz1000() extends Information {
  final var rank : Int =  10;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Mhz2000() extends Information {
  final var rank : Int =  11;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
case class Mhz500() extends Information {
  final var rank : Int =  12;
  def compare( that : Information): Int= {
    if(this.rank == that.rank) {
      return 0;
    }
    else {
      return this.rank-that.rank;
    }
  }
}
