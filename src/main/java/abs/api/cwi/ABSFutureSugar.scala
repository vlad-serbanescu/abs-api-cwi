package abs.api.cwi

import java.util.concurrent.Callable

abstract class SugaredActor extends LocalActor {
  implicit val hostActor: LocalActor = this

  def absMethod[T](fn: => T): ABSFuture[T] = ABSFuture.done(fn)

  def absVoidMethod(fn: => Unit): ABSFuture[Void] = {fn; ABSFuture.done}

}

object ABSFutureSugar {
  type VoidFuture = ABSFuture[Void]

  implicit class ABSFutureImplicit[V] (fut: ABSFuture[V]) {
    def onSuccess[R](continuation: CallableGet[R, V])(implicit hostActor: LocalActor) =
      hostActor.getSpawn(fut, continuation)
  }

  def sequence[R](futures: Iterable[ABSFuture[R]]): ABSFuture[List[R]] = {
    new ABSFuture[List[R]] with Actor {
      private var completed = false
      override def awaiting(actor: Actor): Unit = {
        super.awaiting(actor)
        futures.foreach(_.awaiting(this))
        this.send(null)
      }

      override def isDone: Boolean = completed

      override def getOrNull(): List[R] = {
        if (completed) {
          futures.map(_.getOrNull()).toList
        } else {
          null
        }
      }

      override def send[V](message: Callable[ABSFuture[V]]): ABSFuture[V] = {
        if (!completed) {
          completed = futures.forall(_.isDone)
        }
        if (completed)
          notifyDependant()
        null
      }

      override def forward(target: ABSFuture[List[R]]): Unit = ???
      override def complete(value: List[R]): Unit = ???
      override def spawn[V](guard: Guard, message: Callable[ABSFuture[V]]) = ???
      override def getSpawn[T, V](f: ABSFuture[V], message: CallableGet[T, V], priority: Int, strict: Boolean) = ???
    }
  }
}
