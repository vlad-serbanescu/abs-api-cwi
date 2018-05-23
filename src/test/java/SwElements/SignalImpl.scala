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

class SignalImpl(var destCOG : LocalActor,var hs : HauptSignal,var mhz2000 : Magnet,var mhz500 : Magnet,var vs : VorSignal,var mhz1000 : Magnet,var sp : SichtbarkeitsPunkt,var name : String,var resp : ZugFolge) extends LocalActor with Signal {
  private final var serialVersionUID : java.lang.Long =  1L;


  var state : SignalState =  STOP();

  var free : Boolean =  false;

  var broken : Boolean =  false;

  var observedBy : Train =  null;

  def triggered(): ABSFuture[Void]= {
    if ((!Objects.equals(this.resp, null))) {
      var msg_605701856851 : Callable[ABSFuture[Void]] = () => this.resp.triggered(this);
      this.resp.send(msg_605701856851);
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def printName( prefix : String): ABSFuture[Void]= {
println(((prefix + " ") + this.name));
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def acqFree(): ABSFuture[Void]= {
    if(new Supplier[Boolean] {
  def get(): Boolean= {
    return (Objects.equals(state, FAHRT()));
  }
}
.get()) {
      return this.SignalImplacqFreeAwait0();
    }
    else {
      return spawn(Guard.convert(new Supplier[Boolean] {
            def get(): Boolean= {
              return (Objects.equals(state, FAHRT()));
            }
          }
          ),()=>this.SignalImplacqFreeAwait0());
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def acqHalt(): ABSFuture[Void]= {
    if(new Supplier[Boolean] {
  def get(): Boolean= {
    return (Objects.equals(state, STOP()));
  }
}
.get()) {
      return this.SignalImplacqHaltAwait0();
    }
    else {
      return spawn(Guard.convert(new Supplier[Boolean] {
            def get(): Boolean= {
              return (Objects.equals(state, STOP()));
            }
          }
          ),()=>this.SignalImplacqHaltAwait0());
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def getObserver(): ABSFuture[Train]= {
    if(new Supplier[Boolean] {
  def get(): Boolean= {
    return (!Objects.equals(observedBy, null));
  }
}
.get()) {
      return this.SignalImplgetObserverAwait0();
    }
    else {
      return spawn(Guard.convert(new Supplier[Boolean] {
            def get(): Boolean= {
              return (!Objects.equals(observedBy, null));
            }
          }
          ),()=>this.SignalImplgetObserverAwait0());
    }
  }

  def setObserver( obs : Train): ABSFuture[Void]= {
    this.observedBy = obs;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def swap( t : Time): ABSFuture[Void]= {
    if (this.free) {
      this.sperren(t);
    }
    else {
      this.freischalten();
    }
    this.free = ( ! this.free);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def break( t : Time): ABSFuture[Void]= {
    this.broken = true;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def breakNow(): ABSFuture[Void]= {
    this.broken = true;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def isFree(): ABSFuture[Boolean]= {
    var n : Node = this.hs.getNode().getOrNull();
;
    var fromEdge : Edge = this.hs.getWaitTrack().getOrNull();
;
    var next : Edge = n.getOut(fromEdge).getOrNull();
;
    var nextNode : Node = next.getTo(n).getOrNull();
;
    var fin : Boolean = nextNode.hasCrit().getOrNull();
;
    while (( ! fin)) {
      next = n.getOut(next).getOrNull();
;
      nextNode = next.getTo(nextNode).getOrNull();
;
      fin = nextNode.hasCrit().getOrNull();
;
    }
    return ABSFuture.done(true);
  }

  def freischalten(): ABSFuture[Boolean]= {
    this.hs.setState(FAHRT(), now());
    this.vs.setState(FAHRT(), now());
    this.sp.setState(FAHRT(), now());
    this.state = FAHRT();
    if ((!Objects.equals(this.mhz2000, null))) {
      this.mhz2000.deactivate();
    }
    if ((!Objects.equals(this.mhz500, null))) {
      this.mhz500.deactivate();
    }
    if ((!Objects.equals(this.mhz1000, null))) {
      this.mhz1000.deactivate();
    }
    if ((!Objects.equals(this.observedBy, null))) {
      var msg_2090407281125 : Callable[ABSFuture[Void]] = () => this.observedBy.notify(Info(FAHRT()), now());
      this.observedBy.send(msg_2090407281125);
    }
    return ABSFuture.done(true);
  }

  def sperren( t : Time): ABSFuture[Boolean]= {
    if (( ! this.broken)) {
      this.hs.setState(STOP(), t);
      this.vs.setState(STOP(), t);
      this.sp.setState(STOP(), t);
      if ((!Objects.equals(this.mhz2000, null))) {
        this.mhz2000.activate();
      }
      if ((!Objects.equals(this.mhz500, null))) {
        this.mhz500.activate();
      }
      if ((!Objects.equals(this.mhz1000, null))) {
        this.mhz1000.activate();
      }
      this.state = STOP();
    }
    else {
      var n : Node = this.hs.getNode().getOrNull();
;
println(((((("MSG;" + (n).toString()) + ";") + (timeValue(now())).toString()) + ";failure occured at ") + this.name));
    }
    return ABSFuture.done(this.broken);
  }


def SignalImplacqFreeAwait0(): ABSFuture[Void]= {
  return ABSFuture.done();
}
def SignalImplacqHaltAwait0(): ABSFuture[Void]= {
  return ABSFuture.done();
}
def SignalImplgetObserverAwait0(): ABSFuture[Train]= {
  return ABSFuture.done(this.observedBy);
}
{
    moveToCOG(destCOG);
    this.hs.setSignal(this);
    this.sp.setSignal(this);
    this.vs.setSignal(this);
  }

}
