package abs.api.cwi;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface Actor {
	public <V> Future<V> send(Runnable message);

	public <V> Future<V> send(Callable<V> message);

	public void await(Guard guard, Callable message);

	public void await(Guard guard, Runnable message);
}
