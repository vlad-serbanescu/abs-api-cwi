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

class TrainImpl(var destCOG : LocalActor,var app : App,var name : String,var lengthn : Int) extends LocalActor with Train {
  private final var serialVersionUID : java.lang.Long =  1L;


  var posFront : Edge =  null;

  var posBack : Edge =  null;

  var nodeFront : Node =  null;

  var nodeBack : Node =  null;

  var locFront : Double =  0;

  var locBack : Double =  0;

  var ttl : Int =  -1;

  var lastStop : Time =  TimeOf(0);

  var responsible : ZugFolge =  null;

  var nextStopReq : Boolean =  false;

  var orders : List[Order] =  Nil[Order]();

  var emergCount : Int =  0;

  var fahrCounter : Int =  0;

  var vwait : Boolean =  false;

  var nEv : NextEvent =  NoEvent();

  var distanceTotal : Double =  0;

  var pGen : PhysicsGenerator =  null;

  var pzbGen : PZBGenerator =  null;

  def advance( r : Double): ABSFuture[Void]= {
    var f_w1r : Double = r;
    if(Array(r,r)(0)<=0) {
      return this.TrainImpladvanceAwait0(f_w1r);
    }
    else {
      return spawn(Guard.convert(Array(r,r)),()=>this.TrainImpladvanceAwait0(f_w1r));
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def go( start : Edge,  init : Node,  startOffset : Double,  startSpeed : Double): ABSFuture[Void]= {
    var msg_3559070584 : ABSFuture[Void] = this.goResp(start, init, startOffset, startSpeed, null);
;
    var f_w1init : Node = init;
    var f_w1start : Edge = start;
    var f_w1startOffset : Double = startOffset;
    var f_w1startSpeed : Double = startSpeed;
    return this.getSpawn(msg_3559070584, (msg_3559070584_par: Void)=>this.TrainImplgoAwait0(f_w1init, f_w1start, f_w1startOffset, f_w1startSpeed), Actor.HIGH_PRIORITY, false);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def goResp( start : Edge,  init : Node,  startOffset : Double,  startSpeed : Double,  resp : ZugFolge): ABSFuture[Void]= {
    if ((startOffset > 0)) {
      var msg_355907092 : ABSFuture[Void] = this.advance(startOffset);
;
      var f_w1init : Node = init;
      var f_w1resp : ZugFolge = resp;
      var f_w1start : Edge = start;
      var f_w1startOffset : Double = startOffset;
      var f_w1startSpeed : Double = startSpeed;
      return this.getSpawn(msg_355907092, (msg_355907092_par: Void)=>this.TrainImplgoRespAwait0(f_w1init, f_w1resp, f_w1start, f_w1startOffset, f_w1startSpeed), Actor.HIGH_PRIORITY, false);
    }
println(((((("MV;INIT;" + (this).toString()) + ";") + intToString(truncate(startOffset))) + ";") + (start).toString()));
    this.posFront = start;
    this.posBack = start;
    this.nodeFront = init;
    this.nodeBack = init;
    this.locFront = this.lengthn;
    this.locBack = 0;
    this.pGen.setV(startSpeed);
    this.responsible = resp;
    if ((startSpeed > 0)) {
      var msg_3559070393 : ABSFuture[Void] = this.detNext();
;
      var f_w2init : Node = init;
      var f_w2resp : ZugFolge = resp;
      var f_w2start : Edge = start;
      var f_w2startOffset : Double = startOffset;
      var f_w2startSpeed : Double = startSpeed;
      return this.getSpawn(msg_3559070393, (msg_3559070393_par: Void)=>this.TrainImplgoRespAwait1(f_w2init, f_w2resp, f_w2start, f_w2startOffset, f_w2startSpeed), Actor.HIGH_PRIORITY, false);
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def detNext(): ABSFuture[Void]= {
    var v : Double = this.pGen.getV().getOrNull();
;
println(((((((("DATA;" + this.name) + ";") + (truncate(timeValue(now()))).toString()) + " ") + intToString(truncate(v))) + ";") + (now()).toString()));
    this.vwait = false;
    var msg_783956603291 : Callable[ABSFuture[Int]] = () => this.posFront.getLength();
    var tmp456897159 : ABSFuture[Int] = this.posFront.send (msg_783956603291);
    var f_w1tmp456897159 : ABSFuture[Int] = tmp456897159;
    var f_w1v : Double = v;
    if(tmp456897159.isDone()) {
      return this.TrainImpldetNextAwait0(f_w1tmp456897159, f_w1v);
    }
    else {
      return spawn(Guard.convert(tmp456897159),()=>this.TrainImpldetNextAwait0(f_w1tmp456897159, f_w1v));
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def getNext( list : List[GenEvent]): ABSFuture[GenEvent]= {
    var minEv : GenEvent =  ChangeEv(NoEvent(), false);
;
    var min : Double =  86400000;
;
    var i : Int =  0;
;
    while ((i < length(list))) {
println((minEv).toString());
      var next : GenEvent =  nth(list, i);
;
      var nextT : Double =  next match  {
          case ChangeEv(ne, _) => moment(ne);
          case FrontEv(ne) => moment(ne);
          case BackEv(ne) => moment(ne);
          case NoGenEvent() => -1;
          }
          ;
;
      if (((nextT < min) && (nextT >= 0))) {
        min = nextT;
        minEv = next;
      }
      i = (i + 1);
    }
    return ABSFuture.done(minEv);
  }

  def arriveMiddle( ev : NextEvent,  forceSkip : Boolean): ABSFuture[Void]= {
    var msg_3559070608 : ABSFuture[Void] = this.advance(in_short(moment(ev)));
;
    var f_w1ev : NextEvent = ev;
    var f_w1forceSkip : Boolean = forceSkip;
    return this.getSpawn(msg_3559070608, (msg_3559070608_par: Void)=>this.TrainImplarriveMiddleAwait0(f_w1ev, f_w1forceSkip), Actor.HIGH_PRIORITY, false);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def arriveFront( ev : NextEvent): ABSFuture[Void]= {
    var msg_3559070207 : ABSFuture[Void] = this.advance(in_short(moment(ev)));
;
    var f_w1ev : NextEvent = ev;
    return this.getSpawn(msg_3559070207, (msg_3559070207_par: Void)=>this.TrainImplarriveFrontAwait0(f_w1ev), Actor.HIGH_PRIORITY, false);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def arriveBack( ev : NextEvent): ABSFuture[Void]= {
    var msg_355907081 : ABSFuture[Void] = this.advance(in_short(moment(ev)));
;
    var f_w1ev : NextEvent = ev;
    return this.getSpawn(msg_355907081, (msg_355907081_par: Void)=>this.TrainImplarriveBackAwait0(f_w1ev), Actor.HIGH_PRIORITY, false);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def processInfo( i : Information): ABSFuture[Void]= {
    i match  {
      case ChangeResp(zf) => this.responsible = zf;
          ;
      case Info(STOP()) => if (( ! listContains(this.orders, Ord2()))) {
            this.pGen.setAccelEmergency();
          }
          else {
            this.orders = without(this.orders, Ord2());
          }
          ;
      case StartPrepare(STOP()) => if (( ! listContains(this.orders, Ord2()))) {
            this.pGen.setAccelBreakNull();
          }
          else {
            this.orders = without(this.orders, Ord2());
          }
          ;
      case Info(FAHRT()) => if (listContains(this.orders, Ord144())) {
            this.pGen.setAccelEmergency();
            this.orders = without(this.orders, Ord144());
          }
          ;
      case StartPrepare(FAHRT()) => if (listContains(this.orders, Ord144())) {
            this.pGen.setAccelBreakNull();
            this.orders = without(this.orders, Ord144());
            this.nextStopReq = true;
          }
          ;
      case Mhz1000() => this.pzbGen.setState(Last1000(), this.distanceTotal);
          ;
      case Mhz500() => this.pzbGen.setState(Last500(), this.distanceTotal);
          ;
      case Mhz2000() => this.pGen.setAccelEmergency();
          this.pzbGen.setState(LastNone(), this.distanceTotal);
          ;
      case Limit(x) => this.pGen.handleLimitEv(x);
          ;
      case LimitPrepare(x) => this.pGen.handleLimitPrepareEv(x);
          ;
      case WeichenbereichVerlassen() => var vreise : Double = this.pGen.getReise().getOrNull();
          ;
          this.processInfo(Limit(truncate(vreise)));
          ;
      case _ => ;
      case _ => ;
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def notify( info : Information,  t : Time): ABSFuture[Void]= {
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def command( com : Command,  t : Time): ABSFuture[Void]= {
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def forceUpdate( r : Double): ABSFuture[Void]= {
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def order( given : List[Order]): ABSFuture[Void]= {
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def tellTime(): ABSFuture[Void]= {
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setTtl( i : Int): ABSFuture[Void]= {
    this.ttl = i;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def acqStop(): ABSFuture[Time]= {
    var msg_605790704804 : Callable[ABSFuture[Void]] = () => this.pGen.acqStop();
    var tmp593573468 : ABSFuture[Void] = this.pGen.send (msg_605790704804);
    var f_w1tmp593573468 : ABSFuture[Void] = tmp593573468;
    if(tmp593573468.isDone()) {
      return this.TrainImplacqStopAwait0(f_w1tmp593573468);
    }
    else {
      return spawn(Guard.convert(tmp593573468),()=>this.TrainImplacqStopAwait0(f_w1tmp593573468));
    }
  }

  def requestAtResp(): ABSFuture[Void]= {
    this.nextStopReq = false;
    if ((!Objects.equals(this.responsible, null))) {
      this.command(Resume(), now());
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }


def TrainImpladvanceAwait0( r : Double): ABSFuture[Void]= {
  var w_1$r : Double =  r;
  return ABSFuture.done();
}
def TrainImplgoAwait0( init : Node,  start : Edge,  startOffset : Double,  startSpeed : Double): ABSFuture[Void]= {
  var w_1$init : Node =  init;
  var w_1$startOffset : Double =  startOffset;
  var w_1$startSpeed : Double =  startSpeed;
  var w_1$start : Edge =  start;
  return ABSFuture.done();
}
def TrainImplgoRespAwait0( init : Node,  resp : ZugFolge,  start : Edge,  startOffset : Double,  startSpeed : Double): ABSFuture[Void]= {
  var w_1$init : Node =  init;
  var w_1$startOffset : Double =  startOffset;
  var w_1$resp : ZugFolge =  resp;
  var w_1$startSpeed : Double =  startSpeed;
  var w_1$start : Edge =  start;
println(((((("MV;INIT;" + (this).toString()) + ";") + intToString(truncate(w_1$startOffset))) + ";") + (w_1$start).toString()));
  this.posFront = w_1$start;
  this.posBack = w_1$start;
  this.nodeFront = w_1$init;
  this.nodeBack = w_1$init;
  this.locFront = this.lengthn;
  this.locBack = 0;
  this.pGen.setV(w_1$startSpeed);
  this.responsible = w_1$resp;
  if ((w_1$startSpeed > 0)) {
    var msg_3559070201 : ABSFuture[Void] = this.detNext();
;
    var f_w2w_1$init : Node = w_1$init;
    var f_w2w_1$resp : ZugFolge = w_1$resp;
    var f_w2w_1$start : Edge = w_1$start;
    var f_w2w_1$startOffset : Double = w_1$startOffset;
    var f_w2w_1$startSpeed : Double = w_1$startSpeed;
    return this.getSpawn(msg_3559070201, (msg_3559070201_par: Void)=>this.TrainImplgoRespAwait1(f_w2w_1$init, f_w2w_1$resp, f_w2w_1$start, f_w2w_1$startOffset, f_w2w_1$startSpeed), Actor.HIGH_PRIORITY, false);
  }
  return ABSFuture.done();
}
def TrainImplgoRespAwait1( init : Node,  resp : ZugFolge,  start : Edge,  startOffset : Double,  startSpeed : Double): ABSFuture[Void]= {
  var w_2$init : Node =  init;
  var w_2$startOffset : Double =  startOffset;
  var w_2$resp : ZugFolge =  resp;
  var w_2$startSpeed : Double =  startSpeed;
  var w_2$start : Edge =  start;
  return ABSFuture.done();
}
def TrainImpldetNextAwait0( tmp456897159 : ABSFuture[Int],  v : Double): ABSFuture[Void]= {
  var w_1$v : Double =  v;
  var w_1$tmp456897159 : ABSFuture[Int] =  tmp456897159;
  var lFront : Double = w_1$tmp456897159.getOrNull();
  var msg_1498595851124 : Callable[ABSFuture[Int]] = () => this.posBack.getLength();
  var tmp1650415378 : ABSFuture[Int] = this.posBack.send (msg_1498595851124);
  var f_w2lFront : Double = lFront;
  var f_w2tmp1650415378 : ABSFuture[Int] = tmp1650415378;
  var f_w2w_1$tmp456897159 : ABSFuture[Int] = w_1$tmp456897159;
  var f_w2w_1$v : Double = w_1$v;
  if(tmp1650415378.isDone()) {
    return this.TrainImpldetNextAwait1(f_w2lFront, f_w2tmp1650415378, f_w2w_1$tmp456897159, f_w2w_1$v);
  }
  else {
    return spawn(Guard.convert(tmp1650415378),()=>this.TrainImpldetNextAwait1(f_w2lFront, f_w2tmp1650415378, f_w2w_1$tmp456897159, f_w2w_1$v));
  }
  return ABSFuture.done();
}
def TrainImpldetNextAwait1( lFront : Double,  tmp1650415378 : ABSFuture[Int],  tmp456897159 : ABSFuture[Int],  v : Double): ABSFuture[Void]= {
  var w_2$lFront : Double =  lFront;
  var w_2$v : Double =  v;
  var w_2$tmp1650415378 : ABSFuture[Int] =  tmp1650415378;
  var w_2$tmp456897159 : ABSFuture[Int] =  tmp456897159;
  var lBack : Double = w_2$tmp1650415378.getOrNull();
  var frontNext : Double =  (w_2$lFront - this.locFront);
;
  var backNext : Double =  (lBack - this.locBack);
;
  var list : List[GenEvent] = this.pGen.getNextEvents(frontNext, backNext, this.fahrCounter, this.emergCount, this.distanceTotal).getOrNull();
;
  var list2 : List[GenEvent] = this.pzbGen.getNextEvents(frontNext, backNext, this.fahrCounter, this.emergCount, this.distanceTotal).getOrNull();
;
  list = concatenate(list, list2);
  var gen : GenEvent = this.getNext(list).getOrNull();
;
  gen match  {
    case ChangeEv(w_2$ne, w_2$em) => this.nEv = w_2$ne;
        var msg_3559070449 : Callable[ABSFuture[Void]] = () => this.arriveMiddle(this.nEv, false);
        this.send(msg_3559070449);
        ;
    case FrontEv(w_2$ne) => this.nEv = w_2$ne;
        var msg_3559070158 : Callable[ABSFuture[Void]] = () => this.arriveFront(this.nEv);
        this.send(msg_3559070158);
        ;
    case BackEv(w_2$ne) => this.nEv = w_2$ne;
        var msg_3559070390 : Callable[ABSFuture[Void]] = () => this.arriveBack(this.nEv);
        this.send(msg_3559070390);
        ;
    case _ => ;
  }
  return ABSFuture.done();
}
def TrainImplarriveMiddleAwait0( ev : NextEvent,  forceSkip : Boolean): ABSFuture[Void]= {
  var w_1$ev : NextEvent =  ev;
  var w_1$forceSkip : Boolean =  forceSkip;
  if ((Objects.equals(counter(w_1$ev), this.emergCount))) {
    var w_1$nV : Double = this.pGen.handleEv(w_1$ev).getOrNull();
;
    if (pzbOneLess(w_1$ev)) {
      this.pzbGen.oneLess();
    }
    this.distanceTotal = (this.distanceTotal + ll(w_1$ev));
    this.locFront = (this.locFront + ll(w_1$ev));
    this.locBack = (this.locBack + ll(w_1$ev));
println(((((((("MV;SPEED;" + (this).toString()) + ";") + intToString(truncate(timeValue(now())))) + ";") + intToString(truncate(ll(w_1$ev)))) + ";") + intToString(truncate(vnew(w_1$ev)))));
    if ((Objects.equals(w_1$nV, 0))) {
println(((((((("DATA;" + this.name) + ";") + intToString(truncate(timeValue(now())))) + " ") + intToString(truncate(w_1$nV))) + ";") + (now()).toString()));
      this.lastStop = now();
      this.vwait = true;
      if ((Objects.equals(this.nextStopReq, true))) {
        this.requestAtResp();
      }
    }
    else {
      if (w_1$forceSkip) {
        this.vwait = true;
      }
      else {
        var msg_3559070473 : ABSFuture[Void] = this.detNext();
;
        var f_w2w_1$nV : Double = w_1$nV;
        var f_w2w_1$ev : NextEvent = w_1$ev;
        var f_w2w_1$forceSkip : Boolean = w_1$forceSkip;
        return this.getSpawn(msg_3559070473, (msg_3559070473_par: Void)=>this.TrainImplarriveMiddleAwait1(f_w2w_1$nV, f_w2w_1$ev, f_w2w_1$forceSkip), Actor.HIGH_PRIORITY, false);
      }
    }
  }
  return ABSFuture.done();
}
def TrainImplarriveMiddleAwait1( nV : Double,  ev : NextEvent,  forceSkip : Boolean): ABSFuture[Void]= {
  var w_2$ev : NextEvent =  ev;
  var w_2$nV : Double =  nV;
  var w_2$forceSkip : Boolean =  forceSkip;
  return ABSFuture.done();
}
def TrainImplarriveFrontAwait0( ev : NextEvent): ABSFuture[Void]= {
  var w_1$ev : NextEvent =  ev;
  if ((!Objects.equals(fahrCount(w_1$ev), this.fahrCounter))) {
    var msg_3559070560 : ABSFuture[Void] = this.detNext();
;
    var f_w2w_1$ev : NextEvent = w_1$ev;
    return this.getSpawn(msg_3559070560, (msg_3559070560_par: Void)=>this.TrainImplarriveFrontAwait1(f_w2w_1$ev), Actor.HIGH_PRIORITY, false);
  }
  else {
    if ((Objects.equals(counter(w_1$ev), this.emergCount))) {
      this.distanceTotal = (this.distanceTotal + ll(w_1$ev));
      var w_2$dis : Double =  ll(w_1$ev);
;
      this.pGen.handleEv(w_1$ev);
      var w_2$reachedEnd : Boolean =  false;
;
      var msg_783956603978 : Callable[ABSFuture[Graph.Node]] = () => this.posFront.getTo(this.nodeFront);
      var w_2$tmp741236338 : ABSFuture[Graph.Node] = this.posFront.send (msg_783956603978);
      var f_w3w_2$dis : Double = w_2$dis;
      var f_w3w_2$reachedEnd : Boolean = w_2$reachedEnd;
      var f_w3w_2$tmp741236338 : ABSFuture[Graph.Node] = w_2$tmp741236338;
      var f_w3w_1$ev : NextEvent = w_1$ev;
      if(w_2$tmp741236338.isDone()) {
        return this.TrainImplarriveFrontAwait2(f_w3w_2$dis, f_w3w_2$reachedEnd, f_w3w_2$tmp741236338, f_w3w_1$ev);
      }
      else {
        return spawn(Guard.convert(w_2$tmp741236338),()=>this.TrainImplarriveFrontAwait2(f_w3w_2$dis, f_w3w_2$reachedEnd, f_w3w_2$tmp741236338, f_w3w_1$ev));
      }
    }
  }
  return ABSFuture.done();
}
def TrainImplarriveFrontAwait1( ev : NextEvent): ABSFuture[Void]= {
  var w_2$ev : NextEvent =  ev;
  return ABSFuture.done();
}
def TrainImplarriveFrontAwait2( dis : Double,  reachedEnd : Boolean,  tmp741236338 : ABSFuture[Graph.Node],  ev : NextEvent): ABSFuture[Void]= {
  var w_3$ev : NextEvent =  ev;
  var w_3$reachedEnd : Boolean =  reachedEnd;
  var w_3$tmp741236338 : ABSFuture[Graph.Node] =  tmp741236338;
  var w_3$dis : Double =  dis;
  var n : Node = w_3$tmp741236338.getOrNull();
  var msg_110524 : Callable[ABSFuture[List[Information]]] = () => n.triggerFront(this, now(), this.posFront);
  var fi : ABSFuture[List[Information]] = n.send (msg_110524);
  var f_w4w_3$dis : Double = w_3$dis;
  var f_w4fi : ABSFuture[List[Information]] = fi;
  var f_w4n : Node = n;
  var f_w4w_3$reachedEnd : Boolean = w_3$reachedEnd;
  var f_w4w_3$tmp741236338 : ABSFuture[Graph.Node] = w_3$tmp741236338;
  var f_w4w_3$ev : NextEvent = w_3$ev;
  if(fi.isDone()) {
    return this.TrainImplarriveFrontAwait3(f_w4w_3$dis, f_w4fi, f_w4n, f_w4w_3$reachedEnd, f_w4w_3$tmp741236338, f_w4w_3$ev);
  }
  else {
    return spawn(Guard.convert(fi),()=>this.TrainImplarriveFrontAwait3(f_w4w_3$dis, f_w4fi, f_w4n, f_w4w_3$reachedEnd, f_w4w_3$tmp741236338, f_w4w_3$ev));
  }
  return ABSFuture.done();
}
def TrainImplarriveFrontAwait3( dis : Double,  fi : ABSFuture[List[Information]],  n : Node,  reachedEnd : Boolean,  tmp741236338 : ABSFuture[Graph.Node],  ev : NextEvent): ABSFuture[Void]= {
  var w_4$ev : NextEvent =  ev;
  var w_4$fi : ABSFuture[List[Information]] =  fi;
  var w_4$reachedEnd : Boolean =  reachedEnd;
  var w_4$tmp741236338 : ABSFuture[Graph.Node] =  tmp741236338;
  var w_4$n : Node =  n;
  var w_4$dis : Double =  dis;
  var f_w5w_4$dis : Double = w_4$dis;
  var f_w5w_4$fi : ABSFuture[List[Information]] = w_4$fi;
  var f_w5w_4$n : Node = w_4$n;
  var f_w5w_4$reachedEnd : Boolean = w_4$reachedEnd;
  var f_w5w_4$tmp741236338 : ABSFuture[Graph.Node] = w_4$tmp741236338;
  var f_w5w_4$ev : NextEvent = w_4$ev;
  var TrainImplarriveFrontAwait4m: CallableGet[Void, List[Information]] = (li)=>this.TrainImplarriveFrontAwait4(f_w5w_4$dis, f_w5w_4$fi, f_w5w_4$n, f_w5w_4$reachedEnd, f_w5w_4$tmp741236338, f_w5w_4$ev, li);
  return getSpawn(w_4$fi, TrainImplarriveFrontAwait4m, Actor.HIGH_PRIORITY, true);
  return ABSFuture.done();
}
def TrainImplarriveFrontAwait4( dis : Double,  fi : ABSFuture[List[Information]],  n : Node,  reachedEnd : Boolean,  tmp741236338 : ABSFuture[Graph.Node],  ev : NextEvent,  fivalue : List[Information]): ABSFuture[Void]= {
  var w_5$ev : NextEvent =  ev;
  var w_5$fi : ABSFuture[List[Information]] =  fi;
  var w_5$fivalue : List[Information] =  fivalue;
  var w_5$reachedEnd : Boolean =  reachedEnd;
  var w_5$tmp741236338 : ABSFuture[Graph.Node] =  tmp741236338;
  var w_5$n : Node =  n;
  var w_5$dis : Double =  dis;
  var li : List[Information] = fivalue;
  var msg_112781303901 : Callable[ABSFuture[Edge]] = () => w_5$n.getOut(this.posFront);
  var f : ABSFuture[Edge] = w_5$n.send (msg_112781303901);
  var f_w6w_5$dis : Double = w_5$dis;
  var f_w6f : ABSFuture[Edge] = f;
  var f_w6w_5$fi : ABSFuture[List[Information]] = w_5$fi;
  var f_w6li : List[Information] = li;
  var f_w6w_5$n : Node = w_5$n;
  var f_w6w_5$reachedEnd : Boolean = w_5$reachedEnd;
  var f_w6w_5$tmp741236338 : ABSFuture[Graph.Node] = w_5$tmp741236338;
  var f_w6w_5$ev : NextEvent = w_5$ev;
  if(f.isDone()) {
    return this.TrainImplarriveFrontAwait5(f_w6w_5$dis, f_w6f, f_w6w_5$fi, f_w6li, f_w6w_5$n, f_w6w_5$reachedEnd, f_w6w_5$tmp741236338, f_w6w_5$ev);
  }
  else {
    return spawn(Guard.convert(f),()=>this.TrainImplarriveFrontAwait5(f_w6w_5$dis, f_w6f, f_w6w_5$fi, f_w6li, f_w6w_5$n, f_w6w_5$reachedEnd, f_w6w_5$tmp741236338, f_w6w_5$ev));
  }
  return ABSFuture.done();
}
def TrainImplarriveFrontAwait5( dis : Double,  f : ABSFuture[Edge],  fi : ABSFuture[List[Information]],  li : List[Information],  n : Node,  reachedEnd : Boolean,  tmp741236338 : ABSFuture[Graph.Node],  ev : NextEvent): ABSFuture[Void]= {
  var w_6$ev : NextEvent =  ev;
  var w_6$fi : ABSFuture[List[Information]] =  fi;
  var w_6$f : ABSFuture[Edge] =  f;
  var w_6$li : List[Information] =  li;
  var w_6$reachedEnd : Boolean =  reachedEnd;
  var w_6$tmp741236338 : ABSFuture[Graph.Node] =  tmp741236338;
  var w_6$n : Node =  n;
  var w_6$dis : Double =  dis;
  var f_w7w_6$dis : Double = w_6$dis;
  var f_w7w_6$f : ABSFuture[Edge] = w_6$f;
  var f_w7w_6$fi : ABSFuture[List[Information]] = w_6$fi;
  var f_w7w_6$li : List[Information] = w_6$li;
  var f_w7w_6$n : Node = w_6$n;
  var f_w7w_6$reachedEnd : Boolean = w_6$reachedEnd;
  var f_w7w_6$tmp741236338 : ABSFuture[Graph.Node] = w_6$tmp741236338;
  var f_w7w_6$ev : NextEvent = w_6$ev;
  var TrainImplarriveFrontAwait6m: CallableGet[Void, Edge] = (nextFront)=>this.TrainImplarriveFrontAwait6(f_w7w_6$dis, f_w7w_6$f, f_w7w_6$fi, f_w7w_6$li, f_w7w_6$n, f_w7w_6$reachedEnd, f_w7w_6$tmp741236338, f_w7w_6$ev, nextFront);
  return getSpawn(w_6$f, TrainImplarriveFrontAwait6m, Actor.HIGH_PRIORITY, true);
  return ABSFuture.done();
}
def TrainImplarriveFrontAwait6( dis : Double,  f : ABSFuture[Edge],  fi : ABSFuture[List[Information]],  li : List[Information],  n : Node,  reachedEnd : Boolean,  tmp741236338 : ABSFuture[Graph.Node],  ev : NextEvent,  fvalue : Edge): ABSFuture[Void]= {
  var w_7$ev : NextEvent =  ev;
  var w_7$fvalue : Edge =  fvalue;
  var w_7$fi : ABSFuture[List[Information]] =  fi;
  var w_7$f : ABSFuture[Edge] =  f;
  var w_7$li : List[Information] =  li;
  var w_7$reachedEnd : Boolean =  reachedEnd;
  var w_7$tmp741236338 : ABSFuture[Graph.Node] =  tmp741236338;
  var w_7$n : Node =  n;
  var w_7$dis : Double =  dis;
  var nextFront : Edge = fvalue;
  var setNew : Boolean =  true;
;
  var j : Int =  0;
;
  while ((j < length(w_7$li))) {
    var w_7$i : Information =  nth(w_7$li, j);
;
    this.processInfo(w_7$i);
    j = (j + 1);
  }
  if (((Objects.equals(nextFront, null)) || ((this.ttl > 0) && (timeValue(now()) > this.ttl)))) {
    w_7$reachedEnd = true;
    var msg_783956603956 : Callable[ABSFuture[Void]] = () => this.posFront.trainLeaves(this);
    this.posFront.send(msg_783956603956);
println(((((("MV;TERM;" + (this).toString()) + ";") + (timeValue(now())).toString()) + ";") + intToString(truncate(w_7$dis))));
println((((("terminated by ttl or error at " + (timeValue(now())).toString()) + "s with ttl=") + (this.ttl).toString()) + "s"));
  }
  else {
    this.posFront = nextFront;
    var msg_783956603391 : Callable[ABSFuture[Void]] = () => this.posFront.trainEnters(this);
    this.posFront.send(msg_783956603391);
    this.locFront = 0;
    this.nodeFront = w_7$n;
    this.locBack = (this.locBack + w_7$dis);
println(((((((("MV;REACHSTART;" + (this).toString()) + ";") + (timeValue(now())).toString()) + ";") + (this.posFront).toString()) + ";") + intToString(truncate(w_7$dis))));
    var msg_3559070735 : ABSFuture[Void] = this.detNext();
;
    var f_w8w_7$dis : Double = w_7$dis;
    var f_w8w_7$f : ABSFuture[Edge] = w_7$f;
    var f_w8w_7$fi : ABSFuture[List[Information]] = w_7$fi;
    var f_w8j : Int = j;
    var f_w8w_7$li : List[Information] = w_7$li;
    var f_w8w_7$n : Node = w_7$n;
    var f_w8nextFront : Edge = nextFront;
    var f_w8w_7$reachedEnd : Boolean = w_7$reachedEnd;
    var f_w8setNew : Boolean = setNew;
    var f_w8w_7$tmp741236338 : ABSFuture[Graph.Node] = w_7$tmp741236338;
    var f_w8w_7$ev : NextEvent = w_7$ev;
    return this.getSpawn(msg_3559070735, (msg_3559070735_par: Void)=>this.TrainImplarriveFrontAwait7(f_w8w_7$dis, f_w8w_7$f, f_w8w_7$fi, f_w8j, f_w8w_7$li, f_w8w_7$n, f_w8nextFront, f_w8w_7$reachedEnd, f_w8setNew, f_w8w_7$tmp741236338, f_w8w_7$ev), Actor.HIGH_PRIORITY, false);
  }
  return ABSFuture.done();
}
def TrainImplarriveFrontAwait7( dis : Double,  f : ABSFuture[Edge],  fi : ABSFuture[List[Information]],  j : Int,  li : List[Information],  n : Node,  nextFront : Edge,  reachedEnd : Boolean,  setNew : Boolean,  tmp741236338 : ABSFuture[Graph.Node],  ev : NextEvent): ABSFuture[Void]= {
  var w_8$ev : NextEvent =  ev;
  var w_8$fi : ABSFuture[List[Information]] =  fi;
  var w_8$f : ABSFuture[Edge] =  f;
  var w_8$j : Int =  j;
  var w_8$nextFront : Edge =  nextFront;
  var w_8$setNew : Boolean =  setNew;
  var w_8$li : List[Information] =  li;
  var w_8$reachedEnd : Boolean =  reachedEnd;
  var w_8$tmp741236338 : ABSFuture[Graph.Node] =  tmp741236338;
  var w_8$n : Node =  n;
  var w_8$dis : Double =  dis;
  return ABSFuture.done();
}
def TrainImplarriveBackAwait0( ev : NextEvent): ABSFuture[Void]= {
  var w_1$ev : NextEvent =  ev;
  if ((Objects.equals(counter(w_1$ev), this.emergCount))) {
    this.pGen.handleEv(w_1$ev);
    this.distanceTotal = (this.distanceTotal + ll(w_1$ev));
    var w_1$reachedEnd : Boolean =  false;
;
    var msg_1498595851663 : Callable[ABSFuture[Graph.Node]] = () => this.posBack.getTo(this.nodeBack);
    var w_1$tmp2080635344 : ABSFuture[Graph.Node] = this.posBack.send (msg_1498595851663);
    var f_w2w_1$reachedEnd : Boolean = w_1$reachedEnd;
    var f_w2w_1$tmp2080635344 : ABSFuture[Graph.Node] = w_1$tmp2080635344;
    var f_w2w_1$ev : NextEvent = w_1$ev;
    if(w_1$tmp2080635344.isDone()) {
      return this.TrainImplarriveBackAwait1(f_w2w_1$reachedEnd, f_w2w_1$tmp2080635344, f_w2w_1$ev);
    }
    else {
      return spawn(Guard.convert(w_1$tmp2080635344),()=>this.TrainImplarriveBackAwait1(f_w2w_1$reachedEnd, f_w2w_1$tmp2080635344, f_w2w_1$ev));
    }
  }
  return ABSFuture.done();
}
def TrainImplarriveBackAwait1( reachedEnd : Boolean,  tmp2080635344 : ABSFuture[Graph.Node],  ev : NextEvent): ABSFuture[Void]= {
  var w_2$ev : NextEvent =  ev;
  var w_2$reachedEnd : Boolean =  reachedEnd;
  var w_2$tmp2080635344 : ABSFuture[Graph.Node] =  tmp2080635344;
  var n : Node = w_2$tmp2080635344.getOrNull();
  var msg_110374 : Callable[ABSFuture[List[Information]]] = () => n.triggerBack(this, now(), this.posBack);
  var fi : ABSFuture[List[Information]] = n.send (msg_110374);
  var f_w3fi : ABSFuture[List[Information]] = fi;
  var f_w3n : Node = n;
  var f_w3w_2$reachedEnd : Boolean = w_2$reachedEnd;
  var f_w3w_2$tmp2080635344 : ABSFuture[Graph.Node] = w_2$tmp2080635344;
  var f_w3w_2$ev : NextEvent = w_2$ev;
  if(fi.isDone()) {
    return this.TrainImplarriveBackAwait2(f_w3fi, f_w3n, f_w3w_2$reachedEnd, f_w3w_2$tmp2080635344, f_w3w_2$ev);
  }
  else {
    return spawn(Guard.convert(fi),()=>this.TrainImplarriveBackAwait2(f_w3fi, f_w3n, f_w3w_2$reachedEnd, f_w3w_2$tmp2080635344, f_w3w_2$ev));
  }
  return ABSFuture.done();
}
def TrainImplarriveBackAwait2( fi : ABSFuture[List[Information]],  n : Node,  reachedEnd : Boolean,  tmp2080635344 : ABSFuture[Graph.Node],  ev : NextEvent): ABSFuture[Void]= {
  var w_3$ev : NextEvent =  ev;
  var w_3$fi : ABSFuture[List[Information]] =  fi;
  var w_3$reachedEnd : Boolean =  reachedEnd;
  var w_3$n : Node =  n;
  var w_3$tmp2080635344 : ABSFuture[Graph.Node] =  tmp2080635344;
  var f_w4w_3$fi : ABSFuture[List[Information]] = w_3$fi;
  var f_w4w_3$n : Node = w_3$n;
  var f_w4w_3$reachedEnd : Boolean = w_3$reachedEnd;
  var f_w4w_3$tmp2080635344 : ABSFuture[Graph.Node] = w_3$tmp2080635344;
  var f_w4w_3$ev : NextEvent = w_3$ev;
  var TrainImplarriveBackAwait3m: CallableGet[Void, List[Information]] = (li)=>this.TrainImplarriveBackAwait3(f_w4w_3$fi, f_w4w_3$n, f_w4w_3$reachedEnd, f_w4w_3$tmp2080635344, f_w4w_3$ev, li);
  return getSpawn(w_3$fi, TrainImplarriveBackAwait3m, Actor.HIGH_PRIORITY, true);
  return ABSFuture.done();
}
def TrainImplarriveBackAwait3( fi : ABSFuture[List[Information]],  n : Node,  reachedEnd : Boolean,  tmp2080635344 : ABSFuture[Graph.Node],  ev : NextEvent,  fivalue : List[Information]): ABSFuture[Void]= {
  var w_4$ev : NextEvent =  ev;
  var w_4$fi : ABSFuture[List[Information]] =  fi;
  var w_4$fivalue : List[Information] =  fivalue;
  var w_4$reachedEnd : Boolean =  reachedEnd;
  var w_4$n : Node =  n;
  var w_4$tmp2080635344 : ABSFuture[Graph.Node] =  tmp2080635344;
  var li : List[Information] = fivalue;
  var oldBack : Edge =  this.posBack;
;
  var msg_112780342415 : Callable[ABSFuture[Edge]] = () => w_4$n.getOut(this.posBack);
  var fe : ABSFuture[Edge] = w_4$n.send (msg_112780342415);
  var f_w5fe : ABSFuture[Edge] = fe;
  var f_w5w_4$fi : ABSFuture[List[Information]] = w_4$fi;
  var f_w5li : List[Information] = li;
  var f_w5w_4$n : Node = w_4$n;
  var f_w5oldBack : Edge = oldBack;
  var f_w5w_4$reachedEnd : Boolean = w_4$reachedEnd;
  var f_w5w_4$tmp2080635344 : ABSFuture[Graph.Node] = w_4$tmp2080635344;
  var f_w5w_4$ev : NextEvent = w_4$ev;
  if(fe.isDone()) {
    return this.TrainImplarriveBackAwait4(f_w5fe, f_w5w_4$fi, f_w5li, f_w5w_4$n, f_w5oldBack, f_w5w_4$reachedEnd, f_w5w_4$tmp2080635344, f_w5w_4$ev);
  }
  else {
    return spawn(Guard.convert(fe),()=>this.TrainImplarriveBackAwait4(f_w5fe, f_w5w_4$fi, f_w5li, f_w5w_4$n, f_w5oldBack, f_w5w_4$reachedEnd, f_w5w_4$tmp2080635344, f_w5w_4$ev));
  }
  return ABSFuture.done();
}
def TrainImplarriveBackAwait4( fe : ABSFuture[Edge],  fi : ABSFuture[List[Information]],  li : List[Information],  n : Node,  oldBack : Edge,  reachedEnd : Boolean,  tmp2080635344 : ABSFuture[Graph.Node],  ev : NextEvent): ABSFuture[Void]= {
  var w_5$oldBack : Edge =  oldBack;
  var w_5$ev : NextEvent =  ev;
  var w_5$fi : ABSFuture[List[Information]] =  fi;
  var w_5$li : List[Information] =  li;
  var w_5$reachedEnd : Boolean =  reachedEnd;
  var w_5$n : Node =  n;
  var w_5$fe : ABSFuture[Edge] =  fe;
  var w_5$tmp2080635344 : ABSFuture[Graph.Node] =  tmp2080635344;
  var f_w6w_5$fe : ABSFuture[Edge] = w_5$fe;
  var f_w6w_5$fi : ABSFuture[List[Information]] = w_5$fi;
  var f_w6w_5$li : List[Information] = w_5$li;
  var f_w6w_5$n : Node = w_5$n;
  var f_w6w_5$oldBack : Edge = w_5$oldBack;
  var f_w6w_5$reachedEnd : Boolean = w_5$reachedEnd;
  var f_w6w_5$tmp2080635344 : ABSFuture[Graph.Node] = w_5$tmp2080635344;
  var f_w6w_5$ev : NextEvent = w_5$ev;
  var TrainImplarriveBackAwait5m: CallableGet[Void, Graph.Edge] = (thisposBack)=>this.TrainImplarriveBackAwait5(f_w6w_5$fe, f_w6w_5$fi, f_w6w_5$li, f_w6w_5$n, f_w6w_5$oldBack, f_w6w_5$reachedEnd, f_w6w_5$tmp2080635344, f_w6w_5$ev, thisposBack);
  return getSpawn(w_5$fe, TrainImplarriveBackAwait5m, Actor.HIGH_PRIORITY, true);
  return ABSFuture.done();
}
def TrainImplarriveBackAwait5( fe : ABSFuture[Edge],  fi : ABSFuture[List[Information]],  li : List[Information],  n : Node,  oldBack : Edge,  reachedEnd : Boolean,  tmp2080635344 : ABSFuture[Graph.Node],  ev : NextEvent,  fevalue : Graph.Edge): ABSFuture[Void]= {
  var w_6$oldBack : Edge =  oldBack;
  var w_6$ev : NextEvent =  ev;
  var w_6$fi : ABSFuture[List[Information]] =  fi;
  var w_6$fevalue : Graph.Edge =  fevalue;
  var w_6$li : List[Information] =  li;
  var w_6$reachedEnd : Boolean =  reachedEnd;
  var w_6$n : Node =  n;
  var w_6$fe : ABSFuture[Edge] =  fe;
  var w_6$tmp2080635344 : ABSFuture[Graph.Node] =  tmp2080635344;
  posBack = fevalue;
  def TrainImplarriveBackAwait5( fe : ABSFuture[Edge],  fi : ABSFuture[List[Information]],  li : List[Information],  n : Node,  oldBack : Edge,  reachedEnd : Boolean,  tmp2080635344 : ABSFuture[Graph.Node],  ev : NextEvent,  fevalue : Graph.Edge,  fevalue : Graph.Edge): ABSFuture[Void]= {
    var w_6$oldBack : Edge =  oldBack;
    var w_6$ev : NextEvent =  ev;
    var w_6$fi : ABSFuture[List[Information]] =  fi;
    var w_6$fevalue : Graph.Edge =  fevalue;
    var w_6$li : List[Information] =  li;
    var w_6$reachedEnd : Boolean =  reachedEnd;
    var w_6$n : Node =  n;
    var w_6$fe : ABSFuture[Edge] =  fe;
    var w_6$tmp2080635344 : ABSFuture[Graph.Node] =  tmp2080635344;
    var j : Int =  0;
;
    while ((j < length(w_6$li))) {
      var w_6$i : Information =  nth(w_6$li, j);
;
      this.processInfo(w_6$i);
      j = (j + 1);
    }
    if ((Objects.equals(this.posBack, null))) {
      w_6$reachedEnd = true;
println(((((("MV;TERM;" + (this).toString()) + ";") + (timeValue(now())).toString()) + ";") + intToString(truncate(ll(w_6$ev)))));
    }
    else {
      var msg_1835632504358 : Callable[ABSFuture[Void]] = () => w_6$oldBack.trainLeaves(this);
      w_6$oldBack.send(msg_1835632504358);
      this.nodeBack = w_6$n;
      this.locFront = (this.locFront + ll(w_6$ev));
      this.locBack = 0;
      var msg_1498595851293 : Callable[ABSFuture[Void]] = () => this.posBack.trainFullyEnters(this);
      this.posBack.send(msg_1498595851293);
println(((((((("MV;LEAVES;" + (this).toString()) + ";") + (timeValue(now())).toString()) + ";") + (this.posBack).toString()) + ";") + intToString(truncate(ll(w_6$ev)))));
    }
    if ((( ! this.vwait) && ( ! w_6$reachedEnd))) {
      var msg_3559070124 : ABSFuture[Void] = this.detNext();
;
      var f_w7w_6$fe : ABSFuture[Edge] = w_6$fe;
      var f_w7w_6$fi : ABSFuture[List[Information]] = w_6$fi;
      var f_w7j : Int = j;
      var f_w7w_6$li : List[Information] = w_6$li;
      var f_w7w_6$n : Node = w_6$n;
      var f_w7w_6$oldBack : Edge = w_6$oldBack;
      var f_w7w_6$reachedEnd : Boolean = w_6$reachedEnd;
      var f_w7w_6$tmp2080635344 : ABSFuture[Graph.Node] = w_6$tmp2080635344;
      var f_w7w_6$ev : NextEvent = w_6$ev;
      return this.getSpawn(msg_3559070124, (msg_3559070124_par: Void)=>this.TrainImplarriveBackAwait6(f_w7w_6$fe, f_w7w_6$fi, f_w7j, f_w7w_6$li, f_w7w_6$n, f_w7w_6$oldBack, f_w7w_6$reachedEnd, f_w7w_6$tmp2080635344, f_w7w_6$ev), Actor.HIGH_PRIORITY, false);
    }
    return ABSFuture.done();
  }
def TrainImplarriveBackAwait6( fe : ABSFuture[Edge],  fi : ABSFuture[List[Information]],  j : Int,  li : List[Information],  n : Node,  oldBack : Edge,  reachedEnd : Boolean,  tmp2080635344 : ABSFuture[Graph.Node],  ev : NextEvent): ABSFuture[Void]= {
  var w_7$oldBack : Edge =  oldBack;
  var w_7$ev : NextEvent =  ev;
  var w_7$fi : ABSFuture[List[Information]] =  fi;
  var w_7$j : Int =  j;
  var w_7$li : List[Information] =  li;
  var w_7$reachedEnd : Boolean =  reachedEnd;
  var w_7$n : Node =  n;
  var w_7$fe : ABSFuture[Edge] =  fe;
  var w_7$tmp2080635344 : ABSFuture[Graph.Node] =  tmp2080635344;
  return ABSFuture.done();
}
def TrainImplacqStopAwait0( tmp593573468 : ABSFuture[Void]): ABSFuture[Time]= {
  var w_1$tmp593573468 : ABSFuture[Void] =  tmp593573468;
;
  return ABSFuture.done(this.lastStop);
}
{
    moveToCOG(destCOG);
println(((((("ZUG;" + (this).toString()) + ";") + this.name) + ";") + intToString(this.lengthn)));
    this.pGen = new ClassPhysicsGenerator(this , this, this.name);
    this.pzbGen = new ClassPZBGenerator(this , this, this.pGen, this.name);
  }

}
