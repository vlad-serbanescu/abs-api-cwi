package eratosthenes

import abs.api.cwi.ABSFuture.done
import abs.api.cwi.{ActorSystem, SugaredActor, TypedActor}
import abs.api.cwi.ABSFutureSugar._


class Sieve(prime: Int) extends SugaredActor with TypedActor {
  var next: Option[Sieve] = None

  def divide(toDivide: Int): MessageHandler[(Int, Boolean)] = messageHandler {
    if (toDivide % prime == 0) {
      done(toDivide -> false)
    } else if (next isEmpty) {
      next = Some(new Sieve(toDivide))
      done(toDivide -> true)
    } else {
      val nextPrime = next.get
      nextPrime ! nextPrime.divide(toDivide)
    }
  }
}


object SieveMain extends SugaredActor with TypedActor {
  def main(args: Array[String]): Unit = {
    val two = new Sieve(2)
    import two.divide
    val futures = for (i <- 3 to 10000) yield {two ! divide(i)}
    sequence(futures) onSuccess { results: List[(Int, Boolean)] =>
      val primes = 2 +: results.filter(_._2).map(_._1)
      println(s"found ${primes.size} primes: " + primes.mkString("[", ", ", "]"))
      ActorSystem.shutdown()
      done
    }
  }
}