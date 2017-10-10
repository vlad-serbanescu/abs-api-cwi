package awaittest

import abs.api.cwi.{ABSFuture, SugaredActor}

class AwaitTest extends SugaredActor {

  var x = false
  var y = false

  override def init(): ABSFuture[Void] = absVoidMethod {
    this.send(() => this.m1())
    this.send(() => this.m2())
  }

  def m1() = absVoidMethod {
    x = true
    on {!x} execute absVoidContinuation {
      y = true
      for (_ <- 1 to 1000000) {
        if (! y)
          throw new RuntimeException("Actor did not wait properly.")
      }
      println("Finished checking.")
    }
  }

  def m2() = absVoidMethod {
    y = false
    on {x} execute absVoidContinuation {
      x = false
      on {y} execute absVoidContinuation {
        y = false
      }
    }
  }
}

object AwaitTestMain {
  def main(args: Array[String]): Unit = {
    new AwaitTest
  }
}
