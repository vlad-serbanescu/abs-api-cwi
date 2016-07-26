package abs.api.cwi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import javax.net.ServerSocketFactory;

/**
 * The Class ReachableActor. This class describes Actors used in a distributed
 * system. The Actors can either be reachable Actors on this machine. The Actors
 * can be remote actors that are reachable on other machines.
 */
public class ReachableActor extends AbstractActor {

	/** The name. A URI describing the location */
	private URI name;

	protected ConcurrentHashMap<Future<?>, Set<ABSFutureTask<?>>> futureContinuations;
	protected ConcurrentHashMap<Supplier<Boolean>, Set<ABSFutureTask<?>>> conditionContinuations;

	protected ABSFutureTask<?> possibleAwait = null;

	protected boolean completedMessage = true;

	/** The status. Remote or Reachable (local) */
	int status;

	/**
	 * The message queue. This field only exists if the actor is on this machine
	 */
	protected ConcurrentLinkedQueue<ABSFutureTask<?>> messageQueue = null;

	/**
	 * Instantiates a new reachable actor.
	 *
	 * @param name
	 *            the name. determines if IS_REMOTE or IS_REACHABLE
	 */
	public ReachableActor(URI name) {
		super();
		this.name = name;

		if (this.name.getHost().equals(this.hostName))
			status = IS_REACHABLE;
		else
			status = IS_REMOTE;

		if (status == IS_REACHABLE) {
			messageQueue = new ConcurrentLinkedQueue<ABSFutureTask<?>>();

			if (actorMap == null) {
				actorMap = new ConcurrentHashMap<URI, ReachableActor>();
			}

			actorMap.put(name, this);
			if (thisMachine == null) {
				try {
					thisMachine = ServerSocketFactory.getDefault().createServerSocket(1888);
				} catch (IOException e) {
					e.printStackTrace();
				}

				final String checkIP = this.name.getHost();

				new Thread(new MainSocketConnection(thisMachine)).start();
			}
		} else
			remoteUncompletedFutures = new ConcurrentHashMap<URI, Future<?>>();

	}

