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

class SwitchImpl(var destCOG : LocalActor,var wa : WeichenPunkt,var w1 : WeichenPunkt,var w2 : WeichenPunkt,var e1 : Edge,var e2 : Edge,var joinSwitch : Boolean) extends LocalActor with Switch {
  private final var serialVersionUID : java.lang.Long =  1L;


  var inDefault : Boolean =  true;

  def swap(): ABSFuture[Void]= {
    if (this.inDefault) {
      this.wa.removeEdge(this.e1);
      this.w1.removeEdge(this.e1);
      this.wa.addEdge(this.e2);
      this.w2.addEdge(this.e2);
    }
    else {
      this.wa.removeEdge(this.e2);
      this.w2.removeEdge(this.e2);
      this.wa.addEdge(this.e1);
      this.w1.addEdge(this.e1);
    }
    this.inDefault = ( ! this.inDefault);
    return ABSFuture.done();
    return ABSFuture.done();
  }


{
    moveToCOG(destCOG);
    this.wa.removeEdge(this.e2);
    this.w2.removeEdge(this.e2);
  }

}
