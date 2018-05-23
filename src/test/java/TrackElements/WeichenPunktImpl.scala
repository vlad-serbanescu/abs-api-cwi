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

class WeichenPunktImpl(var destCOG : LocalActor,var n : Node) extends LocalActor with WeichenPunkt {
  private final var serialVersionUID : java.lang.Long =  1L;


  def addEdge( edge : Edge): ABSFuture[Void]= {
    this.n.addNext(edge);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def removeEdge( edge : Edge): ABSFuture[Void]= {
    this.n.removeNext(edge);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def isPassable(): ABSFuture[Boolean]= {
    return ABSFuture.done(true);
  }

  def triggerBack( train : Train,  t : Time,  e : Edge): ABSFuture[Information]= {
    return ABSFuture.done(NoInfo());
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
