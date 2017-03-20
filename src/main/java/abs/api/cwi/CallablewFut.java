package abs.api.cwi;

@FunctionalInterface
public interface CallablewFut<T, V> {
	T run(ABSFutureTask<V> future);
}
