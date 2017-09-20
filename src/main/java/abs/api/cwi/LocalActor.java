package abs.api.cwi;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static abs.api.cwi.ABSTask.emptyTask;

class AbsKey implements Comparable<AbsKey> {
	private int priority;
	private int strict;

	public AbsKey(int priority, boolean strict) {
		super();
		this.priority = priority;
		this.strict = strict ? 1 : 0;
	}

	boolean isStrict() { return strict == 1;}

	@Override
	public int compareTo(AbsKey o) {
		// In ascending order, we should get the highest priority/strictness first
		if (o.priority == priority) {
			return o.strict - this.strict;
		} else
			return o.priority - this.priority;
	}
}

public class LocalActor implements Actor {
	private static final boolean STRICT = true;
	private static final boolean NON_STRICT = false;
	private static final int DEFAULT_PRIORITY = 0;
	private static final int HIGH_PRIORITY = 1;

	private ABSTask<?> runningMessage;
	private final AtomicBoolean mainTaskIsRunning = new AtomicBoolean(false);
	private ConcurrentSkipListMap<AbsKey, ConcurrentLinkedQueue<ABSTask<?>>> messageQueue = new ConcurrentSkipListMap<>();

	private class MainTask implements Runnable {
		@Override
		public void run() {
			if (!takeOrDie())
				return; // no enabled message or empty queue
			runningMessage.run();
			ActorSystem.submit(this);  // instead of a loop we submit again, thus allowing other actors' tasks to get a chance of being scheduled in the meantime
		}
	}

	private boolean takeOrDie() {
		synchronized (mainTaskIsRunning) {
			// this synchronized block is to remove the race condition between checking if nothing is there to be executed and resetting the flag mainTaskIsRunning
			for (AbsKey key : messageQueue.keySet()) {
				ConcurrentLinkedQueue<ABSTask<?>> bucket = messageQueue.get(key);
				for (ABSTask<?> absTask : bucket) {
					if (absTask.evaluateGuard()) {
						runningMessage = absTask;
						bucket.remove(absTask);
						return true;
					}
				}
				if (!bucket.isEmpty() && key.isStrict()) {
					// when there are disabled tasks in strict bucket, we cannot execute a lower priority task
					mainTaskIsRunning.set(false);
					return false;
				}
			}
			mainTaskIsRunning.set(false);
			return false;
		}
	}

	private boolean notRunningThenStart() {
		synchronized (mainTaskIsRunning) {
			return mainTaskIsRunning.compareAndSet(false, true);
		}
	}

	private <V> void schedule(ABSTask<V> messageArgument, int priority, boolean strict) {
		if (emptyTask.equals(messageArgument.task))
			return;

		AbsKey key = new AbsKey(priority, strict);
		if (messageQueue.containsKey(key)) {
			messageQueue.get(key).add(messageArgument);
		} else {
			ConcurrentLinkedQueue<ABSTask<?>> bucket = new ConcurrentLinkedQueue<>();
			bucket.add(messageArgument);
			messageQueue.put(key, bucket);
		}
	}

	@Override
	public final <V> ABSFuture<V> send(Callable<ABSFuture<V>> message) {
		ABSTask<V> m = new ABSTask<>(message);
		schedule(m, DEFAULT_PRIORITY, NON_STRICT);
		if (notRunningThenStart()) {
			ActorSystem.submit(new MainTask());
		}
		return m.getResultFuture();
	}

	@Override
	public final <V> ABSFuture<V> spawn(Guard guard, Callable<ABSFuture<V>> message) {
		ABSTask<V> m = new ABSTask<>(message, guard);
		schedule(m, DEFAULT_PRIORITY, NON_STRICT);
		return m.getResultFuture();
	}

	@Override
	public final <T, V> ABSFuture<T> getSpawn(ABSFuture<V> f, CallableGet<T, V> message) {
		return getSpawn(f, message, DEFAULT_PRIORITY, NON_STRICT);
	}

	@Override
	public final <T, V> ABSFuture<T> getSpawn(ABSFuture<V> f, CallableGet<T, V> message, int priority, boolean strict) {
		ABSTask<T> m = new ABSTask<>(() -> message.run(f.getOrNull()), Guard.convert(f));
		schedule(m, priority, strict);
		return m.getResultFuture();
	}
}
