package abs.api.cwi;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class LocalActor extends AbstractActor {

	// Semaphore s;
	private AtomicBoolean executorIsRunning = new AtomicBoolean(false);
	protected ConcurrentLinkedQueue<ABSFuture<?>> messageQueue;
	public ConcurrentHashMap<Future<?>, Set<ABSFuture<?>>> futureContinuations;
	public ConcurrentHashMap<Supplier<Boolean>, Set<ABSFuture<?>>> conditionContinuations;

	protected ABSFuture<?> possibleAwait = null;
	protected boolean completedMessage = true;

	public LocalActor() {
		super();
		messageQueue = new ConcurrentLinkedQueue<ABSFuture<?>>();
		if (localActorMap == null) {
			localActorMap = new ConcurrentSkipListSet<LocalActor>();
		}

		localActorMap.add(this);

	}

	private boolean isEmptyAndDie() {
		synchronized (executorIsRunning) {
			if (messageQueue.isEmpty()) {
				executorIsRunning.set(false);
				return true;
			} else
				return false;
		}
	}
	
	private boolean notEmptyAndStart() {
		synchronized (executorIsRunning) {
			return executorIsRunning.compareAndSet(false, true);
		}
	}
	
	public void notifyLocked(ABSFuture<?> futureLock){
		if (this.futureContinuations != null)
			if (this.futureContinuations.containsKey(futureLock.f)) {
				Set<ABSFuture<?>> continuations = this.futureContinuations.remove(futureLock.f);
				Iterator<ABSFuture<?>> it = continuations.iterator();
				while(it.hasNext()){
					ABSFuture<?> continuation = it.next();
					this.send(continuation);
				}
			}

	}
	
	

	@Override
	public <V> ABSFuture<V> send(Runnable message) {
		ABSFuture<V> m = new ABSFuture<V>(message, IS_REACHABLE, hostName);
		 send(m);
		 return m;
	}

	@Override
	public <V> ABSFuture<V> send(Callable<V> message) {
		ABSFuture<V> m = new ABSFuture<V>(message, IS_REACHABLE, hostName);
		 send(m);
		 return m;

	}

	protected <V> void send(ABSFuture<V> messageArgument) {
		messageQueue.add(messageArgument);
		
		if (notEmptyAndStart()) {
			new Thread(new Runnable() {

				public void run() {
					while (!isEmptyAndDie()) {
						ABSFuture<?> m;
						m = messageQueue.poll();
						m.runningF.run();
						if (!completedMessage) {
							possibleAwait.setF(m.f);
							completedMessage = true;
						} else {
							m.finished();
							releaseAll(m);
						}
					}
				}
			}).start();
		}
	}

	@Override
	public <T> T await(Supplier<Boolean> s, Callable<T> message) {
		ABSFuture<T> m = new ABSFuture<T>(message, IS_REACHABLE, hostName);
		return await(s, m);
	}

	@Override
	public <T> T await(Future<?> s, Callable<T> message) {
		ABSFuture<T> m = new ABSFuture<T>(message, IS_REACHABLE, hostName);
		return await(s, m);

	}

	@Override
	public <T> T await(Supplier<Boolean> s, Runnable message) {
		ABSFuture<T> m = new ABSFuture<T>(message, IS_REACHABLE, hostName);
		return await(s, m);

	}

	@Override
	public <T> T await(Future<?> s, Runnable message) {
		ABSFuture<T> m = new ABSFuture<T>(message, IS_REACHABLE, hostName);
		return await(s, m);

	}

	private <T> T await(Supplier<Boolean> s, ABSFuture<T> m) {
		if (s.get() == true) {
			m.runningF.run();
			try {
				return m.runningF.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			if (conditionContinuations == null)
				conditionContinuations = new ConcurrentHashMap<Supplier<Boolean>, Set<ABSFuture<?>>>();

			if (conditionContinuations.containsKey(s))
				conditionContinuations.get(s).add(m);
			else {
				Set<ABSFuture<?>> continuations = new HashSet<ABSFuture<?>>();
				continuations.add(m);
				conditionContinuations.put(s, continuations);
			}
			possibleAwait = m;
			completedMessage = false;
			return null;
		}
	}

	private <T> T await(Future<?> s, ABSFuture<T> m) {
		if (s.isDone()) {
			m.runningF.run();
			try {
				return m.runningF.get();
			} catch (InterruptedException e) {

				e.printStackTrace();
				return null;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			if (futureContinuations == null) {
				futureContinuations = new ConcurrentHashMap<Future<?>, Set<ABSFuture<?>>>();
			}

			if (futureContinuations.containsKey(s))
				futureContinuations.get(s).add(m);
			else {
				Set<ABSFuture<?>> continuations = new HashSet<ABSFuture<?>>();
				continuations.add(m);
				futureContinuations.put(s, continuations);
			}
			possibleAwait = m;
			completedMessage = false;
			return null;
		}
	}

	@Override
	public <T> T awaitRep(Supplier<Boolean> repCondition, Runnable before, Runnable after, Callable<T> end,
			Supplier<Future<?>> s) {
		Future<?> toHold = s.get();
		if (toHold.isDone()) {
			if (repCondition.get() == true) {
				before.run();
				after.run();
				return awaitRep(repCondition, before, after, end, s);
			} else {
				try {
					return end.call();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

		} else {
			Callable<T> loopContinuation = (Callable<T>) (() -> {
				after.run();
				if (repCondition.get() == true)
					return awaitRep(repCondition, before, after, end, s);
				else
					return end.call();
			});
			before.run();
			return await(toHold, loopContinuation);
		}
	}

	@Override
	public <T> T awaitRep(Supplier<Boolean> repCondition, Supplier<Boolean> s, Runnable beforeAwait,
			Runnable afterAwait, Callable<T> end) {
		if (s.get() == true) {
			if (repCondition.get() == true) {
				beforeAwait.run();
				afterAwait.run();
				return awaitRep(repCondition, s, beforeAwait, afterAwait, end);
			} else {
				try {
					return end.call();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

		} else {
			Callable<T> loopContinuation = (Callable<T>) (() -> {
				afterAwait.run();
				if (repCondition.get() == true)
					return awaitRep(repCondition, s, beforeAwait, afterAwait, end);
				else
					return end.call();
			});
			beforeAwait.run();
			return await(s, loopContinuation);
		}
	}
}
