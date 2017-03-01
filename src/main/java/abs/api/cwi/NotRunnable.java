package abs.api.cwi;

@FunctionalInterface
public interface NotRunnable<T, V> {
	T run(ABSFutureTask<V> future);
}
