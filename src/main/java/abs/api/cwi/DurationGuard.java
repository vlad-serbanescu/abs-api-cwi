package abs.api.cwi;

import java.util.function.Supplier;

public class DurationGuard extends Guard {

    int whenCalled, min, max;

    public DurationGuard(int whenCalled, int min, int max) {
        this.whenCalled = whenCalled;
        this.min = min;
        this.max = max;
    }

    @Override
    boolean evaluate() {
        return (whenCalled+min)>=TimedActorSystem.now();
    }

    @Override
    void addFuture(Actor a) {TimedActorSystem.addDuration(max,a); }

    @Override
    boolean hasFuture() {
        return false;
    }
}
