package abs.api.cwi;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocalActor implements Actor {

	public ABSFutureTask<?> runningMessage;
	private AtomicBoolean mainTaskIsRunning = new AtomicBoolean(false);
	protected ConcurrentLinkedQueue<ABSFutureTask<?>> messageQueue = new ConcurrentLinkedQueue<ABSFutureTask<?>>();
	protected ConcurrentHashMap<ABSFutureTask<?>, Set<ABSFutureTask<?>>> lockedQueue = new ConcurrentHashMap<>();
	protected int syncCallContext = 0; // TODO : might need to be atomic
	private int counter = 0; // TODO : might need to be atomic

	class MainTask implements Runnable {
		@Override
		public void run() {
			// System.out.println(LocalActor.this + " from main task:" +
			// messageQueue);
			if (!takeOrDie())
				return; // no enabled message or empty queue
			try {
				// System.out.println("before running: "+runningMessage);
				if (runningMessage.isSyncCall())
					syncCallContext = runningMessage.syncCallCounter;
				if (runningMessage.syncChainInitiator) {
					syncCallContext = 0; // exit the sync call chain context
					runningMessage.syncChainInitiator = false;
					runningMessage.syncCallCounter = 0;
				}
				runningMessage.run();
				DeploymentComponent.releaseAll(runningMessage);
			} catch (AwaitException ae) {
				// System.out.println("awaiting inside: "+messageQueue);
			}
			// in case there are more actors than threads, give other actors a
			// chance to run
			DeploymentComponent.submit(this);
		}
	}

	private boolean takeOrDie() {
		synchronized (mainTaskIsRunning) {
			//System.out.println("SC: "+ syncCallContext);
			//System.out.println("SC: "+ messageQueue);
			for (ABSFutureTask<?> absFutureTask : messageQueue) {
				if ((syncCallContext == 0 || syncCallContext == absFutureTask.syncCallCounter)
						&& absFutureTask.evaluateGuard()) {
					runningMessage = absFutureTask;
					messageQueue.remove(absFutureTask);
					return true;
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

	private int newCounter() {
		if (syncCallContext == 0) {
			syncCallContext = ++counter;
			runningMessage.syncChainInitiator = true;
		}
		runningMessage.syncCallCounter = syncCallContext;
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
	public <V,T> void sendSync(Callable<V> message, Callable continuation) {
		ABSFutureTask<V> m = new ABSFutureTask<V>(message, newCounter());
		send(m);
		await(runningMessage.continueWith(continuation, Guard.convert(m)));
	}

	// @Override
	// public <V> void sendSync(Callable<V> message, RunnablewF continuation) {
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
		messageQueue.add(messageArgument);
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
		this.syncCallContext = context;
	}

	@Override
	public final void await(Guard guard, Runnable message) {
		int context = this.syncCallContext;
		this.syncCallContext = 0;
		await(runningMessage.continueWith(message, guard));
		this.syncCallContext = context;
	}

	private <T> void await(ABSFutureTask<T> m) {
		if (!m.evaluateGuard()) {
			m.enablingCondition.addFuture(this);
			/*if (m.enablingCondition instanceof FutureGuard) {
				ABSFutureTask<?> key = ((FutureGuard) m.enablingCondition).future;
				if (lockedQueue.containsKey(key))
					lockedQueue.get(key).add(m);
				else {
					Set<ABSFutureTask<?>> disabledSet = new HashSet<>();
					disabledSet.add(m);
					lockedQueue.put(key, disabledSet);
				}
				m.enablingCondition = null;
			} else*/
				messageQueue.add(m);
			throw new AwaitException();
		}
	}

	private class AwaitException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	public void enableMessages(ABSFutureTask<?> key) {
//		System.out.println("LQ: "+ lockedQueue);
//		System.out.println("LQ: "+ key +" "+ lockedQueue.containsKey(key));
		Set<ABSFutureTask<?>> freedtasks =lockedQueue.remove(key);
		for (ABSFutureTask<?> task : freedtasks) {
			send(task);
		}
		
	}

}
