/*
 * The class that will be extended by ABS classes
 * 
 */
package abs.api.cwi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ActorSystem {
    /**
     * The main executor.
     */
    private static ExecutorService mainExecutor = Executors.newFixedThreadPool(10);

    private static AtomicInteger symbolicTime = new AtomicInteger(0);

    private static AtomicInteger runningActors = new AtomicInteger(0);


    private ActorSystem() {
    }

    static void submit(Runnable task) {
        //runningActors.incrementAndGet();
        mainExecutor.submit(task);
    }

    static void done() {
        if (runningActors.decrementAndGet() == 0)
            advanceTime();
    }

    static void advanceTime() {
        symbolicTime.incrementAndGet();
    }

    public static void shutdown() {
        mainExecutor.shutdown();
    }


}
