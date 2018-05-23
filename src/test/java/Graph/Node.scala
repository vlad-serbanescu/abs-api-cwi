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

trait Node extends Actor with Ordered[Actor] {

  def getOut( e : Edge): ABSFuture[Edge];

  def addNext( e : Edge): ABSFuture[Void];

  def triggerFront( train : Train,  t : Time,  e : Edge): ABSFuture[List[Information]];

  def hasCrit(): ABSFuture[Boolean];

  def addElement( elem : TrackElement): ABSFuture[Void];

  def triggerBack( train : Train,  t : Time,  e : Edge): ABSFuture[List[Information]];

  def getCrit(): ABSFuture[TrackElement];

  def removeNext( e : Edge): ABSFuture[Void];

}
