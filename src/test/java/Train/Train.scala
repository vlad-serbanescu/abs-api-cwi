package Train;

import abs.api.cwi.Functions._;
import abs.api.cwi._;
import java.util.Objects;
import java.util.concurrent._;
import java.util.concurrent.atomic._;
import java.util.concurrent.locks._;
import java.util.function._;
import Train.Functions._;
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
import Generator._;
import Generator.Functions._;
import ABS.StdLib._;
import ABS.StdLib.Functions._;

trait Train extends Actor with Ordered[Actor] {

  def go( start : Edge,  init : Node,  startOffset : Double,  startSpeed : Double): ABSFuture[Void];

  def setTtl( i : Int): ABSFuture[Void];

  def forceUpdate( r : Double): ABSFuture[Void];

  def tellTime(): ABSFuture[Void];

  def acqStop(): ABSFuture[Time];

  def goResp( start : Edge,  init : Node,  startOffset : Double,  startSpeed : Double,  resp : ZugFolge): ABSFuture[Void];

  def notify( info : Information,  t : Time): ABSFuture[Void];

  def command( com : Command,  t : Time): ABSFuture[Void];

  def order( given : List[Order]): ABSFuture[Void];

}
