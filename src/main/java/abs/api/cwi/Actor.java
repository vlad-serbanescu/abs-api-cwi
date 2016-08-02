package abs.api.cwi;

import java.util.concurrent.Callable;

public interface Actor {
	public ABSFutureTask<Void> send(Runnable message);

	public <V> ABSFutureTask<V> send(Callable<V> message);

	public void await(Guard guard, Callable message);

	public void await(Guard guard, Runnable message);
}
