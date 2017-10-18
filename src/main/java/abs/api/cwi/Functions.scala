package abs.api.cwi

import java.time.DateTimeException

import scala.util.Random
import scala.io.StdIn._;

object Functions {

  class Rational {
     var num = 0
     var denom = 0

    def this(d: Double) {
      this()
      val s = String.valueOf(d)
      val digitsDec = s.length - 1 - s.indexOf('.')

      var number = d;
      this.denom = 1
      var i = 0
      while (i < digitsDec) {
        number *= 10
        this.denom *= 10
        i +=1
      }
      this.num = Math.round(d).asInstanceOf[Int]
    }

    def this(num: Int, denom: Int) {
      this()
      this.num = num
      this.denom = denom
    }

    override def toString: String = String.valueOf(num) + "/" + String.valueOf(denom)
  }

  val r = new Random(System.currentTimeMillis());

  def random(below: Int): Int = {
    r.nextInt(below);
  }

  def truncate(a: Double): Int = {
    return math.floor(a).toInt;
  }

  def numerator(a: Double): Int = {
    new Rational().num;
  }

  def denominator(a: Double): Int = {
    new Rational().denom;
  }

  def substr( str : String,  start : Int,  length : Int): String= {
    str.substring(start, start+length);
  }

  def strlen( str : String): Int= {
    str.length
  }

  def readln(): String={
    readLine();
  }

  def currentms(): Double= {
    TimedActorSystem.now();
  }

  def lowlevelDeadline(): Double = {
    return 0;
  }

}
