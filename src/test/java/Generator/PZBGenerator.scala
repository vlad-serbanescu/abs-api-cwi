package Generator;

import abs.api.cwi.Functions._;
import abs.api.cwi._;
import java.util.Objects;
import java.util.concurrent._;
import java.util.concurrent.atomic._;
import java.util.concurrent.locks._;
import java.util.function._;
import Generator.Functions._;
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
import Train._;
import Train.Functions._;
import ABS.StdLib._;
import ABS.StdLib.Functions._;

trait PZBGenerator extends EventGenerator with Actor with Ordered[Actor] {

  def getState(): ABSFuture[PZBState];

  def oneLess(): ABSFuture[Void];

  def getNextEvents( frontNext : Double,  backNext : Double,  fahrCounter : Int,  emergCount : Int,  dist : Double): ABSFuture[List[GenEvent]];

  def setState( nV : PZBState,  dist : Double): ABSFuture[Void];

}