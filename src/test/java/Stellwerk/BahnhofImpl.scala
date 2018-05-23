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

class BahnhofImpl(var destCOG : LocalActor,var app : App,var name : String) extends LocalActor with ZugMelde {
  private final var serialVersionUID : java.lang.Long =  1L;


  var inDesc : List[Triple[ZugFolge, Strecke, Signal]] =  Nil[Triple[ZugFolge, Strecke, Signal]]();

  var outDesc : List[Triple[Signal, Strecke, ZugFolge]] =  Nil[Triple[Signal, Strecke, ZugFolge]]();

  var inLocked : Map[Strecke, Boolean] =  EmptyMap[Strecke, Boolean]();

  var outLocked : Map[Strecke, Boolean] =  EmptyMap[Strecke, Boolean]();

  var erlaubnis : Map[Strecke, Boolean] =  EmptyMap[Strecke, Boolean]();

  var erlaubnisUnlocked : Map[Strecke, Boolean] =  EmptyMap[Strecke, Boolean]();

  var allowed : Map[Strecke, Boolean] =  EmptyMap[Strecke, Boolean]();

  var parking : List[Pair[Train, Signal]] =  Nil[Pair[Train, Signal]]();

  var reserved : List[Triple[Train, Signal, ZugMelde]] =  Nil[Triple[Train, Signal, ZugMelde]]();

  var outMelde : List[Pair[Signal, ZugMelde]] =  Nil[Pair[Signal, ZugMelde]]();

  var inMelde : Map[Signal, ZugMelde] =  EmptyMap[Signal, ZugMelde]();

  var streckeMap : List[Pair[Strecke, Strecke]] =  Nil[Pair[Strecke, Strecke]]();

  var schedule : Map[Train, ZugFolge] =  EmptyMap[Train, ZugFolge]();

  var expectOut : Map[Strecke, List[Train]] =  EmptyMap[Strecke, List[Train]]();

  var expectIn : Map[Strecke, List[Train]] =  EmptyMap[Strecke, List[Train]]();

  var inTracks : Map[ZugMelde, Pair[Strecke, Strecke]] =  EmptyMap[ZugMelde, Pair[Strecke, Strecke]]();

  var dura : Map[Pair[ZugMelde, Signal], Int] =  EmptyMap[Pair[ZugMelde, Signal], Int]();

  var inUse : Map[Signal, Signal] =  EmptyMap[Signal, Signal]();

  var paths : Map[Pair[Signal, Signal], List[Switch]] =  EmptyMap[Pair[Signal, Signal], List[Switch]]();

  var outPaths : Map[Pair[Signal, ZugMelde], List[Switch]] =  EmptyMap[Pair[Signal, ZugMelde], List[Switch]]();

  var listen : Map[ZugMelde, List[ActiveZugFolge]] =  EmptyMap[ZugMelde, List[ActiveZugFolge]]();

  var co : Int =  0;

  var haltTime : Int =  30;

