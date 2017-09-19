/*
 * The class that will be extended by ABS classes
 * 
 */
package abs.api.cwi;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActorSystem {
	private static ConcurrentHashMap<ABSFuture<?>, Set<Actor>> awaitingOnFutureActors = new ConcurrentHashMap<>();
	static Callable<ABSFuture<Object>> emptyTask = () -> ABSFuture.of(null);

	/** The main executor. */
	private static ExecutorService mainExecutor = Executors.newCachedThreadPool();

	private ActorSystem() { }

	static void submit(Runnable task) {
		mainExecutor.submit(task);
	}

	static void releaseAll(ABSTask<?> m) {
		Set<Actor> freedActors = awaitingOnFutureActors.remove(m);
		if (freedActors != null) {
			freedActors.forEach(localActor -> localActor.send(emptyTask));
		}
	}

	static void awaitActorOnFuture(Actor a, ABSFuture<?> future) {
		if (awaitingOnFutureActors.containsKey(future)) {
			awaitingOnFutureActors.get(future).add(a);
		} else {
			Set<Actor> dependingActors = new HashSet<>();
			dependingActors.add(a);
			awaitingOnFutureActors.put(future, dependingActors);
		}
	}

	public static void shutdown() {
		mainExecutor.shutdown();
	}
}
