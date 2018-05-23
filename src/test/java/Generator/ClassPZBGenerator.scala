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

class ClassPZBGenerator(var destCOG : LocalActor,var train : Train,var pGen : PhysicsGenerator,var name : String) extends LocalActor with PZBGenerator {
  private final var serialVersionUID : java.lang.Long =  1L;


  var state : PZBState =  LastNone();

  var last1000 : Double =  -1;

  var last1000Time : Time =  TimeOf(-1);

  var last500 : Double =  -1;

  var last500Time : Time =  TimeOf(-1);

  def getState(): ABSFuture[PZBState]= {
    return ABSFuture.done(this.state);
  }

  def getNextEvents( frontNext : Double,  backNext : Double,  fahrCounter : Int,  emergCount : Int,  dist : Double): ABSFuture[List[GenEvent]]= {
    var l : List[GenEvent] =  Nil[GenEvent]();
;
    if ((this.last1000 >= 0)) {
      var nextUp1000 : GenEvent = this.pGen.getNextUpEvent(frontNext, backNext, (1250 - (dist - this.last1000)), fahrCounter, emergCount, true).getOrNull();
;
      l = Cons(nextUp1000, l);
    }
    if ((this.last500 >= 0)) {
      var nextUp500 : GenEvent = this.pGen.getNextUpEvent(frontNext, backNext, (200 - (dist - this.last500)), fahrCounter, emergCount, true).getOrNull();
;
      l = Cons(nextUp500, l);
    }
    var v : Double = this.pGen.getV().getOrNull();
;
    var acc : AccelState = this.pGen.getAccelState().getOrNull();
;
    var accel : Double = this.pGen.getAccel().getOrNull();
;
    var break : Double = this.pGen.getBreak().getOrNull();
;
    var emerg : Double = this.pGen.getEmerg().getOrNull();
;
    var n : Time =  now();
;
    var distTo1000Const : Double =  acc match  {
        case Break(_) => ((((235 / 10) * (235 / 10)) - (v * v)) / (2 * break));
        case Emergency() => ((((235 / 10) * (235 / 10)) - (v * v)) / (2 * emerg));
        case Accel(_) => ((((235 / 10) * (235 / 10)) - (v * v)) / (2 * accel));
        case Stable() => if ((v <= (235 / 10)))  (1 / 10) else 100000000000;
        }
        ;
;
    var distTo500Const : Double =  acc match  {
        case Break(_) => ((((125 / 10) * (125 / 10)) - (v * v)) / (2 * break));
        case Emergency() => ((((125 / 10) * (125 / 10)) - (v * v)) / (2 * emerg));
        case Accel(_) => ((((125 / 10) * (125 / 10)) - (v * v)) / (2 * accel));
        case Stable() => if ((v <= (125 / 10)))  (1 / 10) else 100000000000;
        }
        ;
;
    var a : Double =  acc match  {
        case Break(vfinns) => break;
        case Emergency() => emerg;
        case Accel(vfinns) => accel;
        case Stable() => 0;
        }
        ;
;
    var b : Double =  (-1512 / 10000);
;
    var nn : Double =  (359 / 10000);
;
    var intern : Double =  ((v * v) - ((a / nn) * (36 + (a / nn))));
;
    var distTo500Slope : Double =  -1;
;
    if ((intern >= 0)) {
      distTo500Slope = (((in_sqrt(intern) + (a / nn)) - 18) / nn);
    }
    var hit1000Parabola : GenEvent =  NoGenEvent();
;
    var distTo1000Parabola : Double =  -1;
;
    if ((!Objects.equals(a, -1))) {
      var timeTo1000Parabola : Double =  ((46 - v) / (a + 1));
;
      distTo1000Parabola = ((v * timeTo1000Parabola) + ((((1 / 2) * a) * timeTo1000Parabola) * timeTo1000Parabola));
    }
    if ((((this.last500 > 0) && ((dist - this.last500) < 153)) && (distTo500Slope >= 0))) {
      var hit500Slope : GenEvent = this.pGen.getNextEmergencyEvent(frontNext, backNext, distTo500Slope, fahrCounter, emergCount).getOrNull();
;
      l = Cons(hit500Slope, l);
    }
    else {
      if ((((this.last500 > 0) && (this.last500 >= 153)) && (distTo500Const >= 0))) {
        var hit500Const : GenEvent = this.pGen.getNextEmergencyEvent(frontNext, backNext, distTo500Const, fahrCounter, emergCount).getOrNull();
;
        l = Cons(hit500Const, l);
      }
    }
    if ((((timeValue(this.last1000Time) > 0) && ((timeValue(now()) - timeValue(this.last1000Time)) < 23)) && (distTo1000Parabola >= 0))) {
      if ((!Objects.equals(a, -1))) {
        hit1000Parabola = this.pGen.getNextEmergencyEvent(frontNext, backNext, distTo1000Parabola, fahrCounter, emergCount).getOrNull();
;
        l = Cons(hit1000Parabola, l);
      }
    }
    else {
      if ((((timeValue(this.last1000Time) > 0) && ((timeValue(now()) - timeValue(this.last1000Time)) >= 23)) && (distTo1000Const >= 0))) {
        var hit1000Const : GenEvent = this.pGen.getNextEmergencyEvent(frontNext, backNext, distTo1000Const, fahrCounter, emergCount).getOrNull();
;
        l = Cons(hit1000Const, l);
      }
    }
    return ABSFuture.done(l);
  }

  def setState( nV : PZBState,  dist : Double): ABSFuture[Void]= {
    this.state = nV;
println(("ASD: state: " + (nV).toString()));
    if ((Objects.equals(nV, Last1000()))) {
      this.last1000 = dist;
      this.last1000Time = now();
    }
    if ((Objects.equals(nV, Last500()))) {
      this.last500 = dist;
      this.last500Time = now();
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def oneLess(): ABSFuture[Void]= {
    if ((Objects.equals(this.state, Last500()))) {
      this.state = Last1000();
    }
    else {
      if ((Objects.equals(this.state, Last1000()))) {
        this.state = LastNone();
      }
      else {
println("Error when leaving PZB!");
      }
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }


{
    moveToCOG(destCOG);
  }

}
