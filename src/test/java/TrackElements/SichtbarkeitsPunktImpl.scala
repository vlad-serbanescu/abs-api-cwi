package TrackElements;

import abs.api.cwi.Functions._;
import abs.api.cwi._;
import java.util.Objects;
import java.util.concurrent._;
import java.util.concurrent.atomic._;
import java.util.concurrent.locks._;
import java.util.function._;
import TrackElements.Functions._;
import Graph._;
import Graph.Functions._;
import Train._;
import Train.Functions._;
import Util._;
import Util.Functions._;
import SwElements._;
import SwElements.Functions._;
import Stellwerk._;
import Stellwerk.Functions._;
import ABS.StdLib._;
import ABS.StdLib.Functions._;
import ABS.StdLib._;
import ABS.StdLib.Functions._;

class SichtbarkeitsPunktImpl(var destCOG : LocalActor,var waitTrack : Edge) extends LocalActor with SichtbarkeitsPunkt {
  private final var serialVersionUID : java.lang.Long =  1L;


  var state : SignalState =  STOP();

  var s : Signal =  null;

  def triggerFront( train : Train,  t : Time,  e : Edge): ABSFuture[Information]= {
    var ret : Information =  NoInfo();
;
    if (((!Objects.equals(this.s, null)) && (Objects.equals(this.waitTrack, e)))) {
      this.s.setObserver(train);
      ret = Prepare(this.state);
    }
    return ABSFuture.done(ret);
  }

  def triggerBack( train : Train,  t : Time,  e : Edge): ABSFuture[Information]= {
    return ABSFuture.done(NoInfo());
  }

  def setSignal( sig : Signal): ABSFuture[Void]= {
    this.s = sig;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def getState(): ABSFuture[SignalState]= {
    return ABSFuture.done(this.state);
  }

  def setState( newState : SignalState,  t : Time): ABSFuture[Void]= {
    this.state = newState;
println(((((("CH;" + (this).toString()) + ";") + (newState).toString()) + ";") + (timeValue(t)).toString()));
    return ABSFuture.done();
    return ABSFuture.done();
  }


{
    moveToCOG(destCOG);
  }

}
