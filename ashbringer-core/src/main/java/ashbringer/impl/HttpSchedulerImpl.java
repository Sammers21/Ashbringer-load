package ashbringer.impl;

import ashbringer.HttpScheduler;
import ashbringer.SchedulerOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HttpSchedulerImpl implements HttpScheduler {

    WebClient client;

    public SchedulerOptions options;

    // Current count of async http sessions
    AtomicInteger currentSessions;

    // Max count of async http sessions
    AtomicInteger maxCountOfSessions;

    AtomicLong reqTotal = new AtomicLong(0);

    // main thread pull
    ExecutorService threadPull;

    //scheduller pull
    ExecutorService scheduler = Executors.newSingleThreadExecutor();

    AtomicBoolean stop = new AtomicBoolean(false);

    AtomicLong startTime;
    AtomicLong stopTime;
    AtomicLong limitReq;
    CountDownLatch cdl = new CountDownLatch(1);
    CountDownLatch sessionscdl = new CountDownLatch(1);

    public HttpSchedulerImpl(SchedulerOptions options) {
        this.options = options;
        currentSessions = new AtomicInteger(0);
        maxCountOfSessions = new AtomicInteger(options.maxSessions);
        threadPull = Executors.newFixedThreadPool(options.threadCount);
        client = WebClient.create(options.vertx);
    }

    public HttpSchedulerImpl() {
        this(new SchedulerOptions());
    }

    @Override
    public void start() {
        startTime = new AtomicLong(System.currentTimeMillis());
        scheduler.execute(schedule());
    }

    @Override
    public long start(long req) {
        startTime = new AtomicLong(System.currentTimeMillis());
        scheduler.execute(schedule());
        limitReq = new AtomicLong(req);
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stop();
        return System.currentTimeMillis() - startTime.get();
    }

    public Runnable task() {
        return () ->
                client
                        .get(options.port, options.host, "/")
                        .send(ar -> {
                            if (ar.succeeded()) {
                                reqTotal.incrementAndGet();
                                currentSessions.decrementAndGet();
                                scheduler.execute(schedule());
                            } else {
                                System.out.println("something wrong");
                            }
                        });
    }

    public Runnable schedule() {
        return () -> {
            if (limitReq != null && limitReq.get() <= reqTotal.get())
                cdl.countDown();

            if (!stop.get()) {
                while (currentSessions.incrementAndGet() <= maxCountOfSessions.get() && !stop.get()) {
                    threadPull.execute(task());
                }
                currentSessions.decrementAndGet();
            }

            if (currentSessions.get() == 0 && stop.get()) {
                sessionscdl.countDown();
            }
        };
    }


    public void stop() {
        stop.set(true);

        try {
            sessionscdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scheduler.shutdownNow();
        threadPull.shutdownNow();

        stopTime = new AtomicLong(System.currentTimeMillis());
    }

    @Override
    public String report() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Time spent ");

        double timespent = stopTime == null ?
                (System.currentTimeMillis() - startTime.get()) / 1000
                : (stopTime.get() - startTime.get()) / 1000;

        stringBuilder.append(timespent);
        stringBuilder.append(" seconds\n");
        stringBuilder
                .append("Total requests ").append(reqTotal.get()).append("\n")
                .append("RPS ").append(reqTotal.get() / timespent).append("\n");

        stringBuilder.append(options);

        return stringBuilder.toString();
    }

}
