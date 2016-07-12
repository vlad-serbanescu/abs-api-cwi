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
public abstract class AbstractActor implements Comparable<AbstractActor>,Actor {

	/** The Constant IS_REMOTE. */
	final static int IS_REMOTE = 1;

	/** The Constant IS_REACHABLE. */
	final static int IS_REACHABLE = 2;

	/** The this machine. */
	static ServerSocket thisMachine = null;
	static Socket communicationSocket = null;

	/** The write remote machines. */
	static ConcurrentHashMap<String, ObjectOutputStream> machineOutputStreams = null;

	/** The read remote machines. */
	static ConcurrentHashMap<String, ObjectInputStream> machineInputStreams = null;

	/** The actor map. */
	public static ConcurrentHashMap<URI, ReachableActor> actorMap = null;

	public static ConcurrentSkipListSet<LocalActor> localActorMap = null;

	public static ConcurrentHashMap<URI, List<AbstractActor>> remotePassedFutures;

	public static ConcurrentHashMap<URI, Future<?>> remoteUncompletedFutures = null;
	
	public static ConcurrentHashMap<ABSFuture<?>, Set<LocalActor>> lockedOnFutureActors = null;
	
	//TODO use this map to notify Actors of a completed future and avoid looping through the entire map of actors

	/** The main executor. */
	// static ActorExecutor mainExecutor = null;

	/** The s. */

	String hostName = null;

	/**
	 * Instantiates a new ABS actor.
	 */
	public AbstractActor() {
		// if (mainExecutor == null)
		// mainExecutor = new ActorExecutor(0, 100, 0L, null, null);
		if (hostName == null) {
			try {
				hostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				System.err.println("could not get host");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Send.
	 *
	 * @param <V>
	 *            the value type
	 * @param message
	 *            the message
	 * @return the future
	 */
//	public abstract <V> Message<V> send(Runnable message);
//
//	public abstract <V> Message<V> send(Callable<V> message);
//
//	public abstract <T> T await(Supplier<Boolean> s, Callable<T> message);
//
//	public abstract <T> T await(Future<?> s, Callable<T> message);
//
//	public abstract <T> T await(Supplier<Boolean> s, Runnable message);
//
//	public abstract <T> T await(Future<?> s, Runnable message);
//
//	public abstract <T> T awaitRep(Supplier<Boolean> repCondition, Runnable before, Runnable after, Callable<T> end,
//			Supplier<Future<?>> s);
//
//	public abstract <T> T awaitRep(Supplier<Boolean> repCondition, Supplier<Boolean> s, Runnable before, Runnable after,
//			Callable<T> end);

	@Override
	public <T> T await(URI futureID, Runnable message) {
		if (remoteUncompletedFutures.containsKey(futureID))
			return await(remoteUncompletedFutures.get(futureID), message);
		CompletableFuture<T> toAwait = new CompletableFuture<T>();
		remoteUncompletedFutures.put(futureID, toAwait);
		return await(toAwait, message);
	}

	@Override
	public <T> T await(URI futureID, Callable<T> message) {
		if (remoteUncompletedFutures.containsKey(futureID))
			return await(remoteUncompletedFutures.get(futureID), message);
		CompletableFuture<T> toAwait = new CompletableFuture<T>();
		remoteUncompletedFutures.put(futureID, toAwait);
		return await(toAwait, message);
	}

	@Override
	public <T> T get(URI futureID) {
		if (remoteUncompletedFutures.containsKey(futureID))
			try {
				return (T) remoteUncompletedFutures.get(futureID).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return null;
			}
		CompletableFuture<T> toGet = new CompletableFuture<T>();
		remoteUncompletedFutures.put(futureID, toGet);
		try {
			return (T) toGet.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void releaseAll(ABSFuture<?> m) {

		if (actorMap != null)
			for (ReachableActor actor : actorMap.values()) {
				if (actor.futureContinuations != null)
					if (actor.futureContinuations.containsKey(m.f)) {
						Set<ABSFuture<?>> continuations = actor.futureContinuations.remove(m.f);
						for (ABSFuture<?> continuation : continuations) {
							actor.send(continuation);
						}
					}
				if (actor.conditionContinuations != null)
					for (Supplier<Boolean> condition : actor.conditionContinuations.keySet()) {
						if (condition.get() == true) {
							Set<ABSFuture<?>> continuations = actor.conditionContinuations.remove(condition);
							Iterator<ABSFuture<?>> it = continuations.iterator();
							while(it.hasNext()){
								ABSFuture<?> continuation = it.next();
								actor.send(continuation);
							}
							
						}
					}
			}

		if(lockedOnFutureActors!=null){
			Set<LocalActor> freedActors = lockedOnFutureActors.get(m);
			if(freedActors!=null){
				for (LocalActor localActor : freedActors) {
					localActor.notifyLocked(m);
				}
			}
		}

		
		if (localActorMap != null)
			for (LocalActor actor : localActorMap) {
				if (actor.futureContinuations != null)
					if (actor.futureContinuations.containsKey(m.f)) {
						Set<ABSFuture<?>> continuations = actor.futureContinuations.remove(m.f);
						Iterator<ABSFuture<?>> it = continuations.iterator();
						while(it.hasNext()){
							ABSFuture<?> continuation = it.next();
							actor.send(continuation);
						}
					}

				if (actor.conditionContinuations != null)
					for (Supplier<Boolean> condition : actor.conditionContinuations.keySet()) {
						if (condition.get() == true) {
							Set<ABSFuture<?>> continuations = actor.conditionContinuations.remove(condition);
							for (ABSFuture<?> continuation : continuations) {
								actor.send(continuation);
							}
						}
					}
			}

		if (remotePassedFutures != null) {
			List<AbstractActor> actors = remotePassedFutures.get(m.futureID);
			for (AbstractActor deploymentComponent : actors) {
				ObjectOutputStream destination = machineOutputStreams.get(deploymentComponent.hostName);
				try {
					destination.writeObject(m.futureID);
					try {
						destination.writeObject(m.f.get());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			remotePassedFutures.remove(m.futureID);
		}
	}

	class ActorExecutor extends ThreadPoolExecutor {

		public ActorExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);

		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			if (t == null && r instanceof Future<?>) {
				try {
					Future<?> future = (Future<?>) r;
					if (future.isDone()) {
						future.get();
					}
				} catch (CancellationException ce) {
					t = ce;
				} catch (ExecutionException ee) {
					t = ee.getCause();
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt(); // ignore/reset
				}
			}
			if (t != null) {
				System.out.println(t);
			}
		}

	}

	public int compareTo(AbstractActor o) {
		return this.hashCode() - o.hashCode();
	}
}
