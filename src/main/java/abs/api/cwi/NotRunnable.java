package abs.api.cwi;

@FunctionalInterface
public interface NotRunnable<T> {
	T run(ABSFutureTask future);
}
