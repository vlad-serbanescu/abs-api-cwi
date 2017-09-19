package abs.api.cwi;

import java.util.concurrent.Callable;

public interface Actor {
	<V> ABSFuture<V> send(Callable<ABSFuture<V>> message);

	<V> ABSFuture<V> spawn(Guard guard, Callable<ABSFuture<V>> message);

	<T,V> ABSFuture<T> getSpawn(ABSFuture<V> f, CallableGet<T, V> message);

	default int compare(Actor o) {
		return 0;
	}
}
