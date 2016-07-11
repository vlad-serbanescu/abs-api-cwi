package abs.api.cwi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class MachineListener implements Runnable {

	ObjectInputStream execOis;

	public MachineListener(ObjectInputStream ois) {
		execOis = ois;
	}

	public void run() {
		String finalMessage = null;

		do {
			Object input;
			try {
				input = execOis.readObject();

				if (input instanceof String)
					finalMessage = (String) (input);

				if (input instanceof URI) {
					URI target = (URI) input;

					if (target.getUserInfo().equals("abs")) {

						ReachableActor targetActor = DeploymentComponent.actorMap.get(target);

						do {
							input = execOis.readObject();
							if (input instanceof Future<?>) {
								Future<?> notSure = (Future<?>) input;
							} else {
								if (input instanceof Runnable)
									targetActor.send((Runnable) input);
								if (input instanceof Callable<?>)
									targetActor.send((Callable<?>) input);
							}
						} while (input instanceof Future<?>);
					} else {
						URI futureID = (URI) input;

						CompletableFuture expectedResult = (CompletableFuture) DeploymentComponent.remoteUncompletedFutures
								.get(futureID);

						input = execOis.readObject();

						expectedResult.complete(input);
					}

				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (finalMessage == null);
	}

}
