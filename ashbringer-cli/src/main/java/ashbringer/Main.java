package ashbringer;


import io.vertx.core.Vertx;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {


        Vertx vertx = Vertx.vertx();
        SchedulerOptions options = new SchedulerOptions()
                .setHost(args[0])
                .setPort(Integer.parseInt(args[1]))
                .setVertx(vertx);

        if (args.length >= 3)
            options.setMaxSessions(Integer.parseInt(args[2]));

        if (args.length >= 4)
            options.setThreadCount(Integer.parseInt(args[3]));

        HttpScheduler httpScheduler = HttpScheduler.create(options
        );

        httpScheduler.start();
        while (true) {
            Thread.sleep(1000);
            System.out.println(httpScheduler.report());
        }
    }
}
