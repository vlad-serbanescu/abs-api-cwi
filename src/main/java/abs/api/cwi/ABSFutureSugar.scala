package abs.api.cwi

import java.lang
import java.util.concurrent.Callable
import java.util.function.Supplier


trait TypedActor extends LocalActor {
  trait MessageHandler[V] extends Callable[ABSFuture[V]]

  def messageHandler[V](fn: => ABSFuture[V]) = new MessageHandler[V] {
    override def call() = fn
  }

  def ![V] (message: MessageHandler[V]) = this.send(message)
}


abstract class SugaredActor extends LocalActor {
  implicit val hostActor: LocalActor = this

  class GuardHelper private[SugaredActor] (g: Guard) {
    def execute[T](continuation: Callable[ABSFuture[T]]): ABSFuture[T] = {
      hostActor.spawn(g, continuation)
    }
  }

  def absMethod[T](fn: => T): ABSFuture[T] = ABSFuture.done(fn)

  def absVoidMethod(fn: => Unit): ABSFuture[Void] = {fn; ABSFuture.done}

  def absContinuation[T](fn: => T): Callable[ABSFuture[T]] = () => ABSFuture.done(fn)

  def absVoidContinuation(fn: => Unit): Callable[ABSFuture[Void]] = () => {fn; ABSFuture.done}

  def on(guard: => Boolean) = {
    val supplier = new Supplier[java.lang.Boolean] {
      override def get(): lang.Boolean = guard
    }
    new GuardHelper(Guard.convert(supplier))
  }

  def on(guard: Guard) = new GuardHelper(guard)

}

object ABSFutureSugar {
  type VoidFuture = ABSFuture[Void]

  implicit class ABSFutureImplicit[V] (fut: ABSFuture[V]) {
    def onSuccess[R](continuation: CallableGet[R, V])(implicit hostActor: LocalActor): ABSFuture[R] =
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
