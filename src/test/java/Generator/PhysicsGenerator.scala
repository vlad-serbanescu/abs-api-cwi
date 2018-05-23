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

trait PhysicsGenerator extends EventGenerator with Actor with Ordered[Actor] {

  def setAccelMax(): ABSFuture[Void];

  def getReise(): ABSFuture[Double];

  def setV( nV : Double): ABSFuture[Void];

  def handleEv( ev : NextEvent): ABSFuture[Double];

  def handleLimitEv( x : Double): ABSFuture[Void];

  def handleLimitPrepareEv( x : Double): ABSFuture[Void];

  def getV(): ABSFuture[Double];

  def getAccel(): ABSFuture[Double];

  def getNextUpEvent( frontNext : Double,  backNext : Double,  changeNext : Double,  fahrCounter : Int,  emergCount : Int,  up : Boolean): ABSFuture[GenEvent];

  def getAccelState(): ABSFuture[AccelState];

  def getNextEvents( frontNext : Double,  backNext : Double,  fahrCounter : Int,  emergCount : Int,  dist : Double): ABSFuture[List[GenEvent]];

  def setAccelBreakNull(): ABSFuture[Void];

  def getEmerg(): ABSFuture[Double];

  def setAccelEmergency(): ABSFuture[Void];

  def acqStop(): ABSFuture[Void];

  def getNextEmergencyEvent( frontNext : Double,  backNext : Double,  changeNext : Double,  fahrCounter : Int,  emergCount : Int): ABSFuture[GenEvent];

  def getBreak(): ABSFuture[Double];

}
