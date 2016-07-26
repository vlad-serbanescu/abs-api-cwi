package abs.api.cwi;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class ABSFutureTask<V> implements Serializable, Future<V>, Runnable {

	protected Guard enablingCondition = null;
	protected final CompletableFuture<V> f;
	protected FutureTask<V> runningF = null;

	ABSFutureTask(Runnable message) {
		runningF = new FutureTask<>(message, null);
		f = new CompletableFuture<V>();
	}

	ABSFutureTask(Callable<V> message) {
		runningF = new FutureTask<>(message);
		f = new CompletableFuture<V>();
	}
	
	ABSFutureTask<V> continueWith(FutureTask<V> message, Guard guard) {
		runningF = message;
		this.enablingCondition = guard;
		return this;
	}
		
	@Override
	public V get(){
		try {
			return f.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	void finished() {
		try {
			this.f.complete(runningF.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	boolean evaluateGuard() {
		return enablingCondition == null || enablingCondition.evaluate();
	}

	@Override
	public void run() {
		runningF.run();
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
