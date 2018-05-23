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


object Functions {

  def lastOf[T<%Ordered[ _ >:T]]( list : List[T]): T= {
    return list match  {
        case Cons(x, xs) => if ((Objects.equals(xs, Nil[T]())))  x else lastOf(xs);
        }
        ;
  }

  def extract( list : List[Triple[ZugFolge, Strecke, Signal]],  str : Strecke): Signal= {
    return list match  {
        case Nil() => null;
        case Cons(TripleOf(zf, st, sg), xs) => if ((Objects.equals(str, st)))  sg else extract(xs, str);
        }
        ;
  }

  def removeAll( list : List[Pair[Train, Signal]],  z : Train): List[Pair[Train, Signal]]= {
    return list match  {
        case Nil() => Nil();
        case Cons(PairOf(a, b), xs) => if ((Objects.equals(z, a)))  removeAll(xs, z) else Cons(PairOf(a, b), removeAll(xs, z));
        }
        ;
  }

  def getOther( list : List[Pair[Strecke, Strecke]],  st : Strecke): Strecke= {
    return list match  {
        case Nil() => null;
        case Cons(PairOf(a, b), xs) => if ((Objects.equals(a, st)))  b else if ((Objects.equals(b, st)))  a else getOther(xs, st);
        }
        ;
  }

  def trainRemove( list : List[Triple[Train, Signal, ZugMelde]],  z : Train): List[Triple[Train, Signal, ZugMelde]]= {
    return list match  {
        case Nil() => Nil();
        case Cons(TripleOf(zg, s, sw), xs) => if ((Objects.equals(z, zg)))  trainRemove(xs, z) else Cons(TripleOf(zg, s, sw), trainRemove(xs, z));
        }
        ;
  }

  def getMeldeFrom( list : List[Triple[Train, Signal, ZugMelde]],  z : Train): ZugMelde= {
    return list match  {
        case Nil() => null;
        case Cons(TripleOf(zg, s, sw), xs) => if ((Objects.equals(z, zg)))  sw else getMeldeFrom(xs, z);
        }
        ;
  }

  def getFromMelde( list : List[Pair[Signal, ZugMelde]],  m : ZugMelde): Signal= {
    return list match  {
        case Nil() => null;
        case Cons(PairOf(s, m2), xs) => if ((Objects.equals(m, m2)))  s else getFromMelde(xs, m);
        }
        ;
  }

  def getFromSignal( list : List[Pair[Signal, ZugMelde]],  s : Signal): ZugMelde= {
    return list match  {
        case Nil() => null;
        case Cons(PairOf(s2, m), xs) => if ((Objects.equals(s, s2)))  m else getFromSignal(xs, s);
        }
        ;
  }

  def has[T<%Ordered[ _ >:T]]( list : List[T],  elem : T): Boolean= {
    return list match  {
        case Nil() => false;
        case Cons(x, xs) => if ((Objects.equals(elem, x)))  true else has(xs, elem);
        }
        ;
  }

  def isIn( list : List[Triple[ZugFolge, Strecke, Signal]],  sig : Signal): Boolean= {
    return list match  {
        case Nil() => false;
        case Cons(TripleOf(zf, st, sg), xs) => if ((Objects.equals(sig, sg)))  true else isIn(xs, sig);
        }
        ;
  }

  def extractSignal( list : List[Triple[ZugFolge, Strecke, Signal]],  zfi : ZugFolge,  s : Strecke): Signal= {
    return list match  {
        case Nil() => null;
        case Cons(TripleOf(zf, st, sg), xs) => if (((Objects.equals(zf, zfi)) && (Objects.equals(st, s))))  sg else extractSignal(xs, zfi, s);
        }
        ;
  }

  def extractNextStrecke( list : List[Triple[Signal, Strecke, ZugFolge]],  s : Signal): Strecke= {
    return list match  {
        case Nil() => null;
        case Cons(TripleOf(sg, st2, zf2), xs) => if ((Objects.equals(s, sg)))  st2 else extractNextStrecke(xs, s);
        }
        ;
  }

  def extractLastStrecke( list : List[Triple[ZugFolge, Strecke, Signal]],  s : Signal): Strecke= {
    return list match  {
        case Nil() => null;
        case Cons(TripleOf(zf, st, sg), xs) => if ((Objects.equals(s, sg)))  st else extractLastStrecke(xs, s);
        }
        ;
  }

  def extractLastFolge( list : List[Triple[ZugFolge, Strecke, Signal]],  s : Signal): ZugFolge= {
    return list match  {
        case Nil() => null;
        case Cons(TripleOf(zf, st, sg), xs) => if ((Objects.equals(s, sg)))  zf else extractLastFolge(xs, s);
        }
        ;
  }

  def extractNextFolge( list : List[Triple[Signal, Strecke, ZugFolge]],  s : Signal): ZugFolge= {
    return list match  {
        case Nil() => null;
        case Cons(TripleOf(sg, st2, zf2), xs) => if ((Objects.equals(s, sg)))  zf2 else extractNextFolge(xs, s);
        }
        ;
  }

  def extractOutStrecke( list : List[Triple[Signal, Strecke, ZugFolge]],  zf : ZugFolge): Strecke= {
    return list match  {
        case Nil() => null;
        case Cons(TripleOf(sg, st2, zf2), xs) => if ((Objects.equals(zf, zf2)))  st2 else extractOutStrecke(xs, zf);
        }
        ;
  }

  def getFree( list : List[Triple[Signal, Strecke, ZugFolge]],  ziel : ZugFolge,  used : List[Signal]): Signal= {
    return list match  {
        case Nil() => null;
        case Cons(TripleOf(sg, st2, zf2), xs) => if (((Objects.equals(zf2, ziel)) && ( ! has(used, sg))))  sg else getFree(xs, ziel, used);
        }
        ;
  }

  def getUsed( list : List[Pair[Train, Signal]]): List[Signal]= {
    return list match  {
        case Nil() => Nil();
        case Cons(PairOf(a, b), xs) => Cons(b, getUsed(xs));
        }
        ;
  }

  def getSigFor( list : List[Pair[Train, Signal]],  train : Train): Signal= {
    return list match  {
        case Nil() => null;
        case Cons(PairOf(a, b), xs) => if ((Objects.equals(a, train)))  b else getSigFor(xs, train);
        }
        ;
  }

  def getSigForRes( list : List[Triple[Train, Signal, ZugMelde]],  train : Train): Signal= {
    return list match  {
        case Nil() => null;
        case Cons(TripleOf(a, b, c), xs) => if ((Objects.equals(a, train)))  b else getSigForRes(xs, train);
        }
        ;
  }

  def getTrainOnMelde( list : List[Triple[Train, Signal, ZugMelde]],  m : ZugMelde): Train= {
    return list match  {
        case Nil() => null;
        case Cons(TripleOf(a, b, c), xs) => if ((Objects.equals(c, m)))  a else getTrainOnMelde(xs, m);
        }
        ;
  }

}
