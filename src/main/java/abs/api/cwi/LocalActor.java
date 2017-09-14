package abs.api.cwi;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

class AbsKey implements Comparable<AbsKey> {

	int priority;
	boolean strict;

	public AbsKey(int priority, boolean strict) {
		super();
		this.priority = priority;
		this.strict = strict;
	}

	@Override
	public int compareTo(AbsKey o) {
		if (o.priority == priority) {
			if (o.strict == strict)
				return 0;
			else if (strict && !(o.strict))
				return 1;
			else
				return -1;
		} else
			return priority - o.priority;

	}
}

public class LocalActor implements Actor {

	public ABSFutureTask<?> runningMessage;
	private AtomicBoolean mainTaskIsRunning = new AtomicBoolean(false);
	protected ConcurrentSkipListMap<AbsKey, Set<ABSFutureTask<?>>> messageQueue = new ConcurrentSkipListMap<>();
	protected ConcurrentHashMap<ABSFutureTask<?>, Set<ABSFutureTask<?>>> lockedQueue = new ConcurrentHashMap<>();
	protected int syncCallContext = 0; // TODO : might need to be atomic
	private int counter = 0; // TODO : might need to be atomic
	// public boolean blocked = false;
	// protected ABSFutureTask<?> toResume = null;
	// protected ABSFutureTask<?> blockedOn = null;

	class MainTask implements Runnable {
		@Override
		public void run() {
			// System.out.println(LocalActor.this + " from main task:" +
			// messageQueue);
			if (!takeOrDie())
				return; // no enabled message or empty queue
			// try {
			// System.out.println("before running: "+runningMessage);
			if (runningMessage.isSyncCall())
				syncCallContext = runningMessage.syncCallContext;
			if (runningMessage.syncChainInitiator) {
				syncCallContext = 0; // exit the sync call chain context
				runningMessage.syncChainInitiator = false;
				runningMessage.syncCallContext = 0;
			}
			runningMessage.run();
			DeploymentComponent.releaseAll(runningMessage);
			// } catch (AwaitException ae) {
			// System.out.println("awaiting inside: "+messageQueue);
			// if (ae.block) {
			// blocked = ae.block;
			// toResume = runningMessage;
			// blockedOn = ae.blockOn;
			// }

			// }
			// in case there are more actors than threads, give other actors
			// a
			// chance to run
			DeploymentComponent.submit(this);
		}
	}

	private boolean takeOrDie() {
		synchronized (mainTaskIsRunning) {
			// System.out.println("SC: "+ syncCallContext);
			// System.out.println("SC: "+ messageQueue);
			// if (!blocked) {
			// // System.out.println(this + "TO resume= " + toResume);
			// if (toResume != null) {
			// runningMessage = toResume;
			// toResume = null;
			// blockedOn = null;
			// return true;
			// } else

			for (AbsKey ak : messageQueue.keySet()) {

				Set<ABSFutureTask<?>> bucket = messageQueue.get(ak);
				ABSFutureTask<?> lastTask = null;
				for (ABSFutureTask<?> absFutureTask : bucket) {
					if ((syncCallContext == 0 || syncCallContext == absFutureTask.syncCallContext)
							&& absFutureTask.evaluateGuard()) {
						runningMessage = absFutureTask;
						bucket.remove(absFutureTask);
						return true;
					}
					lastTask = absFutureTask;
				}
				if (lastTask != null && lastTask.strict) {
					mainTaskIsRunning.set(false);
					return false;
				}

				// }
				// // } else {
				// if (blockedOn.isDone()) {
				// runningMessage = toResume;
				// toResume = null;
				// blockedOn = null;
				// blocked=false;
				// return true;
				// }
				// }
				
			}
			mainTaskIsRunning.set(false);
			return false;
		}
	}

	private boolean notRunningThenStart() {
		synchronized (mainTaskIsRunning) {
			// if (!blocked)
			return mainTaskIsRunning.compareAndSet(false, true);
		}
	}

	private int newCounter() {
		if (syncCallContext == 0) {
			syncCallContext = ++counter;
			runningMessage.syncChainInitiator = true;
		}
		runningMessage.syncCallContext = syncCallContext;
		return syncCallContext;
	}

	@Override
	public <V> ABSFutureTask<V> sendSync(Callable<V> message, RunnablewFut continuation) {
		ABSFutureTask<V> m = new ABSFutureTask<V>(message, newCounter());
		send(m);
		Runnable continuation2 = () -> continuation.run(m);
		await(runningMessage.continueWith(continuation2, Guard.convert(m)));
		return m;
	}

	@Override
	public <V> ABSFutureTask<V> sendSync(CallablewFut continuation, Callable<V> message) {
		ABSFutureTask<V> m = new ABSFutureTask<V>(message, newCounter());
		send(m);
		Callable continuation2 = () -> continuation.run(m);
		await(runningMessage.continueWith(continuation2, Guard.convert(m)));
		return m;
	}

	@Override
	public void sendSync(Runnable message, Runnable continuation) {
		ABSFutureTask<Void> m = new ABSFutureTask<>(message, newCounter());
		send(m);
		await(runningMessage.continueWith(continuation, Guard.convert(m)));
		// return m;
	}

