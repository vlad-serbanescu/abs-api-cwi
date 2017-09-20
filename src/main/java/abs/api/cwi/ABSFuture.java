package abs.api.cwi;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static abs.api.cwi.ABSTask.emptyTask;


public class ABSFuture<V> implements Future<V> {
    private boolean completed = false;
    private V value = null;
    private Set<Actor> awaitingActors = new ConcurrentSkipListSet<>();
    private Set<ABSFuture> awaitingFutures = ConcurrentHashMap.newKeySet();

    public static <T> ABSFuture<T> of(T value) {
        return new CompletedABSFuture<>(value);
    }

    public static ABSFuture<Void> completedVoidFuture() {
        return new CompletedVoidFuture();
    }

    /**
     * This convenience method converts a collection of futures into one future, which is done when
     * all those futures are done. Then the internal value of this future is a list containing all
     * results of the input future collection. It is not possible to complete or cancel such a
     * sequenced future.
     * All input futures must contain the same type of result.
     */
    public static <R> ABSFuture<List<R>> sequence(Collection<ABSFuture<R>> futures) {
        ABSFuture<List<R>> dependentFuture = new SequencedABSFuture<>(futures);
        // First register as dependant then check for completion.
        // This might lead to double notification in some corner cases but doesn't miss any
        futures.forEach(fut -> fut.awaitingFutures.add(dependentFuture));
        if (dependentFuture.isDone()) {
            dependentFuture.notifyDependants();
        }
        return dependentFuture;
    }

    void awaiting(Actor actor){
        awaitingActors.add(actor);
    }

    void forward(ABSFuture<V> target) {
        // First register as dependant then check for completion.
        // This might lead to double notification in some corner cases but doesn't miss any
        target.awaitingFutures.add(this);
        if (isDone()) {
            notifyDependants();
        }
    }

    void complete(V value) {
        if (! this.completed) {
            this.value = value;
            this.completed = true;
        }
        notifyDependants();
    }

    private void notifyDependants() {
        awaitingActors.forEach(localActor -> localActor.send(emptyTask));
        awaitingFutures.forEach(ABSFuture::notifyDependants);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return this.completed;
    }

    public V getOrNull() {
        return this.value;
    }

    @Override
    public final V get() throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("Cannot get the value of a future directly. Use provided methods in the Actor interface to install a continuation.");
    }

    @Override
    public final V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException("Cannot get the value of a future directly. Use provided methods in the Actor interface to install a continuation.");
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

class SequencedABSFuture<R> extends ABSFuture<List<R>> {
    private final Collection<ABSFuture<R>> futures;

    SequencedABSFuture(Collection<ABSFuture<R>> futures) {
        this.futures = futures;
    }

    @Override
    void complete(List<R> value) {
        throw new UnsupportedOperationException("Cannot complete a sequenced future.");
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException("Cannot cancel a sequenced future.");
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return futures.stream().allMatch(ABSFuture::isDone);
    }

    @Override
    public List<R> getOrNull() {
        if (isDone())
            return futures.stream().map(ABSFuture::getOrNull).collect(Collectors.toList());
        else
            return null;
    }
}