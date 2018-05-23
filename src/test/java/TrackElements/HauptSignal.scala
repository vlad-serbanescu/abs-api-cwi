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

trait HauptSignal extends CriticalElement with Actor with Ordered[Actor] {

  def isPassable(): ABSFuture[Boolean];

  def getState(): ABSFuture[SignalState];

  def triggerFront( train : Train,  t : Time,  e : Edge): ABSFuture[Information];

  def getWaitTrack(): ABSFuture[Edge];

  def setState( state : SignalState,  t : Time): ABSFuture[Void];

  def getNode(): ABSFuture[Node];

  def setSignal( sig : Signal): ABSFuture[Void];

  def triggerBack( train : Train,  t : Time,  e : Edge): ABSFuture[Information];

}
