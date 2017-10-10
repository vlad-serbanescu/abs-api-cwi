package inittest

import abs.api.cwi.{ABSFuture, SugaredActor}

class InitTest extends SugaredActor {
  var x = false

  override def init() = absVoidMethod {
    x = true
  }

  for (_ <- 1 to 1000000) {
    Thread.sleep(1)
    if (x)
      throw new RuntimeException("Init method is run before construction completed.")
  }
  println("Finished construction.")
}

object InitTestMain {
  def main(args: Array[String]): Unit = {
    new InitTest

  }
}
