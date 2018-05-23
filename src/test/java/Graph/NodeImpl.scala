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

class NodeImpl(var destCOG : LocalActor,var x : Int,var y : Int) extends LocalActor with Node {
  private final var serialVersionUID : java.lang.Long =  1L;


  var edges : List[Edge] =  Nil[Edge]();

  var waiting : Train =  null;

  var belongs : List[TrackElement] =  Nil[TrackElement]();

  def getCrit(): ABSFuture[TrackElement]= {
    return ABSFuture.done(null);
  }

  def hasCrit(): ABSFuture[Boolean]= {
    return ABSFuture.done(false);
  }

  def addElement( elem : TrackElement): ABSFuture[Void]= {
    var st : SignalState = elem.getState().getOrNull();
;
println(((((("ELEM;" + (elem).toString()) + ";") + (this).toString()) + ";") + (st).toString()));
    this.belongs = Cons(elem, this.belongs);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def triggerFront( train : Train,  t : Time,  edge : Edge): ABSFuture[List[Information]]= {
    var i : Int =  0;
;
    var ret : List[Information] =  Nil[Information]();
;
    var last : Information =  NoInfo();
;
    while ((i < length(this.belongs))) {
      var e : TrackElement =  nth(this.belongs, i);
;
      last = e.triggerFront(train, t, edge).getOrNull();
;
      if ((!Objects.equals(last, NoInfo()))) {
        ret = Cons(last, ret);
      }
      i = (i + 1);
    }
    return ABSFuture.done(ret);
  }

  def triggerBack( train : Train,  t : Time,  e : Edge): ABSFuture[List[Information]]= {
    var i : Int =  0;
;
    var ret : List[Information] =  Nil[Information]();
;
    var last : Information =  NoInfo();
;
    while ((i < length(this.belongs))) {
      var el : TrackElement =  nth(this.belongs, i);
;
      last = el.triggerBack(train, t, e).getOrNull();
;
      if ((!Objects.equals(last, NoInfo()))) {
        ret = Cons(last, ret);
      }
      i = (i + 1);
    }
    return ABSFuture.done(ret);
  }

  def addNext( e : Edge): ABSFuture[Void]= {
    this.edges = Cons(e, this.edges);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def removeNext( e : Edge): ABSFuture[Void]= {
    this.edges = remOutEdge(this.edges, e);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def getOut( e : Edge): ABSFuture[Edge]= {
    return ABSFuture.done(getOutEdge(this.edges, e));
  }


{
    moveToCOG(destCOG);
println(((((("NODE;" + (this).toString()) + ";") + intToString(this.x)) + ";") + intToString(this.y)));
  }

}
