package abs.api.cwi;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class ABSFuture<V> implements Serializable {

	public CompletableFuture<V> f;
	protected FutureTask<V> runningF;
	public URI futureID = null;

	protected ABSFuture(String hostname) {
		f = new CompletableFuture<V>();

		try {
			futureID = new URI("http", "future", hostname, f.hashCode(), null, null, null);
		} catch (URISyntaxException e) {
			System.out.println("futureID is wrong");
			e.printStackTrace();
		}
	}

	public ABSFuture(Runnable message, int status, String hostname) {
		this(hostname);
		if (status == AbstractActor.IS_REACHABLE) {
			runningF = new FutureTask<V>(message, null);
		}

	}

	public ABSFuture(Callable<V> message, int status, String hostname) {
		this(hostname);
		if (status == AbstractActor.IS_REACHABLE) {
			runningF = new FutureTask<V>(message);
		}

	}

	protected void setF(CompletableFuture<?> f) {
		this.f = (CompletableFuture<V>) f;
	}

	protected void finished() {
		try {
			this.f.complete(runningF.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ABSFuture) {
			ABSFuture<?> other = (ABSFuture<?>) obj;
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
		return futureID.hashCode();
	}
}
