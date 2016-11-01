package abs.api.cwi;

public abstract class SyncRunnable implements Runnable {

	@SuppressWarnings("rawtypes")
	protected ABSFutureTask future;
	
	/**
	 * @param future the future to set
	 */
	public void setFuture(@SuppressWarnings("rawtypes") ABSFutureTask future) {
		this.future = future;
	}
	
	/**
	 * @return the future
	 */
	@SuppressWarnings("rawtypes")
	public ABSFutureTask getFuture() {
		return future;
	}
}
