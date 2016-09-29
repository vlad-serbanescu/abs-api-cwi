package abs.api.cwi;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public abstract class Guard {
	abstract boolean evaluate();
	abstract void addFuture(Actor a);
	static public Guard convert(Object o) {
		if (o instanceof Supplier) {
			return new PureExpressionGuard((Supplier<Boolean>)o);
		} else if (o instanceof ABSFutureTask) {
			return new FutureGuard((ABSFutureTask)o);
		} else if (o instanceof Guard) {
			return (Guard) o;
		} else{
			System.out.println("Cannot make guard");
			throw new IllegalArgumentException("Cannot make a guard.");
		}
	}
}
