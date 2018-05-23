package SwElements;

import abs.api.cwi.Functions._;
import abs.api.cwi._;
import java.util.Objects;
import java.util.concurrent._;
import java.util.concurrent.atomic._;
import java.util.concurrent.locks._;
import java.util.function._;
import SwElements.Functions._;
import TrackElements._;
import TrackElements.Functions._;
import Train._;
import Train.Functions._;
import Graph._;
import Graph.Functions._;
import Stellwerk._;
import Stellwerk.Functions._;
import Util._;
import Util.Functions._;
import ABS.StdLib._;
import ABS.StdLib.Functions._;

class SpeedLimiterImpl(var destCOG : LocalActor,var anz : GeschwindigkeitsAnzeiger,var vanz : GeschwindigkeitsVorAnzeiger,var m2000 : ContactMagnet,var m1000 : Magnet,var distance : Double) extends LocalActor with SpeedLimiter {
  private final var serialVersionUID : java.lang.Long =  1L;


  var limit : Int =  -1;

  def advance( r : Double): ABSFuture[Void]= {
    var f_w1r : Double = r;
    if(Array(r,r)(0)<=0) {
      return this.SpeedLimiterImpladvanceAwait0(f_w1r);
    }
    else {
      return spawn(Guard.convert(Array(r,r)),()=>this.SpeedLimiterImpladvanceAwait0(f_w1r));
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setLimit( i : Int): ABSFuture[Void]= {
    this.limit = i;
    this.anz.setAllowed(i);
    this.vanz.setAllowed(i);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def passed( m : Magnet): ABSFuture[Void]= {
    if (((Objects.equals(m, this.m2000)) && (this.limit > 0))) {
      var msg_1603121412271 : Callable[ABSFuture[Void]] = () => this.m1000.activate();
      this.m1000.send(msg_1603121412271);
      var delay : Double =  (this.distance / this.limit);
;
      var f_w1delay : Double = delay;
      var f_w1m : Magnet = m;
      if(Array(delay,delay)(0)<=0) {
        return this.SpeedLimiterImplpassedAwait0(f_w1delay, f_w1m);
      }
      else {
        return spawn(Guard.convert(Array(delay,delay)),()=>this.SpeedLimiterImplpassedAwait0(f_w1delay, f_w1m));
      }
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }


def SpeedLimiterImpladvanceAwait0( r : Double): ABSFuture[Void]= {
  var w_1$r : Double =  r;
  return ABSFuture.done();
}
def SpeedLimiterImplpassedAwait0( delay : Double,  m : Magnet): ABSFuture[Void]= {
  var w_1$delay : Double =  delay;
  var w_1$m : Magnet =  m;
  var msg_1603121412472 : Callable[ABSFuture[Void]] = () => this.m1000.deactivate();
  this.m1000.send(msg_1603121412472);
  return ABSFuture.done();
}
{
    moveToCOG(destCOG);
  }

}
