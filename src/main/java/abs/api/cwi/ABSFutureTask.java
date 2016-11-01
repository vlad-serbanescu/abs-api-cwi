package abs.api.cwi;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class ABSFutureTask<V> implements Serializable, Future<V>, Runnable {
	protected Guard enablingCondition = null;
	protected final CompletableFuture<V> f;
	protected Callable<V> task;
	protected int syncCallCounter = 0;
	protected boolean syncChainInitiator = false; 

	public ABSFutureTask(Callable<V> task, int x) {
		this(task);
		this.syncCallCounter = x;
	}

	public ABSFutureTask(Runnable task, int x) {
		this(task);
		this.syncCallCounter = x;
	}

	@SuppressWarnings("unchecked")
	ABSFutureTask(Runnable message) {
		this((Callable<V>) Executors.callable(message));
	}

	ABSFutureTask(Callable<V> message) {
		if (message == null)
			throw new NullPointerException();
		this.task = message;
		f = new CompletableFuture<V>();
	}

	@SuppressWarnings("unchecked")
	ABSFutureTask<V> continueWith(Runnable continuation, Guard guard) {
		return continueWith((Callable<V>) Executors.callable(continuation), guard);
	}

	ABSFutureTask<V> continueWith(Callable<V> continuation, Guard guard) {
		this.task = continuation;
		this.enablingCondition = guard;
		return this;
	}

	boolean isSyncCall() {
		return syncCallCounter != 0;
	}

	@Override
	public V get() {
		try {
			return f.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null; // TODO re-throw?
		}
	}

	boolean evaluateGuard() {
		return enablingCondition == null || enablingCondition.evaluate();
	}

	@Override
	public void run() {
		try {
			V value = task.call();
			f.complete(value);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO never cancellable
		return false;
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return f.isDone();
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		throw new UnsupportedOperationException("will be implemented in future");
	}

	// TODO implement equals, hashCode and toString when necessary
}
