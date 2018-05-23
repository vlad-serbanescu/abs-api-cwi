package ABS.StdLib;


import abs.api.cwi.Functions._;
import abs.api.cwi._;
import java.util.Objects;
import java.util.concurrent._;
import java.util.concurrent.atomic._;
import java.util.concurrent.locks._;
import java.util.function._;
import ABS.StdLib.Functions._;
import ABS.StdLib.Exceptions._;
import ABS.StdLib.Exceptions.Functions._;


object Functions {

  def and( a : Boolean,  b : Boolean): Boolean= {
    return (a && b);
  }

  def not( a : Boolean): Boolean= {
    return ( ! a);
  }

  def max[A<%Ordered[ _ >:A]]( a : A,  b : A): A= {
    return if ((a > b))  a else b;
  }

  def min[A<%Ordered[ _ >:A]]( a : A,  b : A): A= {
    return if ((a < b))  a else b;
  }

  def abs( x : Double): Double= {
    return if ((x > 0))  x else ( - x);
  }

  def pow( b : Double,  n : Int): Double= {
    return if ((n < 0))  (1 / pow(b, ( - n))) else n match  {
        case 0 => 1;
        case _ => (b * pow(b, (n - 1)));
        }
        ;
  }

  def sqrt_newton( x : Double,  estimate : Double,  epsilon : Double): Double= {
    return {val next:Double = ((estimate + (x / estimate)) / 2)
          if ((abs((estimate - next)) < epsilon))  estimate else sqrt_newton(x, next, epsilon)};
  }

  def exp_newton_helper( acc : Double,  x : Double,  next_round : Int,  numerator : Double,  denominator : Int,  epsilon : Double): Double= {
    return {val next:Double = ((numerator * x) / (denominator * next_round))
          if ((abs(next) < epsilon))  (acc + next) else exp_newton_helper((acc + next), x, (next_round + 1), (numerator * x), (denominator * next_round), epsilon)};
  }

  def exp_newton( x : Double,  epsilon : Double): Double= {
    return if ((x < 0))  (1 / exp_newton_helper(((1 - x) + ((x * x) / 2)), ( - x), 3, (x * x), 2, epsilon)) else exp_newton_helper(((1 + x) + ((x * x) / 2)), x, 3, (x * x), 2, epsilon);
  }

  def isJust[A<%Ordered[ _ >:A]]( a : Maybe[A]): Boolean= {
    return a match  {
        case Just(j) => true;
        case Nothing() => false;
        }
        ;
  }

