package abs.api.cwi;

public class FutureGuard extends Guard {

	public ABSFutureTask<?> future;

	public FutureGuard(ABSFutureTask<?> future) {
		super();
		this.future = future;
	}

	@Override
	boolean evaluate() {
		return future.isDone();
	}

	@Override
	void addFuture(Actor a) {
		DeploymentComponent.awaitActorOnFuture(a, future);
	}
	
	

	@Override
	boolean hasFuture() {
		return true;
	}

//	void blockOnFuture(Actor a) {
//		DeploymentComponent.blockActorOnFuture(a, future);
//	}
}
