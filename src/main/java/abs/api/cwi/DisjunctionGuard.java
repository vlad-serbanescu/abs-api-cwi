package abs.api.cwi;

public class DisjunctionGuard extends Guard {
	private Guard left, right;

	public DisjunctionGuard(Guard left, Guard right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	boolean evaluate() {
		return left.evaluate() || right.evaluate();
	}

	@Override
	void addFuture(Actor a) {
		left.addFuture(a);
		right.addFuture(a);	
	}

	public static Guard or(Object... guards){
		if (guards.length == 0) return null;
		Guard g = Guard.convert(guards[0]);
		for (int i=1; i<guards.length; i++) {
			g = new DisjunctionGuard(g, Guard.convert(guards[i]));
		}		
		return g;
	}
}
