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

class ContactMagnetImpl(var destCOG : LocalActor) extends LocalActor with ContactMagnet {
  private final var serialVersionUID : java.lang.Long =  1L;


  var logical : Magnetable =  null;

  var active : Boolean =  false;

  def setLogical( log : Magnetable): ABSFuture[Void]= {
    this.logical = log;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def triggerFront( train : Train,  t : Time,  e : Edge): ABSFuture[Information]= {
    var info : Information =  NoInfo();
;
    if ((!Objects.equals(this.logical, null))) {
      var msg_2061337415503 : Callable[ABSFuture[Void]] = () => this.logical.passed(this);
      this.logical.send(msg_2061337415503);
    }
    if (this.active) {
      info = TestInfo();
    }
    return ABSFuture.done(info);
  }

  def activate(): ABSFuture[Void]= {
    this.active = true;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def deactivate(): ABSFuture[Void]= {
    this.active = false;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def triggerBack( train : Train,  t : Time,  e : Edge): ABSFuture[Information]= {
    return ABSFuture.done(NoInfo());
  }

  def getState(): ABSFuture[SignalState]= {
    return ABSFuture.done(NOSIG());
  }


{
    moveToCOG(destCOG);
  }

}
