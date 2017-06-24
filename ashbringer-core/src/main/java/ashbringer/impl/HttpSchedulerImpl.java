package ashbringer.impl;

import ashbringer.HttpScheduler;
import ashbringer.SchedulerOptions;

import java.util.concurrent.atomic.AtomicInteger;

public class HttpSchedulerImpl implements HttpScheduler {


    public SchedulerOptions options;

    // Current count of async http sessions
    AtomicInteger currentSessions;

    // Max count of async http sessions
    AtomicInteger maxCountOfSessions;

    public HttpSchedulerImpl(SchedulerOptions options) {
        this.options = options;
        currentSessions = new AtomicInteger(0);
        maxCountOfSessions = new AtomicInteger(options.maxSessions);
    }

    public HttpSchedulerImpl() {
        this(new SchedulerOptions());
    }

    public void start() {

    }

    public void stop() {

    }

}
