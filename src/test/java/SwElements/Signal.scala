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

trait Signal extends Actor with Ordered[Actor] with SwElement {

  def sperren( t : Time): ABSFuture[Boolean];

  def setObserver( obs : Train): ABSFuture[Void];

  def triggered(): ABSFuture[Void];

  def isFree(): ABSFuture[Boolean];

  def break( t : Time): ABSFuture[Void];

  def freischalten(): ABSFuture[Boolean];

  def acqHalt(): ABSFuture[Void];

  def printName( prefix : String): ABSFuture[Void];

  def breakNow(): ABSFuture[Void];

  def getObserver(): ABSFuture[Train];

  def acqFree(): ABSFuture[Void];

}
