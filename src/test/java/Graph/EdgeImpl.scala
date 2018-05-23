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

class EdgeImpl(var destCOG : LocalActor,var app : App,var frNode : Node,var toNode : Node,var l : Int) extends LocalActor with Edge {
  private final var serialVersionUID : java.lang.Long =  1L;


  var usedBy : List[Train] =  Nil[Train]();

  def advance( r : Double): ABSFuture[Void]= {
    var f_w1r : Double = r;
    if(Array(r,r)(0)<=0) {
      return this.EdgeImpladvanceAwait0(f_w1r);
    }
    else {
      return spawn(Guard.convert(Array(r,r)),()=>this.EdgeImpladvanceAwait0(f_w1r));
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def pulse( train : Train): ABSFuture[Void]= {
    var first : Boolean =  true;
;
    var r : Double =  timeValue(now());
;
    var z : Int =  0;
;
    while (((length(this.usedBy) > 1) && (z < 10))) {
      var i : Int =  0;
;
      while ((i < length(this.usedBy))) {
        if (( ! (first && (Objects.equals(nth(this.usedBy, i), train))))) {
          var msg_1438144466415 : Callable[ABSFuture[Void]] = () => nth(this.usedBy, i).forceUpdate(r);
          nth(this.usedBy, i).send(msg_1438144466415);
        }
        i = (i + 1);
      }
      var msg_3559070310 : ABSFuture[Void] = this.advance(20);
;
      var f_w1i : Int = i;
      var f_w1first : Boolean = first;
      var f_w1r : Double = r;
      var f_w1z : Int = z;
      var f_w1train : Train = train;
      return this.getSpawn(msg_3559070310, (msg_3559070310_par: Void)=>this.EdgeImplpulseAwait0(f_w1i, f_w1first, f_w1r, f_w1z, f_w1train), Actor.HIGH_PRIORITY, false);
    }
    var j : Int =  0;
;
    while ((j < length(this.usedBy))) {
      var msg_1438144497868 : Callable[ABSFuture[Void]] = () => nth(this.usedBy, j).forceUpdate(r);
      var tmp1212897825 : ABSFuture[Void] = nth(this.usedBy, j).send (msg_1438144497868);
      var f_w2tmp1212897825 : ABSFuture[Void] = tmp1212897825;
      var f_w2first : Boolean = first;
      var f_w2j : Int = j;
      var f_w2r : Double = r;
      var f_w2z : Int = z;
      var f_w2train : Train = train;
      if(tmp1212897825.isDone()) {
        return this.EdgeImplpulseAwait1(f_w2tmp1212897825, f_w2first, f_w2j, f_w2r, f_w2z, f_w2train);
      }
      else {
        return spawn(Guard.convert(tmp1212897825),()=>this.EdgeImplpulseAwait1(f_w2tmp1212897825, f_w2first, f_w2j, f_w2r, f_w2z, f_w2train));
      }
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def trainFullyEnters( train : Train): ABSFuture[Void]= {
    if ((length(this.usedBy) > 1)) {
      var msg_3559070361 : Callable[ABSFuture[Void]] = () => this.pulse(train);
      this.send(msg_3559070361);
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def trainEnters( train : Train): ABSFuture[Void]= {
    this.usedBy = Cons(train, this.usedBy);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def trainLeaves( train : Train): ABSFuture[Void]= {
    this.usedBy = without(this.usedBy, train);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def getTo( n : Node): ABSFuture[Node]= {
    var ret : Node =  this.toNode;
;
    if ((Objects.equals(n, this.toNode))) {
      ret = this.frNode;
    }
    return ABSFuture.done(ret);
  }

  def getLength(): ABSFuture[Int]= {
    return ABSFuture.done(this.l);
  }


def EdgeImpladvanceAwait0( r : Double): ABSFuture[Void]= {
  var w_1$r : Double =  r;
  return ABSFuture.done();
}
def EdgeImplpulseAwait0( i : Int,  first : Boolean,  r : Double,  z : Int,  train : Train): ABSFuture[Void]= {
  var w_1$r : Double =  r;
  var w_1$i : Int =  i;
  var w_1$z : Int =  z;
  var w_1$first : Boolean =  first;
  var w_1$train : Train =  train;
  w_1$r = timeValue(now());
  w_1$first = false;
  w_1$z = (w_1$z + 1);
  while (((length(this.usedBy) > 1) && (w_1$z < 10))) {
    var w_0$i : Int =  0;
;
    while ((w_0$i < length(this.usedBy))) {
      if (( ! (w_1$first && (Objects.equals(nth(this.usedBy, w_0$i), w_1$train))))) {
        var msg_1439214962694 : Callable[ABSFuture[Void]] = () => nth(this.usedBy, w_0$i).forceUpdate(w_1$r);
        nth(this.usedBy, w_0$i).send(msg_1439214962694);
      }
      w_0$i = (w_0$i + 1);
    }
    var msg_3559070739 : ABSFuture[Void] = this.advance(20);
;
    var f_w1w_0$i : Int = w_0$i;
    var f_w1w_1$first : Boolean = w_1$first;
    var f_w1w_1$r : Double = w_1$r;
    var f_w1w_1$z : Int = w_1$z;
    var f_w1w_1$train : Train = w_1$train;
    return this.getSpawn(msg_3559070739, (msg_3559070739_par: Void)=>this.EdgeImplpulseAwait0(f_w1w_0$i, f_w1w_1$first, f_w1w_1$r, f_w1w_1$z, f_w1w_1$train), Actor.HIGH_PRIORITY, false);
  }
  var w_1$j : Int =  0;
;
  while ((w_1$j < length(this.usedBy))) {
    var msg_1439185140427 : Callable[ABSFuture[Void]] = () => nth(this.usedBy, w_1$j).forceUpdate(w_1$r);
    var w_1$tmp1212897825 : ABSFuture[Void] = nth(this.usedBy, w_1$j).send (msg_1439185140427);
    var f_w2w_1$tmp1212897825 : ABSFuture[Void] = w_1$tmp1212897825;
    var f_w2w_1$first : Boolean = w_1$first;
    var f_w2w_1$j : Int = w_1$j;
    var f_w2w_1$r : Double = w_1$r;
    var f_w2w_1$z : Int = w_1$z;
    var f_w2w_1$train : Train = w_1$train;
    if(w_1$tmp1212897825.isDone()) {
      return this.EdgeImplpulseAwait1(f_w2w_1$tmp1212897825, f_w2w_1$first, f_w2w_1$j, f_w2w_1$r, f_w2w_1$z, f_w2w_1$train);
    }
    else {
      return spawn(Guard.convert(w_1$tmp1212897825),()=>this.EdgeImplpulseAwait1(f_w2w_1$tmp1212897825, f_w2w_1$first, f_w2w_1$j, f_w2w_1$r, f_w2w_1$z, f_w2w_1$train));
    }
  }
  return ABSFuture.done();
}
def EdgeImplpulseAwait1( tmp1212897825 : ABSFuture[Void],  first : Boolean,  j : Int,  r : Double,  z : Int,  train : Train): ABSFuture[Void]= {
  var w_2$r : Double =  r;
  var w_2$tmp1212897825 : ABSFuture[Void] =  tmp1212897825;
  var w_2$j : Int =  j;
  var w_2$z : Int =  z;
  var w_2$first : Boolean =  first;
  var w_2$train : Train =  train;
;
  w_2$j = (w_2$j + 1);
  while ((w_2$j < length(this.usedBy))) {
    var msg_1439155349677 : Callable[ABSFuture[Void]] = () => nth(this.usedBy, w_2$j).forceUpdate(w_2$r);
    var w_1$tmp1212897825 : ABSFuture[Void] = nth(this.usedBy, w_2$j).send (msg_1439155349677);
    var f_w2w_1$tmp1212897825 : ABSFuture[Void] = w_1$tmp1212897825;
    var f_w2w_2$first : Boolean = w_2$first;
    var f_w2w_2$j : Int = w_2$j;
    var f_w2w_2$r : Double = w_2$r;
    var f_w2w_2$z : Int = w_2$z;
    var f_w2w_2$train : Train = w_2$train;
    if(w_1$tmp1212897825.isDone()) {
      return this.EdgeImplpulseAwait1(f_w2w_1$tmp1212897825, f_w2w_2$first, f_w2w_2$j, f_w2w_2$r, f_w2w_2$z, f_w2w_2$train);
    }
    else {
      return spawn(Guard.convert(w_1$tmp1212897825),()=>this.EdgeImplpulseAwait1(f_w2w_1$tmp1212897825, f_w2w_2$first, f_w2w_2$j, f_w2w_2$r, f_w2w_2$z, f_w2w_2$train));
    }
  }
  return ABSFuture.done();
}
{
    moveToCOG(destCOG);
    this.frNode.addNext(this);
    this.toNode.addNext(this);
println(((((((("EDGE;" + (this).toString()) + ";") + (this.frNode).toString()) + ";") + (this.toNode).toString()) + ";") + intToString(this.l)));
  }

}
