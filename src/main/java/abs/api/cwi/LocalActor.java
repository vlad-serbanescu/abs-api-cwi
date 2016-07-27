package abs.api.cwi;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocalActor implements Comparable<Actor>, Actor {

	public ABSFutureTask<?> runningMessage;
	private AtomicBoolean executorIsRunning = new AtomicBoolean(false);
	protected ConcurrentLinkedQueue<ABSFutureTask<?>> messageQueue = new ConcurrentLinkedQueue<ABSFutureTask<?>>();

	class MainTask implements Runnable {
		@Override
		public void run() {
			
			System.out.println(LocalActor.this+" from main task:" + messageQueue);
			if (! takeOrDie()) return;	// no enabled message or empty queue
			try {
				runningMessage.run();
				runningMessage.finished();
				DeploymentComponent.releaseAll(runningMessage);
			} catch (AwaitException ae) {
				System.out.println("await with unresolved guard encountered");
			}
			// in case there are more actors than threads, give other actors a chance to run
			DeploymentComponent.submit(this);
		}
	}

	private boolean takeOrDie() {
		synchronized (executorIsRunning) {
			for (ABSFutureTask<?> absFutureTask : messageQueue) {
				if (absFutureTask.evaluateGuard()) {
					runningMessage = absFutureTask;
					messageQueue.remove(absFutureTask);
					return true;
				}
			}
			executorIsRunning.set(false);
			return false;
		}
	}

	private boolean notRunningThenStart() {
		synchronized (executorIsRunning) {
			return executorIsRunning.compareAndSet(false, true);
		}
	}

	@Override
	public final <V> ABSFutureTask<V> send(Runnable message) {
		ABSFutureTask<V> m = new ABSFutureTask<V>(message);
		send(m);
		return m;
	}

	@Override
	public final <V> ABSFutureTask<V> send(Callable<V> message) {
		ABSFutureTask<V> m = new ABSFutureTask<V>(message);
		send(m);
		return m;

	}

	private <V> void send(ABSFutureTask<V> messageArgument) {
		messageQueue.add(messageArgument);
		System.out.println(this+ " queue: "+ messageQueue);
		if (notRunningThenStart()) {
			DeploymentComponent.submit(new MainTask());
		}
	}

	@Override
	public final void await(Guard guard, Callable message) {
		await(runningMessage.continueWith(new FutureTask<>(message), guard));
	}

	@Override
	public final void await(Guard guard, Runnable message) {
		await(runningMessage.continueWith(new FutureTask<>(message, null), guard));
	}

	private <T> void await(ABSFutureTask<T> m) {
		if (!m.evaluateGuard()) {
			m.enablingCondition.addFuture(this);
			messageQueue.add(m);
			System.out.println(this+ " queue: "+ messageQueue);
			throw new AwaitException();
		}
	}

	private class AwaitException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	public int compareTo(Actor o) {
		// TODO check 
		return this.hashCode() - o.hashCode();
	}
}
