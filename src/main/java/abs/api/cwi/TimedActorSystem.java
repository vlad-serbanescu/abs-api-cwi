package abs.api.cwi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TimedActorSystem extends ActorSystem {

    private static AtomicInteger symbolicTime = new AtomicInteger(0);

    private static AtomicInteger runningActors = new AtomicInteger(0);

    private static ConcurrentSkipListMap<Integer, List<Actor>> awaitingDurations = new ConcurrentSkipListMap<>();


    static int now() {
        return symbolicTime.incrementAndGet();
    }

    static void done() {
        if (runningActors.decrementAndGet() == 0) {
            //get the smallest value to advance time
            SortedSet<Integer> keys = awaitingDurations.keySet();
            int advance = keys.first();
            List<Actor> toRealease = awaitingDurations.remove(advance);
            keys.remove(advance);

            for (Integer k :
                    keys) {
                Integer newK = k - advance;
                List<Actor> actors = awaitingDurations.remove(k);
                awaitingDurations.put(newK, actors);
            }

            advanceTime(advance);

            for (Actor a :
                    toRealease) {
                a.send(ABSTask.emptyTask);
            }
        }

    }

    static void addDuration(Integer max, Actor a){
                if(awaitingDurations.containsKey(max)){
                    awaitingDurations.get(max).add(a);
                }
                else{
                    List<Actor> al = new ArrayList<>();
                    al.add(a);
                    awaitingDurations.put(max,al);
                }
    }

    static void advanceTime(int x) {
        symbolicTime.addAndGet(x);
    }
}
