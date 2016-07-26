package abs.api.cwi;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

public class ABSFutureTask<V> implements Serializable {

	protected final Supplier<Boolean> enablingCondition;
	
	protected final CompletableFuture<V> f;
	protected final FutureTask<V> runningF;
	public final URI futureID;

	public ABSFutureTask(Runnable message, int status, String hostname) {
		this(message, status, hostname, new CompletableFuture<>());
	}

	public ABSFutureTask(Callable<V> message, int status, String hostname) {
		this(message, status, hostname, new CompletableFuture<>());
	}
	
	public ABSFutureTask(Callable<V> message, int status, String hostname, CompletableFuture<V> callerFuture) {
		this(new FutureTask<V>(message), status, hostname, callerFuture, null);
	}
	
	public ABSFutureTask(Runnable message, int status, String hostname, CompletableFuture<V> callerFuture) {
		this(new FutureTask<V>(message, null), status, hostname, callerFuture, null);
	}
	
	public ABSFutureTask(FutureTask<V> message, int status, String hostname, CompletableFuture<V> callerFuture, Supplier<Boolean> guard) {
		f = callerFuture;
		runningF = message;
		this.enablingCondition = guard;
		URI id = null;
		try {
			id = new URI("http", "future", hostname, this.hashCode(), null, null, null);   // TODO
		} catch (URISyntaxException e) {
			System.out.println("futureID is wrong");
			e.printStackTrace();
		}
		futureID = id;
	}
		
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
		return enablingCondition.get(); 
	}

	@Override
	public boolean equals(Object obj) {		// TODO
		if (obj instanceof ABSFutureTask) {
			ABSFutureTask<?> other = (ABSFutureTask<?>) obj;
			return other.futureID.equals(futureID);
		}
		if (obj instanceof URI) {
			URI fID = (URI) obj;
			return fID.equals(futureID);

		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return futureID.hashCode();	// TODO
	}
	
	@Override
	public String toString() {
		// TODO proper string representation
		return futureID.toString();
	}
}
