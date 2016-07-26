package abs.api.cwi;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocalActor extends AbstractActor {

	class MainTask implements Runnable {

		@Override
		public void run() {
			System.out.println("from main task:" + messageQueue);
			ABSFutureTask<?> m = takeOrDie();
			if (m == null)
				return;
			completedMessage = true;
			try {
				m.runningF.run();
			} catch (AwaitException ae) {
				possibleAwait.setF(m.f);
			}
			if (completedMessage) {
				m.finished();
				releaseAll(m);
			}
			// in case there are more actors than threads, give other actors a chance to run
			mainExecutor.submit(this);
		}
	}

	// Semaphore s;
	private AtomicBoolean executorIsRunning = new AtomicBoolean(false);
	protected ConcurrentLinkedQueue<ABSFutureTask<?>> messageQueue;
	public ConcurrentHashMap<ABSFutureTask<?>, LinkedList<ABSFutureTask<?>>> futureContinuations;
	// public ConcurrentHashMap<Supplier<Boolean>, Set<ABSFutureTask<?>>>
	// conditionContinuations;

	protected ABSFutureTask<?> possibleAwait = null;
	protected boolean completedMessage = true;

	public LocalActor() {
		super();
		messageQueue = new ConcurrentLinkedQueue<ABSFutureTask<?>>();
		if (localActorMap == null) {
			localActorMap = new ConcurrentSkipListSet<LocalActor>();
		}

		localActorMap.add(this);

	}

	private ABSFutureTask<?> takeOrDie() {
		synchronized (executorIsRunning) {
			ABSFutureTask<?> m = null;
			for (ABSFutureTask<?> absFutureTask : messageQueue) {
				if ((absFutureTask.getEnablingCondition() == null)
						|| (absFutureTask.getEnablingCondition().evaluate())) {
					m = absFutureTask;
					messageQueue.remove(absFutureTask);
					return m;
				}
			}
			executorIsRunning.set(false);
			return null;
		}
	}

	private boolean notEmptyAndStart() {
		synchronized (executorIsRunning) {
			return executorIsRunning.compareAndSet(false, true);
		}
	}

	@Override
	public <V> ABSFutureTask<V> send(Runnable message) {

		ABSFutureTask<V> m = new ABSFutureTask<V>(message, IS_REACHABLE, hostName);
		send(m);
		return m;
	}

	@Override
	public <V> ABSFutureTask<V> send(Callable<V> message) {
		ABSFutureTask<V> m = new ABSFutureTask<V>(message, IS_REACHABLE, hostName);
		send(m);
		return m;

	}

	protected <V> void send(ABSFutureTask<V> messageArgument) {
		messageQueue.add(messageArgument);

		if (notEmptyAndStart()) {
			mainExecutor.submit(new MainTask());
		}
	}

	public void enable(ABSFutureTask<?> completedFuture) {
		LinkedList<ABSFutureTask<?>> releasedTasks = futureContinuations.remove(completedFuture);

	}

	private <T> void await(ABSFutureTask<T> m) {
		if (!m.evaluateGuard()) {
			possibleAwait = m;
			completedMessage = false;

			messageQueue.add(m);

			throw new AwaitException();

			//
			// if (conditionContinuations == null)
			// conditionContinuations = new ConcurrentHashMap<Supplier<Boolean>,
			// Set<ABSFutureTask<?>>>();
			//
			// if (conditionContinuations.containsKey(s))
			// conditionContinuations.get(s).add(m);
			// else {
			// Set<ABSFutureTask<?>> continuations = new
			// HashSet<ABSFutureTask<?>>();
			// continuations.add(m);
			// conditionContinuations.put(s, continuations);
			// }
			//
			// if (futureContinuations == null) {
			// futureContinuations = new ConcurrentHashMap<Future<?>,
			// Set<ABSFutureTask<?>>>();
			// }
			//
			// if (futureContinuations.containsKey(s))
			// futureContinuations.get(s).add(m);
			// else {
			// Set<ABSFutureTask<?>> continuations = new
			// HashSet<ABSFutureTask<?>>();
			// continuations.add(m);
			// futureContinuations.put(s, continuations);
			// }
			//
		}
	}

	private class AwaitException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	@Override
	public <T> void await(Guard guard, Callable<T> message) {
		await(guard, new ABSFutureTask<T>(message, IS_REACHABLE, hostName));
	}

	@Override
	public <T> void await(Guard guard, Runnable message) {
		await(guard, new ABSFutureTask<T>(message, IS_REACHABLE, hostName));
	}

}
