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

trait ActiveZugFolge extends ZugFolge with Actor with Ordered[Actor] {

  def triggered( s : Signal): ABSFuture[Void];

  def nextZfFailed( zf : ActiveZugFolge,  st : Strecke): ABSFuture[Void];

  def reqFree( zf : ActiveZugFolge,  st : Strecke): ABSFuture[Void];

  def addSignalZf( zf : ZugFolge,  st : Strecke,  s : Signal,  st2 : Strecke,  zf2 : ZugFolge): ABSFuture[Void];

  def rueckblock( zf : ZugFolge): ABSFuture[Void];

  def vorblock( zf : ZugFolge,  s : Strecke): ABSFuture[Void];

  def tellTime(): ABSFuture[Void];

  def listen( f : ABSFuture[Pair[Train, Strecke]]): ABSFuture[Void];

}
