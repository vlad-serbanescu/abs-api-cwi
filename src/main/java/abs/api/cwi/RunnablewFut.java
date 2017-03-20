package abs.api.cwi;

@FunctionalInterface
public interface RunnablewFut<V> {
	void run(ABSFutureTask<V> future);
}
