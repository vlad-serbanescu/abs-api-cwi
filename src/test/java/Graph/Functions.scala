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


object Functions {

  def getOutEdge( list : List[Edge],  fromEdge : Edge): Edge= {
    return list match  {
        case Cons(x, xs) => if ((Objects.equals(x, fromEdge)))  getOutEdge(xs, fromEdge) else x;
        case Nil() => null;
        }
        ;
  }

  def remOutEdge( list : List[Edge],  fromEdge : Edge): List[Edge]= {
    return list match  {
        case Cons(x, xs) => if ((Objects.equals(x, fromEdge)))  remOutEdge(xs, fromEdge) else Cons(x, remOutEdge(xs, fromEdge));
        case Nil() => Nil();
        }
        ;
  }

}
