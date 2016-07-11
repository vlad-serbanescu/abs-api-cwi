package abs.api.cwi;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public interface Actor {

	public  <V> ABSFuture<V> send(Runnable message);

	public  <V> ABSFuture<V> send(Callable<V> message);

	public  <T> T await(Supplier<Boolean> s, Callable<T> message);

	public  <T> T await(Future<?> s, Callable<T> message);

	public  <T> T await(Supplier<Boolean> s, Runnable message);

	public  <T> T await(Future<?> s, Runnable message);

	public  <T> T awaitRep(Supplier<Boolean> repCondition, Runnable before, Runnable after, Callable<T> end,
			Supplier<Future<?>> s);

	public  <T> T awaitRep(Supplier<Boolean> repCondition, Supplier<Boolean> s, Runnable before, Runnable after,
			Callable<T> end);

	public <T> T await(URI futureID, Runnable message) ;

	public <T> T await(URI futureID, Callable<T> message) ;

	public <T> T get(URI futureID) ;
}