	@Override
	public <V, T> void sendSync(Callable<V> message, Callable continuation) {
		ABSFutureTask<V> m = new ABSFutureTask<V>(message, newCounter());
		send(m);
		await(runningMessage.continueWith(continuation, Guard.convert(m)));
	}

	// @Override
	// public <V> void sendSync(Callable<V> message, RunnablewF
	// continuation) {
	// ABSFutureTask<V> m = new ABSFutureTask<V>(message, newCounter());
	// send(m);
	// await(runningMessage.continueWith(continuation, Guard.convert(m)));
	// //return m;
	// }
	//
	@Override
	public void sendSync(Runnable message, Callable continuation) {
		ABSFutureTask<Void> m = new ABSFutureTask<>(message, newCounter());
		send(m);
		await(runningMessage.continueWith(continuation, Guard.convert(m)));

	}

	@Override
	public final ABSFutureTask<Void> send(Runnable message) {
		ABSFutureTask<Void> m = new ABSFutureTask<>(message);
		send(m);
		return m;
	}

	@Override
	public final <V> ABSFutureTask<V> send(Callable<V> message) {
		ABSFutureTask<V> m = new ABSFutureTask<V>(message);
		send(m);
		return m;

	}

	public <V> void send(ABSFutureTask<V> messageArgument) {
		if (messageQueue.containsKey(messageArgument.ak)) {
			messageQueue.get(messageArgument.ak).add(messageArgument);
		} else {
			Set<ABSFutureTask<?>> bucket = new ConcurrentSkipListSet<>();
			bucket.add(messageArgument);
			messageQueue.put(messageArgument.ak, bucket);
		}
		if (notRunningThenStart()) {
			DeploymentComponent.submit(new MainTask());
		}
	}

	/**
	 * This is called only from outside, and is therefore for async calls. So it
	 * resets the sync-call-context if AwaitException is thrown. Otherwise, it
	 * keeps the sync-call-context value.
	 */
	@Override
	public final void await(Callable message, Guard guard) {
		int context = this.syncCallContext;
		this.syncCallContext = 0;
		await(runningMessage.continueWith(message, guard));
		//this.syncCallContext = context;
	}

	@Override
	public final void await(Guard guard, Runnable message) {
		int context = this.syncCallContext;
		this.syncCallContext = 0;
		await(runningMessage.continueWith(message, guard));
		//this.syncCallContext = context;
	}

	private <T> void await(ABSFutureTask<T> m) {
		if (!m.evaluateGuard()) {
			// if (block) {
			// System.out.println(this + " blocks on " +
			// m.enablingCondition);
			// FutureGuard enablingFuture = (FutureGuard)
			// (m.enablingCondition);
			// enablingFuture.blockOnFuture(this);
			// messageQueue.add(m);
			// throw new AwaitException(enablingFuture.future);
			//
			// } else {
			m.enablingCondition.addFuture(this);

			// }
			/*
			 * if (m.enablingCondition instanceof FutureGuard) {
			 * ABSFutureTask<?> key = ((FutureGuard)
			 * m.enablingCondition).future; if (lockedQueue.containsKey(key))
			 * lockedQueue.get(key).add(m); else { Set<ABSFutureTask<?>>
			 * disabledSet = new HashSet<>(); disabledSet.add(m);
			 * lockedQueue.put(key, disabledSet); } m.enablingCondition = null;
			 * } else
			 */
			if (messageQueue.containsKey(m.ak)) {
				messageQueue.get(m.ak).add(m);
			} else {
				Set<ABSFutureTask<?>> bucket = new ConcurrentSkipListSet<>();
				bucket.add(m);
				messageQueue.put(m.ak, bucket);
			}
			// throw new AwaitException();
		} else {
			if (m.isSyncCall())
				syncCallContext = m.syncCallContext;
			if (m.syncChainInitiator) {
				syncCallContext = 0; // exit the sync call chain context
				m.syncChainInitiator = false;
				m.syncCallContext = 0;
			}
			m.run();
			DeploymentComponent.releaseAll(m);
			// throw new AwaitException();
		}
	}

	private class AwaitException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		// private boolean block;
		// private ABSFutureTask<?> blockOn;

		// public AwaitException(boolean block) {
		// this.block = block;
		// }
		//
		// public AwaitException(ABSFutureTask<?> b) {
		// this(true);
		// this.blockOn = b;
		// }

	}

	public void enableMessages(ABSFutureTask<?> key) {
		// System.out.println("LQ: "+ lockedQueue);
		// System.out.println("LQ: "+ key +" "+
		// lockedQueue.containsKey(key));
		Set<ABSFutureTask<?>> freedtasks = lockedQueue.remove(key);
		for (ABSFutureTask<?> task : freedtasks) {
			send(task);
		}

	}

	@Override
	public <T, V> void get(CallableGet<T, V> continuation, ABSFutureTask<V> f) {
		//newCounter();
		runningMessage.priority=++counter;
		runningMessage.strict=true;
		Callable continuation2 = () -> continuation.run(f.get());
		await(runningMessage.continueWith(continuation2, Guard.convert(f)));

	}

	@Override
	public <V> void get(ABSFutureTask<V> f, RunnableGet<V> continuation) {
		//newCounter();
		runningMessage.priority=++counter;
		runningMessage.strict=true;
		Runnable continuation2 = () -> continuation.run(f.get());
		await(runningMessage.continueWith(continuation2, Guard.convert(f)));

	}

}
