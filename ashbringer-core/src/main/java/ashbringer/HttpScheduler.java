package ashbringer;

import ashbringer.impl.HttpSchedulerImpl;

public interface HttpScheduler {

    static HttpScheduler create() {
        return new HttpSchedulerImpl();
    }

    static HttpScheduler create(SchedulerOptions options) {
        return new HttpSchedulerImpl(options);
    }

    void start();

    long start(long req);

    void stop();

    String report();
}
