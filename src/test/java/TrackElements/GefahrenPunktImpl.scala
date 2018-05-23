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

class GefahrenPunktImpl(var destCOG : LocalActor,var waitTrack : Edge) extends LocalActor with GefahrenPunkt {
  private final var serialVersionUID : java.lang.Long =  1L;


  var signal : Signal =  null;

  var lastPoint : Boolean =  false;

  def setSignal( sig : Signal): ABSFuture[Void]= {
    this.signal = sig;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setLast( last : Boolean): ABSFuture[Void]= {
    this.lastPoint = last;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def triggerBack( train : Train,  t : Time,  e : Edge): ABSFuture[Information]= {
    var info : Information =  NoInfo();
;
    if (((!Objects.equals(this.signal, null)) && (Objects.equals(e, this.waitTrack)))) {
      var msg_2068035576732 : Callable[ABSFuture[Void]] = () => this.signal.triggered();
      this.signal.send(msg_2068035576732);
    }
    if ((this.lastPoint && (Objects.equals(e, this.waitTrack)))) {
      info = WeichenbereichVerlassen();
    }
    return ABSFuture.done(info);
  }

  def triggerFront( train : Train,  t : Time,  e : Edge): ABSFuture[Information]= {
    return ABSFuture.done(NoInfo());
  }

  def getState(): ABSFuture[SignalState]= {
    return ABSFuture.done(NOSIG());
  }


{
    moveToCOG(destCOG);
  }

}
