/*
 * The class that will be extended by ABS classes
 * 
 */
package abs.api.cwi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import javax.net.ServerSocketFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ABSActor.
 */
public class DeploymentComponent {
	private static ConcurrentHashMap<ABSFutureTask<?>, Set<Actor>> lockedOnFutureActors = new ConcurrentHashMap<>();

	/** The main executor. */
	private static ExecutorService mainExecutor = Executors.newFixedThreadPool(8);

	private DeploymentComponent() {
	}

	public static void submit(Runnable task) {
		mainExecutor.submit(task);
	}

	public static void releaseAll(ABSFutureTask<?> m) {
		Set<Actor> freedActors = lockedOnFutureActors.remove(m);
		//System.out.println(freedActors);
		if (freedActors != null) {
			for (Actor localActor : freedActors) {
				localActor.send((Runnable) () -> {
				}); // just awaken the actor if it has no running task at the
					// moment
			}
		}
		//System.out.println("Finished releasing");
	}

	public static void lockActorOnFuture(Actor a, ABSFutureTask<?> future) {
		if (lockedOnFutureActors.containsKey(future)) {
			lockedOnFutureActors.get(future).add(a);
		} else {
			Set<Actor> dependingActors = new HashSet<>();
			dependingActors.add(a);
			lockedOnFutureActors.put(future, dependingActors);
		}
	}

	public static void shutdown() {
		mainExecutor.shutdown();
		try {
			while (!mainExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
