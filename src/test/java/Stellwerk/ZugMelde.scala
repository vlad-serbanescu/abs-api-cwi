package Stellwerk;

import abs.api.cwi.Functions._;
import abs.api.cwi._;
import java.util.Objects;
import java.util.concurrent._;
import java.util.concurrent.atomic._;
import java.util.concurrent.locks._;
import java.util.function._;
import Stellwerk.Functions._;
import Train._;
import Train.Functions._;
import Graph._;
import Graph.Functions._;
import SwElements._;
import SwElements.Functions._;
import Util._;
import Util.Functions._;
import TrackElements._;
import TrackElements.Functions._;
import Run._;
import Run.Functions._;
import ABS.StdLib._;
import ABS.StdLib.Functions._;

trait ZugMelde extends ZugFolge with Actor with Ordered[Actor] {

  def forceErl( st2 : Strecke): ABSFuture[Void];

  def triggered( s : Signal): ABSFuture[Void];

  def nextZfFailed( zf : ActiveZugFolge,  st : Strecke): ABSFuture[Void];

  def rueckmeldung( swFrom : ZugMelde,  z : Train,  stMelde : Strecke): ABSFuture[Void];

  def addSignalMelde( s : Signal,  melde : ZugMelde,  dur : Int): ABSFuture[Void];

  def setOutPath( weichen : List[Switch]): ABSFuture[Void];

  def initTrain( train : Train,  s : Signal): ABSFuture[Void];

  def reqFree( zf : ActiveZugFolge,  st : Strecke): ABSFuture[Void];

  def addInSignalMelde( s : Signal,  melde : ZugMelde): ABSFuture[Void];

  def tellTime(): ABSFuture[Void];

  def setSchedule( sch : Map[Train, ZugFolge]): ABSFuture[Void];

  def acqPermit( s : Signal,  n : Train,  st2 : Strecke,  next : ZugFolge,  nextM : ZugMelde): ABSFuture[Void];

  def process( n : Train): ABSFuture[ABSFuture[Void]];

  def anmelden( train : Train,  dur : Double,  swFrom : ZugMelde,  st : Strecke): ABSFuture[Void];

  def go( sch : List[Train]): ABSFuture[Void];

  def setInTracks( inTr : Map[ZugMelde, Pair[Strecke, Strecke]]): ABSFuture[Void];

  def rueckblock( zf : ZugFolge): ABSFuture[Void];

  def vorblock( zf : ZugFolge,  s : Strecke): ABSFuture[Void];

  def setPaths( pa : Map[Pair[Signal, Signal], List[Switch]]): ABSFuture[Void];

  def reqErlaubnis( sw : ZugMelde,  stMelde : Strecke): ABSFuture[Pair[Boolean, Strecke]];

  def setStreckeMap( melde : List[Pair[Strecke, Strecke]]): ABSFuture[Void];

  def setOutPaths( pa : Map[Pair[Signal, ZugMelde], List[Switch]]): ABSFuture[Void];

  def setPreconditions( s : Signal,  n : Train,  st2 : Strecke,  next : ZugFolge,  nextM : ZugMelde): ABSFuture[List[Switch]];

  def force( zug : Train,  sig : Signal): ABSFuture[Void];

  def anbieten( train : Train,  st : Strecke,  swFrom : ZugMelde): ABSFuture[Pair[Train, Strecke]];

  def addInSignal( zf : ZugFolge,  st : Strecke,  s : Signal): ABSFuture[Void];

  def addOutSignal( s : Signal,  st : Strecke,  toSw : ZugFolge): ABSFuture[Void];

}
