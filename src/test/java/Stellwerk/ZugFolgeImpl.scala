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

class ZugFolgeImpl(var destCOG : LocalActor,var name : String) extends LocalActor with ActiveZugFolge {
  private final var serialVersionUID : java.lang.Long =  1L;


  var inDesc : List[Triple[ZugFolge, Strecke, Signal]] =  Nil[Triple[ZugFolge, Strecke, Signal]]();

  var outDesc : List[Triple[Signal, Strecke, ZugFolge]] =  Nil[Triple[Signal, Strecke, ZugFolge]]();

  var inLocked : Map[Strecke, Boolean] =  EmptyMap[Strecke, Boolean]();

  var outLocked : Map[Strecke, Boolean] =  EmptyMap[Strecke, Boolean]();

  var broken : Map[Strecke, Boolean] =  EmptyMap[Strecke, Boolean]();

  var brokenHere : Map[Signal, Boolean] =  EmptyMap[Signal, Boolean]();

  var counter : Int =  0;

  var inProg : List[VorblockEv] =  Nil[VorblockEv]();

  var obsolete : Set[VorblockEv] =  EmptySet[VorblockEv]();

  def rueckblock( zf : ZugFolge): ABSFuture[Void]= {
    var stTo : Strecke =  extractOutStrecke(this.outDesc, zf);
;
    this.outLocked = put(this.outLocked, stTo, false);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def tellTime(): ABSFuture[Void]= {
println((now()).toString());
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def listen( f : ABSFuture[Pair[Train, Strecke]]): ABSFuture[Void]= {
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def actSignal( s : Signal,  dur : Int): ABSFuture[Void]= {
    var zf : Strecke =  extractLastStrecke(this.inDesc, s);
;
    var f_w1zf : Strecke = zf;
    var f_w1dur : Int = dur;
    var f_w1s : Signal = s;
    if(new Supplier[Boolean] {
  def get(): Boolean= {
    return (lookupUnsafe(inLocked, extractLastStrecke(inDesc, s)) && ( ! lookupUnsafe(outLocked, extractNextStrecke(outDesc, s))));
  }
}
.get()) {
      return this.ZugFolgeImplactSignalAwait0(f_w1zf, f_w1dur, f_w1s);
    }
    else {
      return spawn(Guard.convert(new Supplier[Boolean] {
            def get(): Boolean= {
              return (lookupUnsafe(inLocked, extractLastStrecke(inDesc, s)) && ( ! lookupUnsafe(outLocked, extractNextStrecke(outDesc, s))));
            }
          }
          ),()=>this.ZugFolgeImplactSignalAwait0(f_w1zf, f_w1dur, f_w1s));
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def addSignalZf( zf : ZugFolge,  st : Strecke,  s : Signal,  st2 : Strecke,  zf2 : ZugFolge): ABSFuture[Void]= {
    this.inDesc = Cons(TripleOf(zf, st, s), this.inDesc);
    this.outDesc = Cons(TripleOf(s, st2, zf2), this.outDesc);
    this.inLocked = put(this.inLocked, st, false);
    this.outLocked = put(this.outLocked, st2, false);
    this.broken = put(this.broken, st2, false);
    this.brokenHere = put(this.brokenHere, s, false);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def continueTrain( s : Signal): ABSFuture[Void]= {
    var msg_115719 : Callable[ABSFuture[Train.Train]] = () => s.getObserver();
    var tmp640929948 : ABSFuture[Train.Train] = s.send (msg_115719);
    var f_w1tmp640929948 : ABSFuture[Train.Train] = tmp640929948;
    var f_w1s : Signal = s;
    if(tmp640929948.isDone()) {
      return this.ZugFolgeImplcontinueTrainAwait0(f_w1tmp640929948, f_w1s);
    }
    else {
      return spawn(Guard.convert(tmp640929948),()=>this.ZugFolgeImplcontinueTrainAwait0(f_w1tmp640929948, f_w1s));
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def vorblock( zf : ZugFolge,  st : Strecke): ABSFuture[Void]= {
    var s : Signal =  extractSignal(this.inDesc, zf, st);
;
    var st2 : Strecke =  extractNextStrecke(this.outDesc, s);
;
    var ev : VorblockEv =  VEvent(zf, st2, this.counter);
;
    this.inProg = Cons(ev, this.inProg);
    this.counter = (this.counter + 1);
    if (lookupUnsafe(this.broken, st2)) {
      var next : ZugFolge =  extractNextFolge(this.outDesc, s);
;
      var n : Double =  timeValue(now());
;
      var msg_3377907783 : Callable[ABSFuture[Void]] = () => next.reqFree(this, st2);
      var tmp1333929103 : ABSFuture[Void] = next.send (msg_3377907783);
      var f_w1n : Double = n;
      var f_w1next : ZugFolge = next;
      var f_w1tmp1333929103 : ABSFuture[Void] = tmp1333929103;
      var f_w1ev : VorblockEv = ev;
      var f_w1s : Signal = s;
      var f_w1st2 : Strecke = st2;
      var f_w1st : Strecke = st;
      var f_w1zf : ZugFolge = zf;
      if(tmp1333929103.isDone()) {
        return this.ZugFolgeImplvorblockAwait0(f_w1n, f_w1next, f_w1tmp1333929103, f_w1ev, f_w1s, f_w1st2, f_w1st, f_w1zf);
      }
      else {
        return spawn(Guard.convert(tmp1333929103),()=>this.ZugFolgeImplvorblockAwait0(f_w1n, f_w1next, f_w1tmp1333929103, f_w1ev, f_w1s, f_w1st2, f_w1st, f_w1zf));
      }
    }
    else {
      var n : Double =  timeValue(now());
;
      var f_w3n : Double = n;
      var f_w3ev : VorblockEv = ev;
      var f_w3s : Signal = s;
      var f_w3st2 : Strecke = st2;
      var f_w3st : Strecke = st;
      var f_w3zf : ZugFolge = zf;
      if(new Supplier[Boolean] {
  def get(): Boolean= {
    return (( ! lookupUnsafe(outLocked, st2)) || contains(obsolete, ev));
  }
}
.get()) {
        return this.ZugFolgeImplvorblockAwait2(f_w3n, f_w3ev, f_w3s, f_w3st2, f_w3st, f_w3zf);
      }
      else {
        return spawn(Guard.convert(new Supplier[Boolean] {
              def get(): Boolean= {
                return (( ! lookupUnsafe(outLocked, st2)) || contains(obsolete, ev));
              }
            }
            ),()=>this.ZugFolgeImplvorblockAwait2(f_w3n, f_w3ev, f_w3s, f_w3st2, f_w3st, f_w3zf));
      }
    }
    this.inProg = without(this.inProg, ev);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def reqFree( zf : ActiveZugFolge,  st : Strecke): ABSFuture[Void]= {
    var f_w1st : Strecke = st;
    var f_w1zf : ActiveZugFolge = zf;
    if(new Supplier[Boolean] {
  def get(): Boolean= {
    return ( ! lookupUnsafe(inLocked, st));
  }
}
.get()) {
      return this.ZugFolgeImplreqFreeAwait0(f_w1st, f_w1zf);
    }
    else {
      return spawn(Guard.convert(new Supplier[Boolean] {
            def get(): Boolean= {
              return ( ! lookupUnsafe(inLocked, st));
            }
          }
          ),()=>this.ZugFolgeImplreqFreeAwait0(f_w1st, f_w1zf));
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def req( train : Train): ABSFuture[Void]= {
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def triggered( s : Signal): ABSFuture[Void]= {
    var msg_115398 : Callable[ABSFuture[Boolean]] = () => s.sperren(now());
    var tmp1400132053 : ABSFuture[Boolean] = s.send (msg_115398);
    var f_w1tmp1400132053 : ABSFuture[Boolean] = tmp1400132053;
    var f_w1s : Signal = s;
    if(tmp1400132053.isDone()) {
      return this.ZugFolgeImpltriggeredAwait0(f_w1tmp1400132053, f_w1s);
    }
    else {
      return spawn(Guard.convert(tmp1400132053),()=>this.ZugFolgeImpltriggeredAwait0(f_w1tmp1400132053, f_w1s));
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def nextZfFailed( zf : ActiveZugFolge,  st : Strecke): ABSFuture[Void]= {
    this.broken = put(this.broken, st, true);
    var i : Int =  0;
;
    while ((i < length(this.inProg))) {
      var ev : VorblockEv =  nth(this.inProg, i);
;
      ev match  {
        case VEvent(zf2, st2, j) => if ((Objects.equals(st, st2))) {
              this.obsolete = insertElement(this.obsolete, ev);
            }
            ;
        case _ => ;
      }
      i = (i + 1);
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }


def ZugFolgeImplactSignalAwait0( zf : Strecke,  dur : Int,  s : Signal): ABSFuture[Void]= {
  var w_1$dur : Int =  dur;
  var w_1$s : Signal =  s;
  var w_1$zf : Strecke =  zf;
  var msg_112777464728 : Callable[ABSFuture[Boolean]] = () => w_1$s.freischalten();
  w_1$s.send(msg_112777464728);
  return ABSFuture.done();
}
def ZugFolgeImplcontinueTrainAwait0( tmp640929948 : ABSFuture[Train.Train],  s : Signal): ABSFuture[Void]= {
  var w_1$s : Signal =  s;
  var w_1$tmp640929948 : ABSFuture[Train.Train] =  tmp640929948;
  var train : Train = w_1$tmp640929948.getOrNull();
  var msg_110621192777 : Callable[ABSFuture[ABS.StdLib.Time]] = () => train.acqStop();
  var tmp1377593328 : ABSFuture[ABS.StdLib.Time] = train.send (msg_110621192777);
  var f_w2tmp1377593328 : ABSFuture[ABS.StdLib.Time] = tmp1377593328;
  var f_w2w_1$tmp640929948 : ABSFuture[Train.Train] = w_1$tmp640929948;
  var f_w2train : Train = train;
  var f_w2w_1$s : Signal = w_1$s;
  if(tmp1377593328.isDone()) {
    return this.ZugFolgeImplcontinueTrainAwait1(f_w2tmp1377593328, f_w2w_1$tmp640929948, f_w2train, f_w2w_1$s);
  }
  else {
    return spawn(Guard.convert(tmp1377593328),()=>this.ZugFolgeImplcontinueTrainAwait1(f_w2tmp1377593328, f_w2w_1$tmp640929948, f_w2train, f_w2w_1$s));
  }
  return ABSFuture.done();
}
def ZugFolgeImplcontinueTrainAwait1( tmp1377593328 : ABSFuture[ABS.StdLib.Time],  tmp640929948 : ABSFuture[Train.Train],  train : Train,  s : Signal): ABSFuture[Void]= {
  var w_2$s : Signal =  s;
  var w_2$tmp1377593328 : ABSFuture[ABS.StdLib.Time] =  tmp1377593328;
  var w_2$tmp640929948 : ABSFuture[Train.Train] =  tmp640929948;
  var w_2$train : Train =  train;
;
  var msg_291322702333 : Callable[ABSFuture[Void]] = () => w_2$train.order(Cons(Ord144(), Cons(Ord2(), Nil())));
  w_2$train.send(msg_291322702333);
  return ABSFuture.done();
}
def ZugFolgeImplvorblockAwait0( n : Double,  next : ZugFolge,  tmp1333929103 : ABSFuture[Void],  ev : VorblockEv,  s : Signal,  st2 : Strecke,  st : Strecke,  zf : ZugFolge): ABSFuture[Void]= {
  var w_1$next : ZugFolge =  next;
  var w_1$st2 : Strecke =  st2;
  var w_1$ev : VorblockEv =  ev;
  var w_1$st : Strecke =  st;
  var w_1$s : Signal =  s;
  var w_1$tmp1333929103 : ABSFuture[Void] =  tmp1333929103;
  var w_1$zf : ZugFolge =  zf;
  var w_1$n : Double =  n;
;
  this.inLocked = put(this.inLocked, w_1$st, true);
  var msg_3559070611 : ABSFuture[Void] = this.continueTrain(w_1$s);
;
  var f_w2w_1$n : Double = w_1$n;
  var f_w2w_1$next : ZugFolge = w_1$next;
  var f_w2w_1$tmp1333929103 : ABSFuture[Void] = w_1$tmp1333929103;
  var f_w2w_1$ev : VorblockEv = w_1$ev;
  var f_w2w_1$s : Signal = w_1$s;
  var f_w2w_1$st2 : Strecke = w_1$st2;
  var f_w2w_1$st : Strecke = w_1$st;
  var f_w2w_1$zf : ZugFolge = w_1$zf;
  return this.getSpawn(msg_3559070611, (msg_3559070611_par: Void)=>this.ZugFolgeImplvorblockAwait1(f_w2w_1$n, f_w2w_1$next, f_w2w_1$tmp1333929103, f_w2w_1$ev, f_w2w_1$s, f_w2w_1$st2, f_w2w_1$st, f_w2w_1$zf), Actor.HIGH_PRIORITY, false);
  return ABSFuture.done();
}
def ZugFolgeImplvorblockAwait1( n : Double,  next : ZugFolge,  tmp1333929103 : ABSFuture[Void],  ev : VorblockEv,  s : Signal,  st2 : Strecke,  st : Strecke,  zf : ZugFolge): ABSFuture[Void]= {
  var w_2$next : ZugFolge =  next;
  var w_2$st2 : Strecke =  st2;
  var w_2$ev : VorblockEv =  ev;
  var w_2$st : Strecke =  st;
  var w_2$s : Signal =  s;
  var w_2$tmp1333929103 : ABSFuture[Void] =  tmp1333929103;
  var w_2$zf : ZugFolge =  zf;
  var w_2$n : Double =  n;
  this.inProg = without(this.inProg, w_2$ev);
  return ABSFuture.done();
}
def ZugFolgeImplvorblockAwait2( n : Double,  ev : VorblockEv,  s : Signal,  st2 : Strecke,  st : Strecke,  zf : ZugFolge): ABSFuture[Void]= {
  var w_3$st2 : Strecke =  st2;
  var w_3$ev : VorblockEv =  ev;
  var w_3$st : Strecke =  st;
  var w_3$s : Signal =  s;
  var w_3$zf : ZugFolge =  zf;
  var w_3$n : Double =  n;
  if (contains(this.obsolete, w_3$ev)) {
    var msg_3559070882 : ABSFuture[Void] = this.vorblock(w_3$zf, w_3$st);
;
    var f_w4w_3$n : Double = w_3$n;
    var f_w4w_3$ev : VorblockEv = w_3$ev;
    var f_w4w_3$s : Signal = w_3$s;
    var f_w4w_3$st2 : Strecke = w_3$st2;
    var f_w4w_3$st : Strecke = w_3$st;
    var f_w4w_3$zf : ZugFolge = w_3$zf;
    return this.getSpawn(msg_3559070882, (msg_3559070882_par: Void)=>this.ZugFolgeImplvorblockAwait3(f_w4w_3$n, f_w4w_3$ev, f_w4w_3$s, f_w4w_3$st2, f_w4w_3$st, f_w4w_3$zf), Actor.HIGH_PRIORITY, false);
  }
  else {
    this.inLocked = put(this.inLocked, w_3$st, true);
    var msg_798806214350 : Callable[ABSFuture[Int]] = () => w_3$st.getLength();
    var w_4$tmp298424794 : ABSFuture[Int] = w_3$st.send (msg_798806214350);
    var f_w5w_4$tmp298424794 : ABSFuture[Int] = w_4$tmp298424794;
    var f_w5w_3$n : Double = w_3$n;
    var f_w5w_3$ev : VorblockEv = w_3$ev;
    var f_w5w_3$s : Signal = w_3$s;
    var f_w5w_3$st2 : Strecke = w_3$st2;
    var f_w5w_3$st : Strecke = w_3$st;
    var f_w5w_3$zf : ZugFolge = w_3$zf;
    if(w_4$tmp298424794.isDone()) {
      return this.ZugFolgeImplvorblockAwait4(f_w5w_4$tmp298424794, f_w5w_3$n, f_w5w_3$ev, f_w5w_3$s, f_w5w_3$st2, f_w5w_3$st, f_w5w_3$zf);
    }
    else {
      return spawn(Guard.convert(w_4$tmp298424794),()=>this.ZugFolgeImplvorblockAwait4(f_w5w_4$tmp298424794, f_w5w_3$n, f_w5w_3$ev, f_w5w_3$s, f_w5w_3$st2, f_w5w_3$st, f_w5w_3$zf));
    }
  }
  this.inProg = without(this.inProg, w_3$ev);
  return ABSFuture.done();
}
def ZugFolgeImplvorblockAwait3( n : Double,  ev : VorblockEv,  s : Signal,  st2 : Strecke,  st : Strecke,  zf : ZugFolge): ABSFuture[Void]= {
  var w_4$st2 : Strecke =  st2;
  var w_4$ev : VorblockEv =  ev;
  var w_4$st : Strecke =  st;
  var w_4$s : Signal =  s;
  var w_4$zf : ZugFolge =  zf;
  var w_4$n : Double =  n;
  this.inProg = without(this.inProg, w_4$ev);
  return ABSFuture.done();
}
def ZugFolgeImplvorblockAwait4( tmp298424794 : ABSFuture[Int],  n : Double,  ev : VorblockEv,  s : Signal,  st2 : Strecke,  st : Strecke,  zf : ZugFolge): ABSFuture[Void]= {
  var w_5$st2 : Strecke =  st2;
  var w_5$ev : VorblockEv =  ev;
  var w_5$st : Strecke =  st;
  var w_5$s : Signal =  s;
  var w_5$tmp298424794 : ABSFuture[Int] =  tmp298424794;
  var w_5$zf : ZugFolge =  zf;
  var w_5$n : Double =  n;
  var len : Int = w_5$tmp298424794.getOrNull();
  len = (len - truncate((timeValue(now()) - w_5$n)));
  var msg_3559070698 : ABSFuture[Void] = this.actSignal(w_5$s, (len + 5));
;
  var f_w6len : Int = len;
  var f_w6w_5$tmp298424794 : ABSFuture[Int] = w_5$tmp298424794;
  var f_w6w_5$n : Double = w_5$n;
  var f_w6w_5$ev : VorblockEv = w_5$ev;
  var f_w6w_5$s : Signal = w_5$s;
  var f_w6w_5$st2 : Strecke = w_5$st2;
  var f_w6w_5$st : Strecke = w_5$st;
  var f_w6w_5$zf : ZugFolge = w_5$zf;
  return this.getSpawn(msg_3559070698, (msg_3559070698_par: Void)=>this.ZugFolgeImplvorblockAwait5(f_w6len, f_w6w_5$tmp298424794, f_w6w_5$n, f_w6w_5$ev, f_w6w_5$s, f_w6w_5$st2, f_w6w_5$st, f_w6w_5$zf), Actor.HIGH_PRIORITY, false);
  return ABSFuture.done();
}
def ZugFolgeImplvorblockAwait5( len : Int,  tmp298424794 : ABSFuture[Int],  n : Double,  ev : VorblockEv,  s : Signal,  st2 : Strecke,  st : Strecke,  zf : ZugFolge): ABSFuture[Void]= {
  var w_6$st2 : Strecke =  st2;
  var w_6$ev : VorblockEv =  ev;
  var w_6$st : Strecke =  st;
  var w_6$s : Signal =  s;
  var w_6$len : Int =  len;
  var w_6$tmp298424794 : ABSFuture[Int] =  tmp298424794;
  var w_6$zf : ZugFolge =  zf;
  var w_6$n : Double =  n;
  this.inProg = without(this.inProg, w_6$ev);
  return ABSFuture.done();
}
def ZugFolgeImplreqFreeAwait0( st : Strecke,  zf : ActiveZugFolge): ABSFuture[Void]= {
  var w_1$st : Strecke =  st;
  var w_1$zf : ActiveZugFolge =  zf;
  var s : Signal =  extractSignal(this.inDesc, w_1$zf, w_1$st);
;
  if (lookupUnsafe(this.brokenHere, s)) {
    var w_1$st2 : Strecke =  extractNextStrecke(this.outDesc, s);
;
    var w_1$zf2 : ZugFolge =  extractNextFolge(this.outDesc, s);
;
    var msg_1004970443580 : Callable[ABSFuture[Void]] = () => w_1$zf2.reqFree(this, w_1$st2);
    var w_1$tmp514420722 : ABSFuture[Void] = w_1$zf2.send (msg_1004970443580);
    var f_w2w_1$st2 : Strecke = w_1$st2;
    var f_w2w_1$tmp514420722 : ABSFuture[Void] = w_1$tmp514420722;
    var f_w2w_1$zf2 : ZugFolge = w_1$zf2;
    var f_w2s : Signal = s;
    var f_w2w_1$st : Strecke = w_1$st;
    var f_w2w_1$zf : ActiveZugFolge = w_1$zf;
    if(w_1$tmp514420722.isDone()) {
      return this.ZugFolgeImplreqFreeAwait1(f_w2w_1$st2, f_w2w_1$tmp514420722, f_w2w_1$zf2, f_w2s, f_w2w_1$st, f_w2w_1$zf);
    }
    else {
      return spawn(Guard.convert(w_1$tmp514420722),()=>this.ZugFolgeImplreqFreeAwait1(f_w2w_1$st2, f_w2w_1$tmp514420722, f_w2w_1$zf2, f_w2s, f_w2w_1$st, f_w2w_1$zf));
    }
  }
  return ABSFuture.done();
}
def ZugFolgeImplreqFreeAwait1( st2 : Strecke,  tmp514420722 : ABSFuture[Void],  zf2 : ZugFolge,  s : Signal,  st : Strecke,  zf : ActiveZugFolge): ABSFuture[Void]= {
  var w_2$st2 : Strecke =  st2;
  var w_2$st : Strecke =  st;
  var w_2$s : Signal =  s;
  var w_2$zf2 : ZugFolge =  zf2;
  var w_2$tmp514420722 : ABSFuture[Void] =  tmp514420722;
  var w_2$zf : ActiveZugFolge =  zf;
;
  return ABSFuture.done();
}
def ZugFolgeImpltriggeredAwait0( tmp1400132053 : ABSFuture[Boolean],  s : Signal): ABSFuture[Void]= {
  var w_1$s : Signal =  s;
  var w_1$tmp1400132053 : ABSFuture[Boolean] =  tmp1400132053;
  var failed : Boolean = w_1$tmp1400132053.getOrNull();
  var st : Strecke =  extractLastStrecke(this.inDesc, w_1$s);
;
  var st2 : Strecke =  extractNextStrecke(this.outDesc, w_1$s);
;
  var next : ZugFolge =  extractNextFolge(this.outDesc, w_1$s);
;
  var last : ZugFolge =  extractLastFolge(this.inDesc, w_1$s);
;
  if (failed) {
    var msg_3314326972 : Callable[ABSFuture[Void]] = () => last.nextZfFailed(this, st);
    last.send(msg_3314326972);
    this.brokenHere = put(this.brokenHere, w_1$s, true);
  }
  else {
    var msg_3314326461 : Callable[ABSFuture[Void]] = () => last.rueckblock(this);
    last.send(msg_3314326461);
  }
  var msg_3377907787 : Callable[ABSFuture[Void]] = () => next.vorblock(this, st2);
  next.send(msg_3377907787);
  this.outLocked = put(this.outLocked, st2, true);
  this.inLocked = put(this.inLocked, st, false);
  return ABSFuture.done();
}
{
    moveToCOG(destCOG);
  }

}
