package ashbringer;


import io.vertx.core.Vertx;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {


        Vertx vertx = Vertx.vertx();
        HttpScheduler httpScheduler = HttpScheduler.create(new SchedulerOptions()
                .setThreadCount(5)
                .setMaxSessions(50_000)
                .setVertx(vertx)
                .setHost(args[0])
                .setPort(Integer.parseInt(args[1]))
        );

        httpScheduler.start();
        while (true) {
            Thread.sleep(1000);
            System.out.println(httpScheduler.report());
        }
    }
}
