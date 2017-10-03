package exceptionhandling

import abs.api.cwi.{ABSFuture, LocalActor}
import abs.api.cwi.ABSFuture.done

class MyActor extends LocalActor {
  var healthy = true

  def faulty: ABSFuture[Void] = {
    if (this.healthy) {
      this.healthy = false
    } else {
      this.healthy = true  // for next time
      throw new RuntimeException("I am not feeling well...")
    }
    done
  }

  def main(args: Array[String]): Unit = {
    val actor = new MyActor()

  }

}
