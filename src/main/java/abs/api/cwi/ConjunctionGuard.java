package abs.api.cwi;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class ConjunctionGuard extends Guard {
	private Guard left, right;

	public ConjunctionGuard(Guard left, Guard right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	boolean evaluate() {
		return left.evaluate() && right.evaluate();
	}

	@Override
	void addFuture(Actor a) {
		left.addFuture(a);
		right.addFuture(a);
	}
	
	public static Guard and(Object... guards){
		if (guards.length == 0) return null;
		Guard g = Guard.convert(guards[0]);
		for (int i=1; i<guards.length; i++) {
			g = new ConjunctionGuard(g, Guard.convert(guards[i]));
		}		
		return g;
	}
}