  def isLeft[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( vval : Either[A, B]): Boolean= {
    return vval match  {
        case Left(x) => true;
        case _ => false;
        }
        ;
  }

  def isRight[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( vval : Either[A, B]): Boolean= {
    return ( ! isLeft(vval));
  }

  def trd[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B], C<%Ordered[ _ >:C]]( t : Triple[A, B, C]): C= {
    return t match  {
        case TripleOf(_, _, vval) => vval;
        }
        ;
  }

  def set[A<%Ordered[ _ >:A]]( l : List[A]): Set[A]= {
    return l match  {
        case Nil() => EmptySet();
        case Cons(x, xs) => insertElement(set(xs), x);
        }
        ;
  }

  def contains[A<%Ordered[ _ >:A]]( ss : Set[A],  e : A): Boolean= {
    return ss match  {
        case EmptySet() => false;
        case Insert(`e`, _) => true;
        case Insert(x, xs) => if ((x > e))  false else contains(xs, e);
        }
        ;
  }

  def emptySet[A<%Ordered[ _ >:A]]( xs : Set[A]): Boolean= {
    return (Objects.equals(xs, EmptySet[A]()));
  }

  def size[A<%Ordered[ _ >:A]]( xs : Set[A]): Int= {
    return xs match  {
        case EmptySet() => 0;
        case Insert(s, ss) => (1 + size(ss));
        }
        ;
  }

  def elements[A<%Ordered[ _ >:A]]( xs : Set[A]): List[A]= {
    return xs match  {
        case EmptySet() => Nil();
        case Insert(s, ss) => Cons(s, elements(ss));
        }
        ;
  }

  def union[A<%Ordered[ _ >:A]]( set1 : Set[A],  set2 : Set[A]): Set[A]= {
    return set1 match  {
        case EmptySet() => set2;
        case Insert(e1, ss1) => set2 match  {
            case EmptySet() => set1;
            case Insert(`e1`, ss2) => Insert(e1, union(ss1, ss2));
            case Insert(e2, ss2) => if ((e1 < e2))  Insert(e1, union(ss1, set2)) else Insert(e2, union(set1, ss2));
            }
            ;
        }
        ;
  }

  def intersection[A<%Ordered[ _ >:A]]( set1 : Set[A],  set2 : Set[A]): Set[A]= {
    return set1 match  {
        case EmptySet() => EmptySet();
        case Insert(e1, ss1) => set2 match  {
            case EmptySet() => EmptySet();
            case Insert(`e1`, ss2) => Insert(e1, intersection(ss1, ss2));
            case Insert(e2, ss2) => if ((e1 < e2))  intersection(ss1, set2) else intersection(set1, ss2);
            }
            ;
        }
        ;
  }

  def difference[A<%Ordered[ _ >:A]]( set1 : Set[A],  set2 : Set[A]): Set[A]= {
    return set1 match  {
        case EmptySet() => EmptySet();
        case Insert(e1, ss1) => set2 match  {
            case EmptySet() => set1;
            case Insert(`e1`, ss2) => difference(ss1, ss2);
            case Insert(e2, ss2) => if ((e1 < e2))  Insert(e1, difference(ss1, set2)) else difference(set1, ss2);
            }
            ;
        }
        ;
  }

  def isSubset[A<%Ordered[ _ >:A]]( maybe_subset : Set[A],  set : Set[A]): Boolean= {
    return maybe_subset match  {
        case EmptySet() => true;
        case Insert(elem, rest) => (contains(set, elem) && isSubset(rest, set));
        }
        ;
  }

  def insertElement[A<%Ordered[ _ >:A]]( xs : Set[A],  e : A): Set[A]= {
    return xs match  {
        case EmptySet() => Insert(e, EmptySet());
        case Insert(`e`, _) => xs;
        case Insert(x, ss) => if ((e < x))  Insert(e, xs) else Insert(x, insertElement(ss, e));
        }
        ;
  }

  def remove[A<%Ordered[ _ >:A]]( xs : Set[A],  e : A): Set[A]= {
    return xs match  {
        case EmptySet() => EmptySet();
        case Insert(`e`, ss) => ss;
        case Insert(x, ss) => if ((e < x))  xs else Insert(x, remove(ss, e));
        }
        ;
  }

  def take[A<%Ordered[ _ >:A]]( ss : Set[A]): A= {
    return ss match  {
        case Insert(e, _) => e;
        }
        ;
  }

  def takeMaybe[A<%Ordered[ _ >:A]]( ss : Set[A]): Maybe[A]= {
    return ss match  {
        case EmptySet() => Nothing();
        case Insert(e, _) => Just(e);
        }
        ;
  }

  def hasNext[A<%Ordered[ _ >:A]]( s : Set[A]): Boolean= {
    return ( ! emptySet(s));
  }

  def next[A<%Ordered[ _ >:A]]( s : Set[A]): Pair[Set[A], A]= {
    return s match  {
        case Insert(e, set2) => PairOf(set2, e);
        }
        ;
  }

  def list[A<%Ordered[ _ >:A]]( l : List[A]): List[A]= {
    return l;
  }

  def length[A<%Ordered[ _ >:A]]( list : List[A]): Int= {
    return list match  {
        case Nil() => 0;
        case Cons(p, l) => (1 + length(l));
        }
        ;
  }

  def isEmpty[A<%Ordered[ _ >:A]]( list : List[A]): Boolean= {
    return (Objects.equals(list, Nil[A]()));
  }

  def nth[A<%Ordered[ _ >:A]]( list : List[A],  n : Int): A= {
    return n match  {
        case 0 => head(list);
        case _ => nth(tail(list), (n - 1));
        }
        ;
  }

  def without[A<%Ordered[ _ >:A]]( list : List[A],  a : A): List[A]= {
    return list match  {
        case Nil() => Nil();
        case Cons(`a`, tail) => without(tail, a);
        case Cons(x, tail) => Cons(x, without(tail, a));
        }
        ;
  }

  def concatenate[A<%Ordered[ _ >:A]]( list1 : List[A],  list2 : List[A]): List[A]= {
    return list1 match  {
        case Nil() => list2;
        case Cons(head, tail) => Cons(head, concatenate(tail, list2));
        }
        ;
  }

  def appendright[A<%Ordered[ _ >:A]]( list : List[A],  p : A): List[A]= {
    return concatenate(list, Cons(p, Nil()));
  }

  def reverse[A<%Ordered[ _ >:A]]( list : List[A]): List[A]= {
    return list match  {
        case Cons(hd, tl) => appendright(reverse(tl), hd);
        case Nil() => Nil();
        }
        ;
  }

  def copy[A<%Ordered[ _ >:A]]( p : A,  n : Int): List[A]= {
    return n match  {
        case 0 => Nil();
        case m => Cons(p, copy(p, (m - 1)));
        }
        ;
  }

  def map[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( l : List[Pair[A, B]]): Map[A, B]= {
    return l match  {
        case Nil() => EmptyMap();
        case Cons(hd, tl) => InsertAssoc(hd, map(tl));
        }
        ;
  }

  def emptyMap[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( map : Map[A, B]): Boolean= {
    return (Objects.equals(map, EmptyMap[A, B]()));
  }

  def removeKey[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( map : Map[A, B],  key : A): Map[A, B]= {
    return map match  {
        case EmptyMap() => map;
        case InsertAssoc(PairOf(`key`, _), m) => m;
        case InsertAssoc(pair, tail) => InsertAssoc(pair, removeKey(tail, key));
        }
        ;
  }

  def values[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( map : Map[A, B]): List[B]= {
    return map match  {
        case EmptyMap() => Nil();
        case InsertAssoc(PairOf(_, elem), tail) => Cons(elem, values(tail));
        }
        ;
  }

  def keys[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( map : Map[A, B]): Set[A]= {
    return map match  {
        case EmptyMap() => EmptySet();
        case InsertAssoc(PairOf(a, _), tail) => insertElement(keys(tail), a);
        }
        ;
  }

  def lookup[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( ms : Map[A, B],  k : A): Maybe[B]= {
    return ms match  {
        case InsertAssoc(PairOf(`k`, y), _) => Just(y);
        case InsertAssoc(_, tm) => lookup(tm, k);
        case EmptyMap() => Nothing();
        }
        ;
  }

  def lookupMaybe[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( ms : Map[A, B],  k : A): Maybe[B]= {
    return lookup(ms, k);
  }

  def lookupUnsafe[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( ms : Map[A, B],  k : A): B= {
    return fromJust(lookup(ms, k));
  }

  def lookupDefault[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( ms : Map[A, B],  k : A,  d : B): B= {
    return ms match  {
        case InsertAssoc(PairOf(`k`, y), _) => y;
        case InsertAssoc(_, tm) => lookupDefault(tm, k, d);
        case EmptyMap() => d;
        }
        ;
  }

  def insert[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( map : Map[A, B],  p : Pair[A, B]): Map[A, B]= {
    return InsertAssoc(p, map);
  }

  def put[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( ms : Map[A, B],  k : A,  v : B): Map[A, B]= {
    return ms match  {
        case EmptyMap() => InsertAssoc(PairOf(k, v), EmptyMap());
        case InsertAssoc(PairOf(`k`, _), ts) => InsertAssoc(PairOf(k, v), ts);
        case InsertAssoc(p, ts) => InsertAssoc(p, put(ts, k, v));
        }
        ;
  }

  def intToString( n : Int): String= {
    return (n).toString();
  }

  def now(): Time= {
    return TimeOf(currentms());
  }

  def timeDifference( t1 : Time,  t2 : Time): Double= {
    return abs((timeValue(t2) - timeValue(t1)));
  }

  def timeLessThan( t1 : Time,  t2 : Time): Boolean= {
    return (timeValue(t1) < timeValue(t2));
  }

  def isDurationInfinite( d : Duration): Boolean= {
    return d match  {
        case DurationOf(_) => false;
        case InfDuration() => true;
        }
        ;
  }

  def addDuration( t : Time,  d : Duration): Time= {
    return TimeOf((timeValue(t) + durationValue(d)));
  }

  def subtractDuration( t : Time,  d : Duration): Time= {
    return TimeOf((timeValue(t) - durationValue(d)));
  }

  def durationLessThan( d1 : Duration,  d2 : Duration): Boolean= {
    return d1 match  {
        case DurationOf(v1) => d2 match  {
            case DurationOf(v2) => (v1 < v2);
            case InfDuration() => false;
            }
            ;
        case InfDuration() => false;
        }
        ;
  }

  def deadline(): Duration= {
    return (lowlevelDeadline() < 0) match  {
        case true => InfDuration();
        case false => DurationOf(lowlevelDeadline());
        }
        ;
  }

  def subtractFromDuration( d : Duration,  v : Double): Duration= {
    return d match  {
        case InfDuration() => InfDuration();
        case DurationOf(x) => DurationOf((x - v));
        }
        ;
  }

  def fromJust[A<%Ordered[ _ >:A]]( data : Maybe[A]): A= {
    return data match  {
        case Just(res) => res;
        }
        ;
  }

  def left[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( data : Either[A, B]): A= {
    return data match  {
        case Left(res) => res;
        }
        ;
  }

  def right[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( data : Either[A, B]): B= {
    return data match  {
        case Right(res) => res;
        }
        ;
  }

  def fst[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( data : Pair[A, B]): A= {
    return data match  {
        case PairOf(res, _) => res;
        }
        ;
  }

  def snd[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B]]( data : Pair[A, B]): B= {
    return data match  {
        case PairOf(_, res) => res;
        }
        ;
  }

  def trdT[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B], C<%Ordered[ _ >:C]]( data : Triple[A, B, C]): C= {
    return data match  {
        case TripleOf(_, _, res) => res;
        }
        ;
  }

  def fstT[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B], C<%Ordered[ _ >:C]]( data : Triple[A, B, C]): A= {
    return data match  {
        case TripleOf(res, _, _) => res;
        }
        ;
  }

  def sndT[A<%Ordered[ _ >:A], B<%Ordered[ _ >:B], C<%Ordered[ _ >:C]]( data : Triple[A, B, C]): B= {
    return data match  {
        case TripleOf(_, res, _) => res;
        }
        ;
  }

  def head[A<%Ordered[ _ >:A]]( data : List[A]): A= {
    return data match  {
        case Cons(res, _) => res;
        }
        ;
  }

  def tail[A<%Ordered[ _ >:A]]( data : List[A]): List[A]= {
    return data match  {
        case Cons(_, res) => res;
        }
        ;
  }

  def timeValue( data : Time): Double= {
    return data match  {
        case TimeOf(res) => res;
        }
        ;
  }

  def durationValue( data : Duration): Double= {
    return data match  {
        case DurationOf(res) => res;
        }
        ;
  }

}
