package abs.api.cwi;

@FunctionalInterface
public interface CallableGet<T, V> {
	T run(V futValue);
}