package abs.api.cwi;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public interface Actor {

	public <V> ABSFutureTask<V> send(Runnable message);

	public <V> ABSFutureTask<V> send(Callable<V> message);

	public <T> void await(Guard guard, Callable<T> message);

	public <T> void await(Guard guard, Runnable message);

	// public <T> T awaitRep(Supplier<Boolean> repCondition, Runnable before,
	// Runnable after, Callable<T> end,
	// Supplier<Future<?>> s);
	//
	// public <T> T awaitRep(Supplier<Boolean> repCondition, Supplier<Boolean>
	// s, Runnable before, Runnable after,
	// Callable<T> end);

//	public <T> T await(URI futureID, Runnable message);
//
//	public <T> T await(URI futureID, Callable<T> message);
//
//	public <T> T get(URI futureID);
}
