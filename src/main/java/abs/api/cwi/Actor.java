package abs.api.cwi;

import java.util.concurrent.Callable;

public interface Actor {

	public void sendSync(Runnable message, Runnable continuation);

	// public <V>void sendSync(Callable<V> message, Runnable continuation);

	public void sendSync(Runnable message, Callable continuation);

	public <V, T> void sendSync(Callable<V> message, Callable continuation);

	// public ABSFutureTask<Void> sendSync(Runnable message, NotRunnable
	// continuation);

	public <V> ABSFutureTask<V> sendSync(CallablewFut continuation, Callable<V> message);

	public <V> ABSFutureTask<V> sendSync(Callable<V> message, RunnablewFut continuation);

	public ABSFutureTask<Void> send(Runnable message);

	public <V> ABSFutureTask<V> send(Callable<V> message);

	public <V> void send(ABSFutureTask<V> message);

	public void await(Callable message, Guard guard);

	public void await(Guard guard, Runnable message);

	public <T,V> void get(CallableGet<T,V> message, ABSFutureTask<V> f);

	public <V> void get(ABSFutureTask<V> f, RunnableGet<V> message);

	public void enableMessages(ABSFutureTask<?> m);

	default int compare(Actor o) {
		return 0;
	}

}
