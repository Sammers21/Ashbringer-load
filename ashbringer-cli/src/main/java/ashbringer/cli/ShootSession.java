package ashbringer.cli;


import ashbringer.core.NettyShooter;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

public class ShootSession {

    private final NettyShooter nettyShooter;

    Semaphore semaphore = new Semaphore(1);
    AtomicLong totalReq = new AtomicLong(0);
    AtomicLong lastSecondReq = new AtomicLong(0);
    AtomicLong lastSecondReqRem = new AtomicLong(0);
    AtomicLong sessionStartTime;
    AtomicLong currentSessions = new AtomicLong(0);
    long maxSessions;
    volatile boolean stopFlag = false;
    int nThreads;

    public ShootSession(String host, int port) {
        this(host, port, "");
    }

    public ShootSession(String host, int port, String path) {
        this(host, port, path, 1000, 1);
    }

    public ShootSession(String host, int port, String path, long curSessions) {
        this(host, port, path, curSessions, 1);
    }

    public ShootSession(String host, int port, String path, long maxSessions, int nThreads) {
        this(host, port, path, maxSessions, nThreads, false);
    }

    public ShootSession(String host, int port, String path, long maxSessions, int nThreads, boolean ssl) {
        nettyShooter = new NettyShooter(host, port, path, nThreads, ssl);
        sessionStartTime = new AtomicLong(System.currentTimeMillis());
        this.maxSessions = maxSessions;
        this.nThreads = nThreads;
    }


    void start() {
        new Thread(() -> {
            while (!stopFlag) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (currentSessions.get() < maxSessions && !stopFlag) {
                    currentSessions.incrementAndGet();
                    nettyShooter.shoot(() -> {
                        totalReq.incrementAndGet();
                        lastSecondReq.incrementAndGet();
                        currentSessions.decrementAndGet();
                        semaphore.release();
                    });
                }
            }
        }).start();

        new Thread(() -> {
            while (!stopFlag) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lastSecondReqRem.set(lastSecondReq.get());
                lastSecondReq.set(0);
            }
        }).start();
    }

    void stop() {
        stopFlag = true;
    }

    Report report() {
        return new Report(totalReq.get(),
                lastSecondReqRem.get(),
                sessionStartTime.get(),
                currentSessions.get(),
                nThreads,
                nettyShooter.host,
                nettyShooter.path,
                nettyShooter.port
        );
    }
}
