package Graph;

import abs.api.cwi.Functions._;
import abs.api.cwi._;
import java.util.Objects;
import java.util.concurrent._;
import java.util.concurrent.atomic._;
import java.util.concurrent.locks._;
import java.util.function._;
import Graph.Functions._;
import TrackElements._;
import TrackElements.Functions._;
import Train._;
import Train.Functions._;
import Util._;
import Util.Functions._;
import Run._;
import Run.Functions._;
import ABS.StdLib._;
import ABS.StdLib.Functions._;

trait Edge extends Actor with Ordered[Actor] {

  def getLength(): ABSFuture[Int];

  def getTo( n : Node): ABSFuture[Node];

  def trainFullyEnters( train : Train): ABSFuture[Void];

  def trainEnters( train : Train): ABSFuture[Void];

  def trainLeaves( train : Train): ABSFuture[Void];

}