	protected <V> ABSFutureTask<V> send(ABSFutureTask<V> m) {

		if (this.status == IS_REACHABLE) {
			messageQueue.add(m);
			if (messageQueue.size() == 1) {
				new Thread(new Runnable() {

					public void run() {
						while (messageQueue.isEmpty() == false) {
							ABSFutureTask<?> m;
							m = messageQueue.poll();
							m.runningF.run();
							if (!completedMessage) {
								possibleAwait.setF(m.f);
								completedMessage = true;
							} else {
								m.finished();
								releaseAll(m);
							}
						}
					}
				}).start();
			}
		} else {

			remoteUncompletedFutures.put(m.futureID, m.f);
			if (machineOutputStreams != null && machineOutputStreams.containsKey(name.getHost())) {
				ObjectOutputStream destination = machineOutputStreams.get(name.getHost());
				try {
					destination.writeObject(this.name);
					destination.writeObject(m.futureID);
					//TODO no M
					destination.writeObject(m);

				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {

				try {
					communicationSocket = new Socket(this.name.getHost(), 1888);
					ObjectOutputStream oos = new ObjectOutputStream(communicationSocket.getOutputStream());
					ObjectInputStream ois = new ObjectInputStream(communicationSocket.getInputStream());

					if (machineOutputStreams == null) {
						machineOutputStreams = new ConcurrentHashMap<String, ObjectOutputStream>();
					}

					machineOutputStreams.put(communicationSocket.getInetAddress().getHostAddress(), oos);

					oos.writeObject(this.name);
					oos.writeObject(m.futureID);
					//TOFO no M
					oos.writeObject(m);

					if (machineInputStreams == null) {
						machineInputStreams = new ConcurrentHashMap<String, ObjectInputStream>();
					}

					machineInputStreams.put(communicationSocket.getInetAddress().getHostAddress(), ois);

					new Thread(new MachineListener(ois)).start();

				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return m;
	}

	/**
	 * /** Send Method
	 * 
	 * @param <V>
	 *            the value type
	 * @param the
	 *            message
	 * @return the future
	 * 
	 * 
	 * 
	 */

	@Override
	public <V> ABSFutureTask<V> send(Runnable message) {
		ABSFutureTask<V> m = new ABSFutureTask<V>(message, status, name.getHost());
		return send(m);
	}

	@Override
	public <V> ABSFutureTask<V> send(Callable<V> message) {
		ABSFutureTask<V> m = new ABSFutureTask<V>(message, status, name.getHost());
		return send(m);
	}

	
//	@Override
//	public <T> T await(Supplier<Boolean> s, Callable<T> message) {
//		ABSFutureTask<T> m = new ABSFutureTask<T>(message, status, this.name.getHost());
//		return await(s, m);
//	}
//
//	@Override
//	public <T> T await(Future<?> s, Callable<T> message) {
//		ABSFutureTask<T> m = new ABSFutureTask<T>(message, status, this.name.getHost());
//		return await(s, m);
//	}
//
//	@Override
//	public <T> T await(Supplier<Boolean> s, Runnable message) {
//		ABSFutureTask<T> m = new ABSFutureTask<T>(message, status, this.name.getHost());
//		return await(s, m);
//	}
//
//	@Override
//	public <T> T await(Future<?> s, Runnable message) {
//		ABSFutureTask<T> m = new ABSFutureTask<T>(message, status, this.name.getHost());
//		return await(s, m);
//	}

	private <T> T await(Supplier<Boolean> s, ABSFutureTask<T> m) {
		if (s.get() == true) {
			m.runningF.run();
			try {
				return m.runningF.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			if (conditionContinuations == null)
				conditionContinuations = new ConcurrentHashMap<Supplier<Boolean>, Set<ABSFutureTask<?>>>();

			if (conditionContinuations.containsKey(s))
				conditionContinuations.get(s).add(m);
			else {
				Set<ABSFutureTask<?>> continuations = new HashSet<ABSFutureTask<?>>();
				continuations.add(m);
				conditionContinuations.put(s, continuations);
			}
			possibleAwait = m;
			completedMessage = false;
			return null;
		}
	}

	private <T> T await(Future<?> s, ABSFutureTask<T> m) {
		if (s.isDone()) {
			System.out.println("entering await completion");
			m.runningF.run();
			try {
				return m.runningF.get();
			} catch (InterruptedException e) {

				e.printStackTrace();
				return null;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			if (futureContinuations == null) {
				futureContinuations = new ConcurrentHashMap<Future<?>, Set<ABSFutureTask<?>>>();
			}

			if (futureContinuations.containsKey(s))
				futureContinuations.get(s).add(m);
			else {
				Set<ABSFutureTask<?>> continuations = new HashSet<ABSFutureTask<?>>();
				continuations.add(m);
				futureContinuations.put(s, continuations);
			}
			possibleAwait = m;
			completedMessage = false;
			return null;
		}
	}
	
//	@Override
//	public <T> T awaitRep(Supplier<Boolean> repCondition, Runnable before, Runnable after,
//			Callable<T> end,Supplier<Future<?>> s) {
//		if (s.get().isDone()) {
//			if (repCondition.get() == true) {
//				before.run();
//				after.run();
//				return awaitRep(repCondition,before, after, end,s);
//			} else {
//				try {
//					return end.call();
//				} catch (Exception e) {
//					e.printStackTrace();
//					return null;
//				}
//			}
//
//		} else {
//			Callable<T> loopContinuation = (Callable<T>) (() -> {
//				after.run();
//				if (repCondition.get() == true)
//					return awaitRep(repCondition, before, after, end,s);
//				else
//					return end.call();
//			});
//			before.run();
//			return await(s.get(), loopContinuation);
//		}
//	}
//
//	@Override
//	public <T> T awaitRep(Supplier<Boolean> repCondition, Supplier<Boolean> s, Runnable beforeAwait, Runnable afterAwait,
//			Callable<T> end) {
//		if (s.get() == true) {
//			if (repCondition.get() == true) {
//				beforeAwait.run();
//				afterAwait.run();
//				return awaitRep(repCondition, s, beforeAwait, afterAwait, end);
//			} else {
//				try {
//					return end.call();
//				} catch (Exception e) {
//					e.printStackTrace();
//					return null;
//				}
//			}
//
//		} else {
//			Callable<T> loopContinuation = (Callable<T>) (() -> {
//				afterAwait.run();
//				if (repCondition.get() == true)
//					return awaitRep(repCondition, s, beforeAwait, afterAwait, end);
//				else
//					return end.call();
//			});
//			beforeAwait.run();
//			return await(s, loopContinuation);
//		}
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReachableActor)
			return ((ReachableActor) obj).name.equals(name);
		else if (obj instanceof URI)
			return ((URI) obj).equals(name);
		return super.equals(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public <T> void await(Guard guard, Callable<T> message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void await(Guard guard, Runnable message) {
		// TODO Auto-generated method stub
		
	}
}
