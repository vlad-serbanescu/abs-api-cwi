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


object Functions {

  def listContains[T<%Ordered[ _ >:T]]( list : List[T],  elem : T): Boolean= {
    return list match  {
        case Cons(x, xs) => if ((Objects.equals(elem, x)))  true else listContains(xs, elem);
        case Nil() => false;
        }
        ;
  }

  def approxStart( r : Double): Int= {
    return approxStartHelper(r, 1);
  }

  def approxStartHelper( r : Double,  i : Int): Int= {
    return if (((i * i) > r))  i else approxStartHelper(r, (i + 2));
  }

  def in_short( r : Double): Double= {
    return if ((denominator(r) < 1000))  r else (truncate(((numerator(r) / denominator(r)) * 1000)) / 1000);
  }

  def in_sqrt( t : Double): Double= {
    return sqrt_newton(t, 20, (1 / 100));
  }

}
