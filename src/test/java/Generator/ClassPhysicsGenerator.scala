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

class ClassPhysicsGenerator(var destCOG : LocalActor,var train : Train,var name : String) extends LocalActor with PhysicsGenerator {
  private final var serialVersionUID : java.lang.Long =  1L;


  var v : Double =  0;

  var accelState : AccelState =  Stable();

  var length : Int =  70;

  var force : Int =  145;

  var weight : Int =  125;

  var accel : Double =  1;

  var emerg : Double =  (-116 / 100);

  var break : Double =  (-80 / 100);

  var vmax : Double =  38;

  var vreise : Double =  38;

  def getBreak(): ABSFuture[Double]= {
    return ABSFuture.done(this.break);
  }

  def getEmerg(): ABSFuture[Double]= {
    return ABSFuture.done(this.emerg);
  }

  def getAccel(): ABSFuture[Double]= {
    return ABSFuture.done(this.accel);
  }

  def getReise(): ABSFuture[Double]= {
    return ABSFuture.done(this.vreise);
  }

  def getAccelState(): ABSFuture[AccelState]= {
    return ABSFuture.done(this.accelState);
  }

  def getV(): ABSFuture[Double]= {
    return ABSFuture.done(this.v);
  }

  def setV( nV : Double): ABSFuture[Void]= {
    this.v = nV;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setAccelMax(): ABSFuture[Void]= {
    this.accelState = Accel(this.vmax);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setAccelBreakNull(): ABSFuture[Void]= {
    this.accelState = Break(0);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setAccelEmergency(): ABSFuture[Void]= {
    this.accelState = Emergency();
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def handleLimitEv( x : Double): ABSFuture[Void]= {
    if ((x > this.vmax)) {
    }
    if ((this.v <= x)) {
      this.accelState = this.accelState match  {
case Break(vnew) => Accel(x);
case Accel(vnew) => if ((vnew > x))  Accel(x) else this.accelState;
case Stable() => Accel(x);
case _ => this.accelState;
}
;
    }
    else {
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def handleLimitPrepareEv( x : Double): ABSFuture[Void]= {
    if ((x > this.vmax)) {
    }
    if ((this.v <= x)) {
      this.accelState = this.accelState match  {
case Break(vnew) => if ((vnew > x))  Break(x) else this.accelState;
case Accel(vnew) => if ((vnew > x))  Accel(x) else this.accelState;
case _ => this.accelState;
}
;
    }
    else {
      this.accelState = this.accelState match  {
case Break(vnew) => if ((vnew > x))  Break(x) else this.accelState;
case Accel(vnew) => Break(x);
case Stable() => Break(x);
case _ => this.accelState;
}
;
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def handleEv( ev : NextEvent): ABSFuture[Double]= {
    this.accelState = newState(ev);
    this.v = vnew(ev);
println(((((("SPP: " + (this.v).toString()) + " - ") + (this.accelState).toString()) + " - ") + (now()).toString()));
    return ABSFuture.done(this.v);
  }

  def acqStop(): ABSFuture[Void]= {
    if(new Supplier[Boolean] {
  def get(): Boolean= {
    return (Objects.equals(v, 0));
  }
}
.get()) {
      return this.ClassPhysicsGeneratoracqStopAwait0();
    }
    else {
      return spawn(Guard.convert(new Supplier[Boolean] {
            def get(): Boolean= {
              return (Objects.equals(v, 0));
            }
          }
          ),()=>this.ClassPhysicsGeneratoracqStopAwait0());
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def getNextChangeTime( acc : AccelState): ABSFuture[Double]= {
    return ABSFuture.done(truncate(acc match  {
        case Break(vfinns) => (((vfinns * vfinns) - (this.v * this.v)) / (2 * this.break));
        case Emergency() => (( - (this.v * this.v)) / (2 * this.emerg));
        case Accel(vfinns) => (((vfinns * vfinns) - (this.v * this.v)) / (2 * this.accel));
        case Stable() => (100000000 / 1);
        }
        ));
  }

  def getNextEvents( frontNext : Double,  backNext : Double,  fahrCounter : Int,  emergCounter : Int,  dist : Double): ABSFuture[List[GenEvent]]= {
    var changeNext : Double = this.getNextChangeTime(this.accelState).getOrNull();
;
    var l : List[GenEvent] =  Nil[GenEvent]();
;
    if (((changeNext <= backNext) && (changeNext <= frontNext))) {
      var ev1 : GenEvent = this.getNextChangeEvent(frontNext, backNext, changeNext, fahrCounter, emergCounter, false).getOrNull();
;
      l = Cons(ev1, l);
    }
    else {
      if (((frontNext <= backNext) && (frontNext <= changeNext))) {
        var ev2 : GenEvent = this.getNextFrontEvent(frontNext, backNext, changeNext, fahrCounter, emergCounter).getOrNull();
;
        l = Cons(ev2, l);
      }
      else {
        if (((backNext <= frontNext) && (backNext <= changeNext))) {
          var ev3 : GenEvent = this.getNextBackEvent(frontNext, backNext, changeNext, fahrCounter, emergCounter).getOrNull();
;
          l = Cons(ev3, l);
        }
      }
    }
    return ABSFuture.done(l);
  }

  def getNextEmergencyEvent( frontNext : Double,  backNext : Double,  changeNext : Double,  fahrCounter : Int,  emergCount : Int): ABSFuture[GenEvent]= {
    var vnew : Double =  in_short(this.v);
;
    var timeNext : Double =  0;
;
    this.accelState match  {
      case Break(vfin) => timeNext = ((2 * changeNext) / (this.v + vfin));
          vnew = (this.v + (this.break * timeNext));
          ;
      case Emergency() => timeNext = ((2 * changeNext) / this.v);
          vnew = (this.v + (this.emerg * timeNext));
          ;
      case Accel(vfin) => timeNext = ((2 * changeNext) / (this.v + vfin));
          vnew = (this.v + (this.accel * timeNext));
          ;
      case Stable() => timeNext = (changeNext / this.v);
          ;
      case _ => ;
    }
    return ABSFuture.done(ChangeEv(Ev(timeNext, changeNext, Emergency(), vnew, emergCount, fahrCounter, Middle(), now(), this.v, false), true));
  }

  def getNextUpEvent( frontNext : Double,  backNext : Double,  changeNext : Double,  fahrCounter : Int,  emergCount : Int,  up : Boolean): ABSFuture[GenEvent]= {
    var vnew : Double =  in_short(this.v);
;
    var timeNext : Double =  0;
;
    this.accelState match  {
      case Break(vfin) => timeNext = ((2 * changeNext) / (this.v + vfin));
          vnew = (this.v + (this.break * timeNext));
          ;
      case Emergency() => timeNext = ((2 * changeNext) / this.v);
          vnew = (this.v + (this.emerg * timeNext));
          ;
      case Accel(vfin) => timeNext = ((2 * changeNext) / (this.v + vfin));
          vnew = (this.v + (this.accel * timeNext));
          ;
      case Stable() => timeNext = (changeNext / this.v);
          ;
      case _ => ;
    }
    var ret : GenEvent =  NoGenEvent();
;
    if ((timeNext > 0)) {
      ret = ChangeEv(Ev(timeNext, changeNext, this.accelState, vnew, emergCount, fahrCounter, Middle(), now(), this.v, up), false);
    }
    return ABSFuture.done(ret);
  }

  def getNextChangeEvent( frontNext : Double,  backNext : Double,  changeNext : Double,  fahrCounter : Int,  emergCount : Int,  up : Boolean): ABSFuture[GenEvent]= {
    var vnew : Double =  in_short(this.v);
;
    var timeNext : Double =  0;
;
    this.accelState match  {
      case Break(vfin) => timeNext = ((vfin - this.v) / this.break);
          vnew = vfin;
          ;
      case Emergency() => timeNext = ( - (this.v / this.emerg));
          vnew = 0;
          ;
      case Accel(vfin) => timeNext = ((vfin - this.v) / this.accel);
          vnew = vfin;
          ;
      case Stable() => timeNext = (changeNext / this.v);
          ;
      case _ => ;
    }
    return ABSFuture.done(ChangeEv(Ev(timeNext, changeNext, Stable(), vnew, emergCount, fahrCounter, Middle(), now(), this.v, up), false));
  }

  def getNextFrontEvent( frontNext : Double,  backNext : Double,  changeNext : Double,  fahrCounter : Int,  emergCount : Int): ABSFuture[GenEvent]= {
    var vnew : Double =  this.accelState match  {
        case Break(vfin) => in_short(in_sqrt(abs(((this.v * this.v) + ((2 * this.break) * frontNext)))));
        case Emergency() => in_short(in_sqrt(abs(((this.v * this.v) + ((2 * this.emerg) * frontNext)))));
        case Accel(vfin) => in_short(in_sqrt(abs(((this.v * this.v) + ((2 * this.accel) * frontNext)))));
        case Stable() => this.v;
        }
        ;
;
    var timeNext : Double =  ((2 * frontNext) / (vnew + this.v));
;
    return ABSFuture.done(FrontEv(Ev(timeNext, frontNext, this.accelState, vnew, emergCount, fahrCounter, Front(), now(), this.v, false)));
  }

  def getNextBackEvent( frontNext : Double,  backNext : Double,  changeNext : Double,  fahrCounter : Int,  emergCount : Int): ABSFuture[GenEvent]= {
    var vnew : Double =  this.accelState match  {
        case Break(vfin) => in_short(in_sqrt(abs(((this.v * this.v) + ((2 * this.break) * backNext)))));
        case Emergency() => in_short(in_sqrt(abs(((this.v * this.v) + ((2 * this.emerg) * backNext)))));
        case Accel(vfin) => in_short(in_sqrt(abs(((this.v * this.v) + ((2 * this.accel) * backNext)))));
        case Stable() => this.v;
        }
        ;
;
    var timeNext : Double =  ((2 * backNext) / (vnew + this.v));
;
    return ABSFuture.done(BackEv(Ev(timeNext, backNext, this.accelState, vnew, emergCount, fahrCounter, Back(), now(), this.v, false)));
  }


def ClassPhysicsGeneratoracqStopAwait0(): ABSFuture[Void]= {
  return ABSFuture.done();
}
{
    moveToCOG(destCOG);
  }

}
