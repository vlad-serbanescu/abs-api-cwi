package abs.api.cwi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static abs.api.cwi.ABSTask.emptyTask;


public class ABSFuture<V> {
    private V value = null;
    private ABSFuture<V> target = null;
    private boolean completed = false;
    private Set<Actor> awaitingActors = ConcurrentHashMap.newKeySet();

    public static <T> ABSFuture<T> done(T value) {
        return new CompletedABSFuture<>(value);
    }

    public static ABSFuture<Void> done() {
        return new CompletedVoidFuture();
    }

    /**
     * This convenience method converts a collection of futures into one future, which is done when
     * all those futures are done. Then the internal done of this future is a list containing all
     * results of the input future collection. It is not possible to complete or cancel such a
     * sequenced future.
     * All input futures must contain the same type of result.
     */
    public static <R> ABSFuture<List<R>> sequence(Collection<ABSFuture<R>> futures) {
        if (futures.isEmpty())
            return done(new ArrayList<>());
        return new SequencedABSFuture<>(futures);
    }

    void awaiting(Actor actor){
        if (target == null) {
            // in the meantime another thread may set the target, so below code is not "else"
            awaitingActors.add(actor);
        }
        if (target != null) {
            target.awaiting(actor);
        }
    }

    void forward(ABSFuture<V> target) {
        assert this.target == null;
        this.target = target;
        // First register as dependant then check for completion.
        // This might lead to double notification in some corner cases but doesn't miss any
        awaitingActors.forEach(target::awaiting);
        if (target.isDone()) {
            notifyDependant();
        }
    }

    void complete(V value) {
        assert (!this.completed);
        assert (this.target == null);
        this.value = value;
        this.completed = true;
        notifyDependant();
    }

    protected void notifyDependant() {
        awaitingActors.forEach(localActor -> localActor.send(emptyTask));
    }

    public boolean isDone() {
        // If in the middle of running this method, a target is added such that I may actually be done, the
        // following code returns not done, but that shouldn't be a problem because the next round will be fine.
        // Though, we should make sure that there will a "next round" and that is taken care of in LocalActor takeOrDie method.
        return (target == null) ? this.completed : target.isDone();
    }

    V getOrNull() {
        return (target == null) ? this.value : target.getOrNull();
    }
}

class CompletedABSFuture<T> extends ABSFuture<T> {
    CompletedABSFuture(T value) {
        this.complete(value);
    }
}

class CompletedVoidFuture extends ABSFuture<Void> {
    @Override public boolean isDone() { return true; }
    @Override public Void getOrNull() { return null; }
}

/**
 * A sequenced future behaves like an actor in the sense that it should be notified by the futures it is awaiting.
 *
 * @param <R>
 */
class SequencedABSFuture<R> extends ABSFuture<List<R>> implements Actor {
    private final Collection<ABSFuture<R>> futures;
    private boolean completed = false;

    SequencedABSFuture(Collection<ABSFuture<R>> futures) {
        this.futures = futures;
    }

    @Override
    void awaiting(Actor actor){
        super.awaiting(actor);
        this.futures.forEach(future -> future.awaiting(this));
        this.send(null);
    }

    @Override
    public boolean isDone() {
        return completed;
    }

    @Override
    public List<R> getOrNull() {
        // no need to calculate `completed` because it is always done before calling this method
        if (completed) {
            return futures.stream().map(ABSFuture::getOrNull).collect(Collectors.toList());
        }
        else
            return null;
    }

    @Override
    public <V> ABSFuture<V> send(Callable<ABSFuture<V>> message) {
        if (!completed)
            completed = futures.stream().allMatch(ABSFuture::isDone);
        if (completed)
            notifyDependant();
        return null;
    }

    @Override
    void complete(List<R> value) {
        throw new UnsupportedOperationException("Cannot complete a sequenced future.");
    }

    @Override
    void forward(ABSFuture<List<R>> dummy) {
        throw new UnsupportedOperationException("Cannot forward a sequenced future.");
    }

    @Override
    public <V> ABSFuture<V> spawn(Guard guard, Callable<ABSFuture<V>> message) {
        return null;
    }

    @Override
    public <T, V> ABSFuture<T> getSpawn(ABSFuture<V> f, CallableGet<T, V> message, int priority, boolean strict) {
        return null;
    }
}