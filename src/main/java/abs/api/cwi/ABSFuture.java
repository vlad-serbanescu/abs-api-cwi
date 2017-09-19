package abs.api.cwi;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ABSFuture<V> implements Future<V> {
    private CompletableFuture<V> internal = new CompletableFuture<>();

    ABSFuture() { /* create an uncompleted future */ }

    private ABSFuture(V value) {
        // create a completed future, for the purpose of returning values
        this.complete(value);
    }

    public static <T> ABSFuture<T> of(T value) {
        return new ABSFuture<>(value);
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
        return new ABSFuture<List<R>>(){
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

            public List<R> getOrNull() {
                if (isDone())
                    return futures.stream().map(ABSFuture::getOrNull).collect(Collectors.toList());
                else
                    return null;
            }
        };
    }

    void forward(ABSFuture<V> target) {
        this.internal = target.internal;
    }

    void complete(V value) {
        internal.complete(value);
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
        return internal.isDone();
    }

    public V getOrNull() {
        if (internal.isDone())
            try {
                return internal.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to get the future result.", e);
            }
        else
            return null;
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

class CompletedVoidFuture extends ABSFuture<Void> {
    @Override
    public boolean isDone() {
        return true;
    }

    public Void getOrNull() {
        return null;
    }
}