  def rueckblock( zf : ZugFolge): ABSFuture[Void]= {
    var stTo : Strecke =  extractOutStrecke(this.outDesc, zf);
;
    this.outLocked = put(this.outLocked, stTo, false);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def vorblock( zf : ZugFolge,  st : Strecke): ABSFuture[Void]= {
    var s : Signal =  extractSignal(this.inDesc, zf, st);
;
    this.inLocked = put(this.inLocked, st, true);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def advance( r : Double): ABSFuture[Void]= {
    var f_w1r : Double = r;
    if(Array(r,r)(0)<=0) {
      return this.BahnhofImpladvanceAwait0(f_w1r);
    }
    else {
      return spawn(Guard.convert(Array(r,r)),()=>this.BahnhofImpladvanceAwait0(f_w1r));
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def tellTime(): ABSFuture[Void]= {
println((now()).toString());
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def reqFree( zf : ActiveZugFolge,  st : Strecke): ABSFuture[Void]= {
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def nextZfFailed( zf : ActiveZugFolge,  st : Strecke): ABSFuture[Void]= {
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setListen( listenMap : Map[ZugMelde, List[ActiveZugFolge]]): ABSFuture[Void]= {
    this.listen = listenMap;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setPaths( pa : Map[Pair[Signal, Signal], List[Switch]]): ABSFuture[Void]= {
    this.paths = pa;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setOutPaths( pa : Map[Pair[Signal, ZugMelde], List[Switch]]): ABSFuture[Void]= {
    this.outPaths = pa;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setStreckeMap( melde : List[Pair[Strecke, Strecke]]): ABSFuture[Void]= {
    this.streckeMap = melde;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def forceErl( st2 : Strecke): ABSFuture[Void]= {
    this.erlaubnis = put(this.erlaubnis, st2, true);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def actSignal( s : Signal): ABSFuture[Void]= {
    var st : Strecke =  extractLastStrecke(this.inDesc, s);
;
    var f_w1st : Strecke = st;
    var f_w1s : Signal = s;
    if(new Supplier[Boolean] {
  def get(): Boolean= {
    return lookupUnsafe(inLocked, st);
  }
}
.get()) {
      return this.BahnhofImplactSignalAwait0(f_w1st, f_w1s);
    }
    else {
      return spawn(Guard.convert(new Supplier[Boolean] {
            def get(): Boolean= {
              return lookupUnsafe(inLocked, st);
            }
          }
          ),()=>this.BahnhofImplactSignalAwait0(f_w1st, f_w1s));
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def force( zug : Train,  sig : Signal): ABSFuture[Void]= {
    this.parking = Cons(PairOf(zug, sig), this.parking);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setInTracks( inTr : Map[ZugMelde, Pair[Strecke, Strecke]]): ABSFuture[Void]= {
    this.inTracks = inTr;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setSchedule( sch : Map[Train, ZugFolge]): ABSFuture[Void]= {
    this.schedule = sch;
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def initTrain( train : Train,  s : Signal): ABSFuture[Void]= {
    this.parking = Cons(PairOf(train, s), this.parking);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def process( n : Train): ABSFuture[ABSFuture[Void]]= {
    var f_w1n : Train = n;
    if(new Supplier[Boolean] {
  def get(): Boolean= {
    return (!Objects.equals(getSigFor(parking, n), null));
  }
}
.get()) {
      return this.BahnhofImplprocessAwait0(f_w1n);
    }
    else {
      return spawn(Guard.convert(new Supplier[Boolean] {
            def get(): Boolean= {
              return (!Objects.equals(getSigFor(parking, n), null));
            }
          }
          ),()=>this.BahnhofImplprocessAwait0(f_w1n));
    }
  }

  def setPreconditions( s : Signal,  n : Train,  st2 : Strecke,  next : ZugFolge,  nextM : ZugMelde): ABSFuture[List[Switch]]= {
    var msg_3559070213 : Callable[ABSFuture[Void]] = () => this.acqPermit(s, n, st2, next, nextM);
    var acq : ABSFuture[Void] = this.send (msg_3559070213);
    var f_w1acq : ABSFuture[Void] = acq;
    var f_w1n : Train = n;
    var f_w1next : ZugFolge = next;
    var f_w1nextM : ZugMelde = nextM;
    var f_w1s : Signal = s;
    var f_w1st2 : Strecke = st2;
    if(acq.isDone()) {
      return this.BahnhofImplsetPreconditionsAwait0(f_w1acq, f_w1n, f_w1next, f_w1nextM, f_w1s, f_w1st2);
    }
    else {
      return spawn(Guard.convert(acq),()=>this.BahnhofImplsetPreconditionsAwait0(f_w1acq, f_w1n, f_w1next, f_w1nextM, f_w1s, f_w1st2));
    }
  }

  def acqPermit( s : Signal,  n : Train,  st2 : Strecke,  next : ZugFolge,  nextM : ZugMelde): ABSFuture[Void]= {
    while (( ! lookupUnsafe(this.erlaubnis, st2))) {
println((((((now()).toString() + " +++ ") + this.name) + "starts permit loop: ") + (this.expectIn).toString()));
      var f_w1n : Train = n;
      var f_w1next : ZugFolge = next;
      var f_w1nextM : ZugMelde = nextM;
      var f_w1s : Signal = s;
      var f_w1st2 : Strecke = st2;
      if(new Supplier[Boolean] {
  def get(): Boolean= {
    return (Objects.equals(lookupUnsafe(expectIn, st2), Nil[Train]()));
  }
}
.get()) {
        return this.BahnhofImplacqPermitAwait0(f_w1n, f_w1next, f_w1nextM, f_w1s, f_w1st2);
      }
      else {
        return spawn(Guard.convert(new Supplier[Boolean] {
              def get(): Boolean= {
                return (Objects.equals(lookupUnsafe(expectIn, st2), Nil[Train]()));
              }
            }
            ),()=>this.BahnhofImplacqPermitAwait0(f_w1n, f_w1next, f_w1nextM, f_w1s, f_w1st2));
      }
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def go( sch : List[Train]): ABSFuture[Void]= {
    sch match  {
      case Cons(n, xs) => var msg_3559070881 : ABSFuture[ABSFuture[Void]] = this.process(n);
          ;
          var f_w1n : Train.Train = n;
          var f_w1xs : ABS.StdLib.List[Train.Train] = xs;
          var f_w1sch : List[Train] = sch;
          return this.getSpawn(msg_3559070881, (future_ff_par: ABSFuture[Void])=>this.BahnhofImplgoAwait0(f_w1n, f_w1xs, f_w1sch, future_ff_par), Actor.HIGH_PRIORITY, false);
          ;
      case Nil() => ;
      case _ => ;
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def addInSignal( zf : ZugFolge,  st : Strecke,  s : Signal): ABSFuture[Void]= {
    this.inDesc = Cons(TripleOf(zf, st, s), this.inDesc);
    this.inLocked = put(this.inLocked, st, false);
    this.erlaubnis = put(this.erlaubnis, st, false);
    this.allowed = put(this.allowed, st, true);
    this.erlaubnisUnlocked = put(this.erlaubnisUnlocked, st, true);
    this.expectOut = put(this.expectOut, st, Nil());
    this.expectIn = put(this.expectIn, st, Nil());
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def addOutSignal( s : Signal,  st2 : Strecke,  zf2 : ZugFolge): ABSFuture[Void]= {
    this.outDesc = Cons(TripleOf(s, st2, zf2), this.outDesc);
    this.outLocked = put(this.outLocked, st2, false);
    this.erlaubnis = put(this.erlaubnis, st2, false);
    this.allowed = put(this.allowed, st2, true);
    this.erlaubnisUnlocked = put(this.erlaubnisUnlocked, st2, true);
    this.expectOut = put(this.expectOut, st2, Nil());
    this.expectIn = put(this.expectIn, st2, Nil());
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def addSignalMelde( s : Signal,  melde : ZugMelde,  dur : Int): ABSFuture[Void]= {
    this.outMelde = Cons(PairOf(s, melde), this.outMelde);
    this.dura = put(this.dura, PairOf(melde, s), dur);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def addInSignalMelde( s : Signal,  melde : ZugMelde): ABSFuture[Void]= {
    this.inMelde = put(this.inMelde, s, melde);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def getInSignal( s : Strecke): ABSFuture[Signal]= {
    var res : Signal =  extract(this.inDesc, s);
;
    return ABSFuture.done(res);
  }

  def anbieten( train : Train,  st : Strecke,  swFrom : ZugMelde): ABSFuture[Pair[Train, Strecke]]= {
    var zf : ZugFolge =  lookupUnsafe(this.schedule, train);
;
    var parkSig : Signal =  getFree(this.outDesc, zf, getUsed(this.parking));
;
    var inStr : Strecke =  getOther(this.streckeMap, st);
;
    var f_w1inStr : Strecke = inStr;
    var f_w1parkSig : Signal = parkSig;
    var f_w1zf : ZugFolge = zf;
    var f_w1st : Strecke = st;
    var f_w1swFrom : ZugMelde = swFrom;
    var f_w1train : Train = train;
    if(new Supplier[Boolean] {
  def get(): Boolean= {
    return (lookupUnsafe(allowed, inStr) && (!Objects.equals(getFree(outDesc, lookupUnsafe(schedule, train), getUsed(parking)), null)));
  }
}
.get()) {
      return this.BahnhofImplanbietenAwait0(f_w1inStr, f_w1parkSig, f_w1zf, f_w1st, f_w1swFrom, f_w1train);
    }
    else {
      return spawn(Guard.convert(new Supplier[Boolean] {
            def get(): Boolean= {
              return (lookupUnsafe(allowed, inStr) && (!Objects.equals(getFree(outDesc, lookupUnsafe(schedule, train), getUsed(parking)), null)));
            }
          }
          ),()=>this.BahnhofImplanbietenAwait0(f_w1inStr, f_w1parkSig, f_w1zf, f_w1st, f_w1swFrom, f_w1train));
    }
  }

  def anmelden( train : Train,  dur : Double,  swFrom : ZugMelde,  st : Strecke): ABSFuture[Void]= {
    var inSig : Signal =  getSigForRes(this.reserved, train);
;
    var inStr : Strecke =  getOther(this.streckeMap, st);
;
    var sig : Signal =  extract(this.inDesc, inStr);
;
    var msg_3559070906 : Callable[ABSFuture[Void]] = () => this.handle(dur, inSig, sig);
    this.send(msg_3559070906);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def handle( dur : Double,  inSig : Signal,  sig : Signal): ABSFuture[Void]= {
    var msg_355907076 : ABSFuture[Void] = this.advance(dur);
;
    var f_w1dur : Double = dur;
    var f_w1inSig : Signal = inSig;
    var f_w1sig : Signal = sig;
    return this.getSpawn(msg_355907076, (msg_355907076_par: Void)=>this.BahnhofImplhandleAwait0(f_w1dur, f_w1inSig, f_w1sig), Actor.HIGH_PRIORITY, false);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setOutPath( weichen : List[Switch]): ABSFuture[Void]= {
    var i : Int =  0;
;
    while ((i < length(weichen))) {
      var msg_784973385429 : Callable[ABSFuture[Void]] = () => nth(weichen, i).swap();
      var tmp1931723660 : ABSFuture[Void] = nth(weichen, i).send (msg_784973385429);
      var f_w1tmp1931723660 : ABSFuture[Void] = tmp1931723660;
      var f_w1i : Int = i;
      var f_w1weichen : List[Switch] = weichen;
      if(tmp1931723660.isDone()) {
        return this.BahnhofImplsetOutPathAwait0(f_w1tmp1931723660, f_w1i, f_w1weichen);
      }
      else {
        return spawn(Guard.convert(tmp1931723660),()=>this.BahnhofImplsetOutPathAwait0(f_w1tmp1931723660, f_w1i, f_w1weichen));
      }
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def setPath( fromSig : Signal,  toSig : Signal): ABSFuture[Void]= {
    fromSig.printName(this.name);
    toSig.printName(this.name);
    var weichen : List[Switch] =  lookupUnsafe(this.paths, PairOf(fromSig, toSig));
;
    var i : Int =  0;
;
    while ((i < length(weichen))) {
      var msg_78497338561 : Callable[ABSFuture[Void]] = () => nth(weichen, i).swap();
      var f : ABSFuture[Void] = nth(weichen, i).send (msg_78497338561);
      var f_w1f : ABSFuture[Void] = f;
      var f_w1i : Int = i;
      var f_w1weichen : List[Switch] = weichen;
      var f_w1fromSig : Signal = fromSig;
      var f_w1toSig : Signal = toSig;
      if(f.isDone()) {
        return this.BahnhofImplsetPathAwait0(f_w1f, f_w1i, f_w1weichen, f_w1fromSig, f_w1toSig);
      }
      else {
        return spawn(Guard.convert(f),()=>this.BahnhofImplsetPathAwait0(f_w1f, f_w1i, f_w1weichen, f_w1fromSig, f_w1toSig));
      }
    }
    this.inUse = put(this.inUse, toSig, fromSig);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def triggered( s : Signal): ABSFuture[Void]= {
    var msg_115884 : Callable[ABSFuture[Boolean]] = () => s.sperren(now());
    s.send(msg_115884);
    if (isIn(this.inDesc, s)) {
      var msg_3559070389 : ABSFuture[Void] = this.triggeredIn(s);
;
      var f_w1s : Signal = s;
      return this.getSpawn(msg_3559070389, (msg_3559070389_par: Void)=>this.BahnhofImpltriggeredAwait0(f_w1s), Actor.HIGH_PRIORITY, false);
    }
    else {
      this.triggeredOut(s);
    }
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def triggeredIn( s : Signal): ABSFuture[Void]= {
    var last : ZugFolge =  extractLastFolge(this.inDesc, s);
;
    var msg_3314326353 : Callable[ABSFuture[Void]] = () => last.rueckblock(this);
    last.send(msg_3314326353);
    var back : ZugMelde =  lookupUnsafe(this.inMelde, s);
;
    var st2 : Strecke =  extractLastStrecke(this.inDesc, s);
;
    var trains : List[Train] =  lookupUnsafe(this.expectIn, st2);
;
    var train : Train =  lastOf(trains);
;
    trains = without(trains, train);
    this.expectIn = put(this.expectIn, st2, trains);
    var inSig : Signal =  getSigForRes(this.reserved, train);
;
    var msg_3559070599 : ABSFuture[Void] = this.setPath(inSig, s);
;
    var f_w1back : ZugMelde = back;
    var f_w1inSig : Signal = inSig;
    var f_w1last : ZugFolge = last;
    var f_w1st2 : Strecke = st2;
    var f_w1train : Train = train;
    var f_w1trains : List[Train] = trains;
    var f_w1s : Signal = s;
    return this.getSpawn(msg_3559070599, (msg_3559070599_par: Void)=>this.BahnhofImpltriggeredInAwait0(f_w1back, f_w1inSig, f_w1last, f_w1st2, f_w1train, f_w1trains, f_w1s), Actor.HIGH_PRIORITY, false);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def triggeredOut( s : Signal): ABSFuture[Void]= {
println(((this.name + " triggered out ") + (s).toString()));
    var next : ZugFolge =  extractNextFolge(this.outDesc, s);
;
    var st2 : Strecke =  extractNextStrecke(this.outDesc, s);
;
    var msg_3377907697 : Callable[ABSFuture[Void]] = () => next.vorblock(this, st2);
    next.send(msg_3377907697);
    this.outLocked = put(this.outLocked, st2, true);
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def rueckmeldung( swFrom : ZugMelde,  z : Train,  stMelde : Strecke): ABSFuture[Void]= {
    var st : Strecke =  getOther(this.streckeMap, stMelde);
;
    var l : List[Train] =  lookupUnsafe(this.expectOut, st);
;
    this.expectOut = put(this.expectOut, st, without(l, z));
    return ABSFuture.done();
    return ABSFuture.done();
  }

  def reqErlaubnis( sw : ZugMelde,  stMelde : Strecke): ABSFuture[Pair[Boolean, Strecke]]= {
    var st : Strecke =  getOther(this.streckeMap, stMelde);
;
    var ret : Boolean =  false;
;
    if ((lookupUnsafe(this.erlaubnisUnlocked, st) && lookupUnsafe(this.erlaubnis, st))) {
      this.erlaubnis = put(this.erlaubnis, st, false);
      ret = true;
    }
    return ABSFuture.done(PairOf(ret, st));
  }


def BahnhofImpladvanceAwait0( r : Double): ABSFuture[Void]= {
  var w_1$r : Double =  r;
  return ABSFuture.done();
}
def BahnhofImplactSignalAwait0( st : Strecke,  s : Signal): ABSFuture[Void]= {
  var w_1$st : Strecke =  st;
  var w_1$s : Signal =  s;
  var msg_112777464716 : Callable[ABSFuture[Boolean]] = () => w_1$s.freischalten();
  var f : ABSFuture[Boolean] = w_1$s.send (msg_112777464716);
  this.inLocked = put(this.inLocked, w_1$st, false);
  return ABSFuture.done();
}
def BahnhofImplprocessAwait0( n : Train): ABSFuture[ABSFuture[Void]]= {
  var w_1$n : Train =  n;
  var msg_112777459270 : Callable[ABSFuture[Time]] = () => w_1$n.acqStop();
  var f1 : ABSFuture[Time] = w_1$n.send (msg_112777459270);
  var f_w2f1 : ABSFuture[Time] = f1;
  var f_w2w_1$n : Train = w_1$n;
  if(f1.isDone()) {
    return this.BahnhofImplprocessAwait1(f_w2f1, f_w2w_1$n);
  }
  else {
    return spawn(Guard.convert(f1),()=>this.BahnhofImplprocessAwait1(f_w2f1, f_w2w_1$n));
  }
}
def BahnhofImplprocessAwait1( f1 : ABSFuture[Time],  n : Train): ABSFuture[ABSFuture[Void]]= {
  var w_2$f1 : ABSFuture[Time] =  f1;
  var w_2$n : Train =  n;
  var f_w3w_2$f1 : ABSFuture[Time] = w_2$f1;
  var f_w3w_2$n : Train = w_2$n;
  var BahnhofImplprocessAwait2m: CallableGet[ABSFuture[Void], Time] = (last)=>this.BahnhofImplprocessAwait2(f_w3w_2$f1, f_w3w_2$n, last);
  return getSpawn(w_2$f1, BahnhofImplprocessAwait2m, Actor.HIGH_PRIORITY, true);
}
def BahnhofImplprocessAwait2( f1 : ABSFuture[Time],  n : Train,  f1value : Time): ABSFuture[ABSFuture[Void]]= {
  var w_3$f1value : Time =  f1value;
  var w_3$f1 : ABSFuture[Time] =  f1;
  var w_3$n : Train =  n;
  var last : Time = f1value;
  var r : Double =  (timeValue(now()) - timeValue(last));
;
  var f_w4w_3$f1 : ABSFuture[Time] = w_3$f1;
  var f_w4last : Time = last;
  var f_w4r : Double = r;
  var f_w4w_3$n : Train = w_3$n;
  if(Array((this.haltTime - r),(this.haltTime - r))(0)<=0) {
    return this.BahnhofImplprocessAwait3(f_w4w_3$f1, f_w4last, f_w4r, f_w4w_3$n);
  }
  else {
    return spawn(Guard.convert(Array((this.haltTime - r),(this.haltTime - r))),()=>this.BahnhofImplprocessAwait3(f_w4w_3$f1, f_w4last, f_w4r, f_w4w_3$n));
  }
}
def BahnhofImplprocessAwait3( f1 : ABSFuture[Time],  last : Time,  r : Double,  n : Train): ABSFuture[ABSFuture[Void]]= {
  var w_4$r : Double =  r;
  var w_4$last : Time =  last;
  var w_4$f1 : ABSFuture[Time] =  f1;
  var w_4$n : Train =  n;
  var s : Signal =  getSigFor(this.parking, w_4$n);
;
  var st2 : Strecke =  extractNextStrecke(this.outDesc, s);
;
  var next : ZugFolge =  extractNextFolge(this.outDesc, s);
;
  var nextM : ZugMelde =  getFromSignal(this.outMelde, s);
;
  var msg_3559070377 : Callable[ABSFuture[List[Switch]]] = () => this.setPreconditions(s, w_4$n, st2, next, nextM);
  var f : ABSFuture[List[Switch]] = this.send (msg_3559070377);
  var f_w5f : ABSFuture[List[Switch]] = f;
  var f_w5w_4$f1 : ABSFuture[Time] = w_4$f1;
  var f_w5w_4$last : Time = w_4$last;
  var f_w5next : ZugFolge = next;
  var f_w5nextM : ZugMelde = nextM;
  var f_w5w_4$r : Double = w_4$r;
  var f_w5s : Signal = s;
  var f_w5st2 : Strecke = st2;
  var f_w5w_4$n : Train = w_4$n;
  if(f.isDone()) {
    return this.BahnhofImplprocessAwait4(f_w5f, f_w5w_4$f1, f_w5w_4$last, f_w5next, f_w5nextM, f_w5w_4$r, f_w5s, f_w5st2, f_w5w_4$n);
  }
  else {
    return spawn(Guard.convert(f),()=>this.BahnhofImplprocessAwait4(f_w5f, f_w5w_4$f1, f_w5w_4$last, f_w5next, f_w5nextM, f_w5w_4$r, f_w5s, f_w5st2, f_w5w_4$n));
  }
}
def BahnhofImplprocessAwait4( f : ABSFuture[List[Switch]],  f1 : ABSFuture[Time],  last : Time,  next : ZugFolge,  nextM : ZugMelde,  r : Double,  s : Signal,  st2 : Strecke,  n : Train): ABSFuture[ABSFuture[Void]]= {
  var w_5$next : ZugFolge =  next;
  var w_5$st2 : Strecke =  st2;
  var w_5$r : Double =  r;
  var w_5$s : Signal =  s;
  var w_5$last : Time =  last;
  var w_5$f : ABSFuture[List[Switch]] =  f;
  var w_5$nextM : ZugMelde =  nextM;
  var w_5$f1 : ABSFuture[Time] =  f1;
  var w_5$n : Train =  n;
  var f_w6w_5$f : ABSFuture[List[Switch]] = w_5$f;
  var f_w6w_5$f1 : ABSFuture[Time] = w_5$f1;
  var f_w6w_5$last : Time = w_5$last;
  var f_w6w_5$next : ZugFolge = w_5$next;
  var f_w6w_5$nextM : ZugMelde = w_5$nextM;
  var f_w6w_5$r : Double = w_5$r;
  var f_w6w_5$s : Signal = w_5$s;
  var f_w6w_5$st2 : Strecke = w_5$st2;
  var f_w6w_5$n : Train = w_5$n;
  var BahnhofImplprocessAwait5m: CallableGet[ABSFuture[Void], List[Switch]] = (sws)=>this.BahnhofImplprocessAwait5(f_w6w_5$f, f_w6w_5$f1, f_w6w_5$last, f_w6w_5$next, f_w6w_5$nextM, f_w6w_5$r, f_w6w_5$s, f_w6w_5$st2, f_w6w_5$n, sws);
  return getSpawn(w_5$f, BahnhofImplprocessAwait5m, Actor.HIGH_PRIORITY, true);
}
def BahnhofImplprocessAwait5( f : ABSFuture[List[Switch]],  f1 : ABSFuture[Time],  last : Time,  next : ZugFolge,  nextM : ZugMelde,  r : Double,  s : Signal,  st2 : Strecke,  n : Train,  fvalue : List[Switch]): ABSFuture[ABSFuture[Void]]= {
  var w_6$next : ZugFolge =  next;
  var w_6$st2 : Strecke =  st2;
  var w_6$fvalue : List[Switch] =  fvalue;
  var w_6$r : Double =  r;
  var w_6$s : Signal =  s;
  var w_6$last : Time =  last;
  var w_6$f : ABSFuture[List[Switch]] =  f;
  var w_6$nextM : ZugMelde =  nextM;
  var w_6$f1 : ABSFuture[Time] =  f1;
  var w_6$n : Train =  n;
  var sws : List[Switch] = fvalue;
  this.expectOut = put(this.expectOut, w_6$st2, Cons(w_6$n, lookupUnsafe(this.expectOut, w_6$st2)));
  this.erlaubnisUnlocked = put(this.erlaubnisUnlocked, w_6$st2, true);
  var msg_3559070109 : Callable[ABSFuture[Void]] = () => this.setOutPath(sws);
  var f8 : ABSFuture[Void] = this.send (msg_3559070109);
  return ABSFuture.done(f8);
}
def BahnhofImplsetPreconditionsAwait0( acq : ABSFuture[Void],  n : Train,  next : ZugFolge,  nextM : ZugMelde,  s : Signal,  st2 : Strecke): ABSFuture[List[Switch]]= {
  var w_1$next : ZugFolge =  next;
  var w_1$st2 : Strecke =  st2;
  var w_1$s : Signal =  s;
  var w_1$nextM : ZugMelde =  nextM;
  var w_1$acq : ABSFuture[Void] =  acq;
  var w_1$n : Train =  n;
  this.erlaubnisUnlocked = put(this.erlaubnisUnlocked, w_1$st2, false);
  var msg_602086977973 : Callable[ABSFuture[Pair[Train, Strecke]]] = () => w_1$nextM.anbieten(w_1$n, w_1$st2, this);
  var fAnbieten : ABSFuture[Pair[Train, Strecke]] = w_1$nextM.send (msg_602086977973);
  var f_w2w_1$acq : ABSFuture[Void] = w_1$acq;
  var f_w2fAnbieten : ABSFuture[Pair[Train, Strecke]] = fAnbieten;
  var f_w2w_1$n : Train = w_1$n;
  var f_w2w_1$next : ZugFolge = w_1$next;
  var f_w2w_1$nextM : ZugMelde = w_1$nextM;
  var f_w2w_1$s : Signal = w_1$s;
  var f_w2w_1$st2 : Strecke = w_1$st2;
  if(fAnbieten.isDone()) {
    return this.BahnhofImplsetPreconditionsAwait1(f_w2w_1$acq, f_w2fAnbieten, f_w2w_1$n, f_w2w_1$next, f_w2w_1$nextM, f_w2w_1$s, f_w2w_1$st2);
  }
  else {
    return spawn(Guard.convert(fAnbieten),()=>this.BahnhofImplsetPreconditionsAwait1(f_w2w_1$acq, f_w2fAnbieten, f_w2w_1$n, f_w2w_1$next, f_w2w_1$nextM, f_w2w_1$s, f_w2w_1$st2));
  }
}
def BahnhofImplsetPreconditionsAwait1( acq : ABSFuture[Void],  fAnbieten : ABSFuture[Pair[Train, Strecke]],  n : Train,  next : ZugFolge,  nextM : ZugMelde,  s : Signal,  st2 : Strecke): ABSFuture[List[Switch]]= {
  var w_2$next : ZugFolge =  next;
  var w_2$st2 : Strecke =  st2;
  var w_2$fAnbieten : ABSFuture[Pair[Train, Strecke]] =  fAnbieten;
  var w_2$s : Signal =  s;
  var w_2$nextM : ZugMelde =  nextM;
  var w_2$acq : ABSFuture[Void] =  acq;
  var w_2$n : Train =  n;
  var msg_285416704282 : Callable[ABSFuture[Void]] = () => w_2$nextM.anmelden(w_2$n, lookupUnsafe(this.dura, PairOf(w_2$nextM, w_2$s)), this, w_2$st2);
  var f4 : ABSFuture[Void] = w_2$nextM.send (msg_285416704282);
  var sws : List[Switch] =  lookupUnsafe(this.outPaths, PairOf(w_2$s, w_2$nextM));
;
  var msg_3559070144 : Callable[ABSFuture[Void]] = () => this.setOutPath(sws);
  var fOut : ABSFuture[Void] = this.send (msg_3559070144);
  var f_w3w_2$acq : ABSFuture[Void] = w_2$acq;
  var f_w3f4 : ABSFuture[Void] = f4;
  var f_w3w_2$fAnbieten : ABSFuture[Pair[Train, Strecke]] = w_2$fAnbieten;
  var f_w3fOut : ABSFuture[Void] = fOut;
  var f_w3sws : List[Switch] = sws;
  var f_w3w_2$n : Train = w_2$n;
  var f_w3w_2$next : ZugFolge = w_2$next;
  var f_w3w_2$nextM : ZugMelde = w_2$nextM;
  var f_w3w_2$s : Signal = w_2$s;
  var f_w3w_2$st2 : Strecke = w_2$st2;
  if(fOut.isDone()) {
    return this.BahnhofImplsetPreconditionsAwait2(f_w3w_2$acq, f_w3f4, f_w3w_2$fAnbieten, f_w3fOut, f_w3sws, f_w3w_2$n, f_w3w_2$next, f_w3w_2$nextM, f_w3w_2$s, f_w3w_2$st2);
  }
  else {
    return spawn(Guard.convert(fOut),()=>this.BahnhofImplsetPreconditionsAwait2(f_w3w_2$acq, f_w3f4, f_w3w_2$fAnbieten, f_w3fOut, f_w3sws, f_w3w_2$n, f_w3w_2$next, f_w3w_2$nextM, f_w3w_2$s, f_w3w_2$st2));
  }
}
def BahnhofImplsetPreconditionsAwait2( acq : ABSFuture[Void],  f4 : ABSFuture[Void],  fAnbieten : ABSFuture[Pair[Train, Strecke]],  fOut : ABSFuture[Void],  sws : List[Switch],  n : Train,  next : ZugFolge,  nextM : ZugMelde,  s : Signal,  st2 : Strecke): ABSFuture[List[Switch]]= {
  var w_3$next : ZugFolge =  next;
  var w_3$st2 : Strecke =  st2;
  var w_3$fAnbieten : ABSFuture[Pair[Train, Strecke]] =  fAnbieten;
  var w_3$s : Signal =  s;
  var w_3$fOut : ABSFuture[Void] =  fOut;
  var w_3$nextM : ZugMelde =  nextM;
  var w_3$acq : ABSFuture[Void] =  acq;
  var w_3$f4 : ABSFuture[Void] =  f4;
  var w_3$sws : List[Switch] =  sws;
  var w_3$n : Train =  n;
  var f_w4w_3$acq : ABSFuture[Void] = w_3$acq;
  var f_w4w_3$f4 : ABSFuture[Void] = w_3$f4;
  var f_w4w_3$fAnbieten : ABSFuture[Pair[Train, Strecke]] = w_3$fAnbieten;
  var f_w4w_3$fOut : ABSFuture[Void] = w_3$fOut;
  var f_w4w_3$sws : List[Switch] = w_3$sws;
  var f_w4w_3$n : Train = w_3$n;
  var f_w4w_3$next : ZugFolge = w_3$next;
  var f_w4w_3$nextM : ZugMelde = w_3$nextM;
  var f_w4w_3$s : Signal = w_3$s;
  var f_w4w_3$st2 : Strecke = w_3$st2;
  if(new Supplier[Boolean] {
  def get(): Boolean= {
    return ( ! lookupUnsafe(outLocked, w_3$st2));
  }
}
.get()) {
    return this.BahnhofImplsetPreconditionsAwait3(f_w4w_3$acq, f_w4w_3$f4, f_w4w_3$fAnbieten, f_w4w_3$fOut, f_w4w_3$sws, f_w4w_3$n, f_w4w_3$next, f_w4w_3$nextM, f_w4w_3$s, f_w4w_3$st2);
  }
  else {
    return spawn(Guard.convert(new Supplier[Boolean] {
          def get(): Boolean= {
            return ( ! lookupUnsafe(outLocked, w_3$st2));
          }
        }
        ),()=>this.BahnhofImplsetPreconditionsAwait3(f_w4w_3$acq, f_w4w_3$f4, f_w4w_3$fAnbieten, f_w4w_3$fOut, f_w4w_3$sws, f_w4w_3$n, f_w4w_3$next, f_w4w_3$nextM, f_w4w_3$s, f_w4w_3$st2));
  }
}
def BahnhofImplsetPreconditionsAwait3( acq : ABSFuture[Void],  f4 : ABSFuture[Void],  fAnbieten : ABSFuture[Pair[Train, Strecke]],  fOut : ABSFuture[Void],  sws : List[Switch],  n : Train,  next : ZugFolge,  nextM : ZugMelde,  s : Signal,  st2 : Strecke): ABSFuture[List[Switch]]= {
  var w_4$next : ZugFolge =  next;
  var w_4$st2 : Strecke =  st2;
  var w_4$fAnbieten : ABSFuture[Pair[Train, Strecke]] =  fAnbieten;
  var w_4$s : Signal =  s;
  var w_4$fOut : ABSFuture[Void] =  fOut;
  var w_4$nextM : ZugMelde =  nextM;
  var w_4$acq : ABSFuture[Void] =  acq;
  var w_4$f4 : ABSFuture[Void] =  f4;
  var w_4$sws : List[Switch] =  sws;
  var w_4$n : Train =  n;
  var msg_112780347464 : Callable[ABSFuture[Boolean]] = () => w_4$s.freischalten();
  var ff : ABSFuture[Boolean] = w_4$s.send (msg_112780347464);
  this.parking = removeAll(this.parking, w_4$n);
  var msg_112780347861 : Callable[ABSFuture[Void]] = () => w_4$s.acqFree();
  var fFree : ABSFuture[Void] = w_4$s.send (msg_112780347861);
  var f_w5w_4$acq : ABSFuture[Void] = w_4$acq;
  var f_w5w_4$f4 : ABSFuture[Void] = w_4$f4;
  var f_w5w_4$fAnbieten : ABSFuture[Pair[Train, Strecke]] = w_4$fAnbieten;
  var f_w5fFree : ABSFuture[Void] = fFree;
  var f_w5w_4$fOut : ABSFuture[Void] = w_4$fOut;
  var f_w5ff : ABSFuture[Boolean] = ff;
  var f_w5w_4$sws : List[Switch] = w_4$sws;
  var f_w5w_4$n : Train = w_4$n;
  var f_w5w_4$next : ZugFolge = w_4$next;
  var f_w5w_4$nextM : ZugMelde = w_4$nextM;
  var f_w5w_4$s : Signal = w_4$s;
  var f_w5w_4$st2 : Strecke = w_4$st2;
  if(fFree.isDone()) {
    return this.BahnhofImplsetPreconditionsAwait4(f_w5w_4$acq, f_w5w_4$f4, f_w5w_4$fAnbieten, f_w5fFree, f_w5w_4$fOut, f_w5ff, f_w5w_4$sws, f_w5w_4$n, f_w5w_4$next, f_w5w_4$nextM, f_w5w_4$s, f_w5w_4$st2);
  }
  else {
    return spawn(Guard.convert(fFree),()=>this.BahnhofImplsetPreconditionsAwait4(f_w5w_4$acq, f_w5w_4$f4, f_w5w_4$fAnbieten, f_w5fFree, f_w5w_4$fOut, f_w5ff, f_w5w_4$sws, f_w5w_4$n, f_w5w_4$next, f_w5w_4$nextM, f_w5w_4$s, f_w5w_4$st2));
  }
}
def BahnhofImplsetPreconditionsAwait4( acq : ABSFuture[Void],  f4 : ABSFuture[Void],  fAnbieten : ABSFuture[Pair[Train, Strecke]],  fFree : ABSFuture[Void],  fOut : ABSFuture[Void],  ff : ABSFuture[Boolean],  sws : List[Switch],  n : Train,  next : ZugFolge,  nextM : ZugMelde,  s : Signal,  st2 : Strecke): ABSFuture[List[Switch]]= {
  var w_5$ff : ABSFuture[Boolean] =  ff;
  var w_5$next : ZugFolge =  next;
  var w_5$st2 : Strecke =  st2;
  var w_5$fAnbieten : ABSFuture[Pair[Train, Strecke]] =  fAnbieten;
  var w_5$s : Signal =  s;
  var w_5$fOut : ABSFuture[Void] =  fOut;
  var w_5$nextM : ZugMelde =  nextM;
  var w_5$fFree : ABSFuture[Void] =  fFree;
  var w_5$acq : ABSFuture[Void] =  acq;
  var w_5$f4 : ABSFuture[Void] =  f4;
  var w_5$sws : List[Switch] =  sws;
  var w_5$n : Train =  n;
  var msg_112781308349 : Callable[ABSFuture[Void]] = () => w_5$s.acqHalt();
  var fHalt : ABSFuture[Void] = w_5$s.send (msg_112781308349);
  var f_w6w_5$acq : ABSFuture[Void] = w_5$acq;
  var f_w6w_5$f4 : ABSFuture[Void] = w_5$f4;
  var f_w6w_5$fAnbieten : ABSFuture[Pair[Train, Strecke]] = w_5$fAnbieten;
  var f_w6w_5$fFree : ABSFuture[Void] = w_5$fFree;
  var f_w6fHalt : ABSFuture[Void] = fHalt;
  var f_w6w_5$fOut : ABSFuture[Void] = w_5$fOut;
  var f_w6w_5$ff : ABSFuture[Boolean] = w_5$ff;
  var f_w6w_5$sws : List[Switch] = w_5$sws;
  var f_w6w_5$n : Train = w_5$n;
  var f_w6w_5$next : ZugFolge = w_5$next;
  var f_w6w_5$nextM : ZugMelde = w_5$nextM;
  var f_w6w_5$s : Signal = w_5$s;
  var f_w6w_5$st2 : Strecke = w_5$st2;
  if(fHalt.isDone()) {
    return this.BahnhofImplsetPreconditionsAwait5(f_w6w_5$acq, f_w6w_5$f4, f_w6w_5$fAnbieten, f_w6w_5$fFree, f_w6fHalt, f_w6w_5$fOut, f_w6w_5$ff, f_w6w_5$sws, f_w6w_5$n, f_w6w_5$next, f_w6w_5$nextM, f_w6w_5$s, f_w6w_5$st2);
  }
  else {
    return spawn(Guard.convert(fHalt),()=>this.BahnhofImplsetPreconditionsAwait5(f_w6w_5$acq, f_w6w_5$f4, f_w6w_5$fAnbieten, f_w6w_5$fFree, f_w6fHalt, f_w6w_5$fOut, f_w6w_5$ff, f_w6w_5$sws, f_w6w_5$n, f_w6w_5$next, f_w6w_5$nextM, f_w6w_5$s, f_w6w_5$st2));
  }
}
def BahnhofImplsetPreconditionsAwait5( acq : ABSFuture[Void],  f4 : ABSFuture[Void],  fAnbieten : ABSFuture[Pair[Train, Strecke]],  fFree : ABSFuture[Void],  fHalt : ABSFuture[Void],  fOut : ABSFuture[Void],  ff : ABSFuture[Boolean],  sws : List[Switch],  n : Train,  next : ZugFolge,  nextM : ZugMelde,  s : Signal,  st2 : Strecke): ABSFuture[List[Switch]]= {
  var w_6$ff : ABSFuture[Boolean] =  ff;
  var w_6$next : ZugFolge =  next;
  var w_6$fAnbieten : ABSFuture[Pair[Train, Strecke]] =  fAnbieten;
  var w_6$fOut : ABSFuture[Void] =  fOut;
  var w_6$nextM : ZugMelde =  nextM;
  var w_6$fFree : ABSFuture[Void] =  fFree;
  var w_6$acq : ABSFuture[Void] =  acq;
  var w_6$f4 : ABSFuture[Void] =  f4;
  var w_6$sws : List[Switch] =  sws;
  var w_6$n : Train =  n;
  var w_6$st2 : Strecke =  st2;
  var w_6$s : Signal =  s;
  var w_6$fHalt : ABSFuture[Void] =  fHalt;
  return ABSFuture.done(w_6$sws);
}
def BahnhofImplacqPermitAwait0( n : Train,  next : ZugFolge,  nextM : ZugMelde,  s : Signal,  st2 : Strecke): ABSFuture[Void]= {
  var w_1$next : ZugFolge =  next;
  var w_1$st2 : Strecke =  st2;
  var w_1$s : Signal =  s;
  var w_1$nextM : ZugMelde =  nextM;
  var w_1$n : Train =  n;
  this.allowed = put(this.allowed, w_1$st2, false);
println((((((now()).toString() + " +++ ") + this.name) + "calls reqPermit: ") + (this.expectIn).toString()));
  var msg_602086977288 : Callable[ABSFuture[Pair[Boolean, Strecke]]] = () => w_1$nextM.reqErlaubnis(this, w_1$st2);
  var swapF : ABSFuture[Pair[Boolean, Strecke]] = w_1$nextM.send (msg_602086977288);
  var f_w2swapF : ABSFuture[Pair[Boolean, Strecke]] = swapF;
  var f_w2w_1$n : Train = w_1$n;
  var f_w2w_1$next : ZugFolge = w_1$next;
  var f_w2w_1$nextM : ZugMelde = w_1$nextM;
  var f_w2w_1$s : Signal = w_1$s;
  var f_w2w_1$st2 : Strecke = w_1$st2;
  if(swapF.isDone()) {
    return this.BahnhofImplacqPermitAwait1(f_w2swapF, f_w2w_1$n, f_w2w_1$next, f_w2w_1$nextM, f_w2w_1$s, f_w2w_1$st2);
  }
  else {
    return spawn(Guard.convert(swapF),()=>this.BahnhofImplacqPermitAwait1(f_w2swapF, f_w2w_1$n, f_w2w_1$next, f_w2w_1$nextM, f_w2w_1$s, f_w2w_1$st2));
  }
  while (( ! lookupUnsafe(this.erlaubnis, w_1$st2))) {
  }
  return ABSFuture.done();
}
def BahnhofImplacqPermitAwait1( swapF : ABSFuture[Pair[Boolean, Strecke]],  n : Train,  next : ZugFolge,  nextM : ZugMelde,  s : Signal,  st2 : Strecke): ABSFuture[Void]= {
  var w_2$next : ZugFolge =  next;
  var w_2$st2 : Strecke =  st2;
  var w_2$s : Signal =  s;
  var w_2$nextM : ZugMelde =  nextM;
  var w_2$n : Train =  n;
  var w_2$swapF : ABSFuture[Pair[Boolean, Strecke]] =  swapF;
  var f_w3w_2$swapF : ABSFuture[Pair[Boolean, Strecke]] = w_2$swapF;
  var f_w3w_2$n : Train = w_2$n;
  var f_w3w_2$next : ZugFolge = w_2$next;
  var f_w3w_2$nextM : ZugMelde = w_2$nextM;
  var f_w3w_2$s : Signal = w_2$s;
  var f_w3w_2$st2 : Strecke = w_2$st2;
  var BahnhofImplacqPermitAwait2m: CallableGet[Void, Pair[Boolean, Strecke]] = (swapP)=>this.BahnhofImplacqPermitAwait2(f_w3w_2$swapF, f_w3w_2$n, f_w3w_2$next, f_w3w_2$nextM, f_w3w_2$s, f_w3w_2$st2, swapP);
  return getSpawn(w_2$swapF, BahnhofImplacqPermitAwait2m, Actor.HIGH_PRIORITY, true);
  while (( ! lookupUnsafe(this.erlaubnis, w_2$st2))) {
  }
  return ABSFuture.done();
}
def BahnhofImplacqPermitAwait2( swapF : ABSFuture[Pair[Boolean, Strecke]],  n : Train,  next : ZugFolge,  nextM : ZugMelde,  s : Signal,  st2 : Strecke,  swapFvalue : Pair[Boolean, Strecke]): ABSFuture[Void]= {
  var w_3$next : ZugFolge =  next;
  var w_3$st2 : Strecke =  st2;
  var w_3$s : Signal =  s;
  var w_3$nextM : ZugMelde =  nextM;
  var w_3$swapFvalue : Pair[Boolean, Strecke] =  swapFvalue;
  var w_3$n : Train =  n;
  var w_3$swapF : ABSFuture[Pair[Boolean, Strecke]] =  swapF;
  var swapP : Pair[Boolean, Strecke] = swapFvalue;
  var swap : Boolean =  fst(swapP);
;
println((((((now()).toString() + " +++ ") + this.name) + "reads reqPermit: ") + (swap).toString()));
  this.allowed = put(this.allowed, w_3$st2, true);
  if (swap) {
    this.erlaubnis = put(this.erlaubnis, w_3$st2, true);
  }
  else {
    var msg_3559070433 : ABSFuture[Void] = this.advance(1);
;
    var f_w4swap : Boolean = swap;
    var f_w4w_3$swapF : ABSFuture[Pair[Boolean, Strecke]] = w_3$swapF;
    var f_w4swapP : Pair[Boolean, Strecke] = swapP;
    var f_w4w_3$n : Train = w_3$n;
    var f_w4w_3$next : ZugFolge = w_3$next;
    var f_w4w_3$nextM : ZugMelde = w_3$nextM;
    var f_w4w_3$s : Signal = w_3$s;
    var f_w4w_3$st2 : Strecke = w_3$st2;
    return this.getSpawn(msg_3559070433, (msg_3559070433_par: Void)=>this.BahnhofImplacqPermitAwait3(f_w4swap, f_w4w_3$swapF, f_w4swapP, f_w4w_3$n, f_w4w_3$next, f_w4w_3$nextM, f_w4w_3$s, f_w4w_3$st2), Actor.HIGH_PRIORITY, false);
  }
  while (( ! lookupUnsafe(this.erlaubnis, w_3$st2))) {
println((((((now()).toString() + " +++ ") + this.name) + "starts permit loop: ") + (this.expectIn).toString()));
    var f_w1w_3$n : Train = w_3$n;
    var f_w1w_3$next : ZugFolge = w_3$next;
    var f_w1w_3$nextM : ZugMelde = w_3$nextM;
    var f_w1w_3$s : Signal = w_3$s;
    var f_w1w_3$st2 : Strecke = w_3$st2;
    if(new Supplier[Boolean] {
  def get(): Boolean= {
    return (Objects.equals(lookupUnsafe(expectIn, w_3$st2), Nil[Train]()));
  }
}
.get()) {
      return this.BahnhofImplacqPermitAwait0(f_w1w_3$n, f_w1w_3$next, f_w1w_3$nextM, f_w1w_3$s, f_w1w_3$st2);
    }
    else {
      return spawn(Guard.convert(new Supplier[Boolean] {
            def get(): Boolean= {
              return (Objects.equals(lookupUnsafe(expectIn, w_3$st2), Nil[Train]()));
            }
          }
          ),()=>this.BahnhofImplacqPermitAwait0(f_w1w_3$n, f_w1w_3$next, f_w1w_3$nextM, f_w1w_3$s, f_w1w_3$st2));
    }
  }
  return ABSFuture.done();
}
def BahnhofImplacqPermitAwait3( swap : Boolean,  swapF : ABSFuture[Pair[Boolean, Strecke]],  swapP : Pair[Boolean, Strecke],  n : Train,  next : ZugFolge,  nextM : ZugMelde,  s : Signal,  st2 : Strecke): ABSFuture[Void]= {
  var w_4$next : ZugFolge =  next;
  var w_4$st2 : Strecke =  st2;
  var w_4$swapP : Pair[Boolean, Strecke] =  swapP;
  var w_4$s : Signal =  s;
  var w_4$swap : Boolean =  swap;
  var w_4$nextM : ZugMelde =  nextM;
  var w_4$n : Train =  n;
  var w_4$swapF : ABSFuture[Pair[Boolean, Strecke]] =  swapF;
  while (( ! lookupUnsafe(this.erlaubnis, w_4$st2))) {
println((((((now()).toString() + " +++ ") + this.name) + "starts permit loop: ") + (this.expectIn).toString()));
    var f_w1w_4$n : Train = w_4$n;
    var f_w1w_4$next : ZugFolge = w_4$next;
    var f_w1w_4$nextM : ZugMelde = w_4$nextM;
    var f_w1w_4$s : Signal = w_4$s;
    var f_w1w_4$st2 : Strecke = w_4$st2;
    if(new Supplier[Boolean] {
  def get(): Boolean= {
    return (Objects.equals(lookupUnsafe(expectIn, w_4$st2), Nil[Train]()));
  }
}
.get()) {
      return this.BahnhofImplacqPermitAwait0(f_w1w_4$n, f_w1w_4$next, f_w1w_4$nextM, f_w1w_4$s, f_w1w_4$st2);
    }
    else {
      return spawn(Guard.convert(new Supplier[Boolean] {
            def get(): Boolean= {
              return (Objects.equals(lookupUnsafe(expectIn, w_4$st2), Nil[Train]()));
            }
          }
          ),()=>this.BahnhofImplacqPermitAwait0(f_w1w_4$n, f_w1w_4$next, f_w1w_4$nextM, f_w1w_4$s, f_w1w_4$st2));
    }
  }
  return ABSFuture.done();
}
def BahnhofImplgoAwait0( n : Train.Train,  xs : ABS.StdLib.List[Train.Train],  sch : List[Train],  future_ff : ABSFuture[Void]): ABSFuture[Void]= {
  var w_1$future_ff : ABSFuture[Void] =  future_ff;
  var w_1$sch : List[Train] =  sch;
  var w_1$xs : ABS.StdLib.List[Train.Train] =  xs;
  var w_1$n : Train.Train =  n;
  var ff : ABSFuture[Void] = future_ff;
  var f_w2w_1$n : Train.Train = w_1$n;
  var f_w2w_1$xs : ABS.StdLib.List[Train.Train] = w_1$xs;
  var f_w2w_1$sch : List[Train] = w_1$sch;
  if(ff.isDone()) {
    return this.BahnhofImplgoAwait1(f_w2w_1$n, f_w2w_1$xs, f_w2w_1$sch);
  }
  else {
    return spawn(Guard.convert(ff),()=>this.BahnhofImplgoAwait1(f_w2w_1$n, f_w2w_1$xs, f_w2w_1$sch));
  }
  return ABSFuture.done();
}
def BahnhofImplgoAwait1( n : Train.Train,  xs : ABS.StdLib.List[Train.Train],  sch : List[Train]): ABSFuture[Void]= {
  var w_2$sch : List[Train] =  sch;
  var w_2$xs : ABS.StdLib.List[Train.Train] =  xs;
  var w_2$n : Train.Train =  n;
  var msg_3559070839 : ABSFuture[Void] = this.go(w_2$xs);
;
  var f_w3w_2$n : Train.Train = w_2$n;
  var f_w3w_2$xs : ABS.StdLib.List[Train.Train] = w_2$xs;
  var f_w3w_2$sch : List[Train] = w_2$sch;
  return this.getSpawn(msg_3559070839, (msg_3559070839_par: Void)=>this.BahnhofImplgoAwait2(f_w3w_2$n, f_w3w_2$xs, f_w3w_2$sch), Actor.HIGH_PRIORITY, false);
  return ABSFuture.done();
}
def BahnhofImplgoAwait2( n : Train.Train,  xs : ABS.StdLib.List[Train.Train],  sch : List[Train]): ABSFuture[Void]= {
  var w_3$sch : List[Train] =  sch;
  var w_3$xs : ABS.StdLib.List[Train.Train] =  xs;
  var w_3$n : Train.Train =  n;
  return ABSFuture.done();
}
def BahnhofImplanbietenAwait0( inStr : Strecke,  parkSig : Signal,  zf : ZugFolge,  st : Strecke,  swFrom : ZugMelde,  train : Train): ABSFuture[Pair[Train, Strecke]]= {
  var w_1$st : Strecke =  st;
  var w_1$swFrom : ZugMelde =  swFrom;
  var w_1$inStr : Strecke =  inStr;
  var w_1$zf : ZugFolge =  zf;
  var w_1$parkSig : Signal =  parkSig;
  var w_1$train : Train =  train;
  this.reserved = Cons(TripleOf(w_1$train, w_1$parkSig, w_1$swFrom), this.reserved);
  var alreadyExp : List[Train] =  Nil[Train]();
;
  var alreadyExpM : Maybe[List[Train]] =  lookup(this.expectIn, w_1$inStr);
;
  if ((!Objects.equals(alreadyExpM, Nothing[ABS.StdLib.List[Train]]()))) {
    alreadyExp = fromJust(alreadyExpM);
  }
  this.expectIn = put(this.expectIn, w_1$inStr, Cons(w_1$train, alreadyExp));
  return ABSFuture.done(PairOf(w_1$train, w_1$inStr));
}
def BahnhofImplhandleAwait0( dur : Double,  inSig : Signal,  sig : Signal): ABSFuture[Void]= {
  var w_1$dur : Double =  dur;
  var w_1$sig : Signal =  sig;
  var w_1$inSig : Signal =  inSig;
  var msg_3559070558 : ABSFuture[Void] = this.setPath(w_1$inSig, w_1$sig);
;
  var f_w2w_1$dur : Double = w_1$dur;
  var f_w2w_1$inSig : Signal = w_1$inSig;
  var f_w2w_1$sig : Signal = w_1$sig;
  return this.getSpawn(msg_3559070558, (msg_3559070558_par: Void)=>this.BahnhofImplhandleAwait1(f_w2w_1$dur, f_w2w_1$inSig, f_w2w_1$sig), Actor.HIGH_PRIORITY, false);
  return ABSFuture.done();
}
def BahnhofImplhandleAwait1( dur : Double,  inSig : Signal,  sig : Signal): ABSFuture[Void]= {
  var w_2$dur : Double =  dur;
  var w_2$sig : Signal =  sig;
  var w_2$inSig : Signal =  inSig;
  var msg_3559070655 : ABSFuture[Void] = this.actSignal(w_2$sig);
;
  var f_w3w_2$dur : Double = w_2$dur;
  var f_w3w_2$inSig : Signal = w_2$inSig;
  var f_w3w_2$sig : Signal = w_2$sig;
  return this.getSpawn(msg_3559070655, (msg_3559070655_par: Void)=>this.BahnhofImplhandleAwait2(f_w3w_2$dur, f_w3w_2$inSig, f_w3w_2$sig), Actor.HIGH_PRIORITY, false);
  return ABSFuture.done();
}
def BahnhofImplhandleAwait2( dur : Double,  inSig : Signal,  sig : Signal): ABSFuture[Void]= {
  var w_3$dur : Double =  dur;
  var w_3$sig : Signal =  sig;
  var w_3$inSig : Signal =  inSig;
  return ABSFuture.done();
}
def BahnhofImplsetOutPathAwait0( tmp1931723660 : ABSFuture[Void],  i : Int,  weichen : List[Switch]): ABSFuture[Void]= {
  var w_1$tmp1931723660 : ABSFuture[Void] =  tmp1931723660;
  var w_1$i : Int =  i;
  var w_1$weichen : List[Switch] =  weichen;
;
  w_1$i = (w_1$i + 1);
  while ((w_1$i < length(w_1$weichen))) {
    var msg_1690932503875 : Callable[ABSFuture[Void]] = () => nth(w_1$weichen, w_1$i).swap();
    var w_0$tmp1931723660 : ABSFuture[Void] = nth(w_1$weichen, w_1$i).send (msg_1690932503875);
    var f_w1w_0$tmp1931723660 : ABSFuture[Void] = w_0$tmp1931723660;
    var f_w1w_1$i : Int = w_1$i;
    var f_w1w_1$weichen : List[Switch] = w_1$weichen;
    if(w_0$tmp1931723660.isDone()) {
      return this.BahnhofImplsetOutPathAwait0(f_w1w_0$tmp1931723660, f_w1w_1$i, f_w1w_1$weichen);
    }
    else {
      return spawn(Guard.convert(w_0$tmp1931723660),()=>this.BahnhofImplsetOutPathAwait0(f_w1w_0$tmp1931723660, f_w1w_1$i, f_w1w_1$weichen));
    }
  }
  return ABSFuture.done();
}
def BahnhofImplsetPathAwait0( f : ABSFuture[Void],  i : Int,  weichen : List[Switch],  fromSig : Signal,  toSig : Signal): ABSFuture[Void]= {
  var w_1$toSig : Signal =  toSig;
  var w_1$fromSig : Signal =  fromSig;
  var w_1$f : ABSFuture[Void] =  f;
  var w_1$i : Int =  i;
  var w_1$weichen : List[Switch] =  weichen;
  w_1$i = (w_1$i + 1);
  while ((w_1$i < length(w_1$weichen))) {
    var msg_1690932503633 : Callable[ABSFuture[Void]] = () => nth(w_1$weichen, w_1$i).swap();
    var w_0$f : ABSFuture[Void] = nth(w_1$weichen, w_1$i).send (msg_1690932503633);
    var f_w1w_0$f : ABSFuture[Void] = w_0$f;
    var f_w1w_1$i : Int = w_1$i;
    var f_w1w_1$weichen : List[Switch] = w_1$weichen;
    var f_w1w_1$fromSig : Signal = w_1$fromSig;
    var f_w1w_1$toSig : Signal = w_1$toSig;
    if(w_0$f.isDone()) {
      return this.BahnhofImplsetPathAwait0(f_w1w_0$f, f_w1w_1$i, f_w1w_1$weichen, f_w1w_1$fromSig, f_w1w_1$toSig);
    }
    else {
      return spawn(Guard.convert(w_0$f),()=>this.BahnhofImplsetPathAwait0(f_w1w_0$f, f_w1w_1$i, f_w1w_1$weichen, f_w1w_1$fromSig, f_w1w_1$toSig));
    }
  }
  this.inUse = put(this.inUse, w_1$toSig, w_1$fromSig);
  return ABSFuture.done();
}
def BahnhofImpltriggeredAwait0( s : Signal): ABSFuture[Void]= {
  var w_1$s : Signal =  s;
  return ABSFuture.done();
}
def BahnhofImpltriggeredInAwait0( back : ZugMelde,  inSig : Signal,  last : ZugFolge,  st2 : Strecke,  train : Train,  trains : List[Train],  s : Signal): ABSFuture[Void]= {
  var w_1$st2 : Strecke =  st2;
  var w_1$s : Signal =  s;
  var w_1$last : ZugFolge =  last;
  var w_1$inSig : Signal =  inSig;
  var w_1$back : ZugMelde =  back;
  var w_1$trains : List[Train] =  trains;
  var w_1$train : Train =  train;
  this.parking = Cons(PairOf(w_1$train, w_1$inSig), this.parking);
  this.reserved = trainRemove(this.reserved, w_1$train);
  var msg_1088594498568 : Callable[ABSFuture[Void]] = () => w_1$back.rueckmeldung(this, w_1$train, w_1$st2);
  w_1$back.send(msg_1088594498568);
  return ABSFuture.done();
}
{
    moveToCOG(destCOG);
  }

}
