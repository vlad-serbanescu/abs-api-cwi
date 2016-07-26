package abs.api.cwi;

public class FutureGuard extends Guard {

	private ABSFutureTask<?> future;

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
		DeploymentComponent.lockActorOnFuture(a, future);
	}
}
