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

class StreckeImpl(var destCOG : LocalActor,var a : Edge,var b : Edge,var aZfst : ZugFolge,var bZfst : ZugFolge,var length : Int) extends LocalActor with Strecke {
  private final var serialVersionUID : java.lang.Long =  1L;


  def getLength(): ABSFuture[Int]= {
    return ABSFuture.done(this.length);
  }


{
    moveToCOG(destCOG);
  }

}
