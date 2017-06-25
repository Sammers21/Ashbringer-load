package ashbringer.cli;


import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // <time> <host> <port> <max_sessions> <nThreads> <path>

        Integer time = 0;
        String host = "0.0.0.0";
        int port = 80;
        int maxSessions = 2;
        int nThreads = 1;
        String path = "";
        if (args.length >= 1) {
            time = Integer.parseInt(args[0]);
        }

        if (args.length >= 2) {
            host = args[1];
        }
        if (args.length >= 3) {
            port = Integer.parseInt(args[2]);
        }
        if (args.length >= 4) {
            maxSessions = Integer.parseInt(args[3]);
        }
        if (args.length >= 5) {
            nThreads = Integer.parseInt(args[4]);
        }
        if (args.length >= 6) {
            path = args[5];
        }

        ShootSession shootSession = new ShootSession(host, port, path, maxSessions, nThreads);
        shootSession.start();
        if (time == 0) {
            while (true) {
                report(shootSession);
            }
        } else {
            IntStream.range(1, time).forEach(
                    s -> {
                        report(shootSession);
                    }
            );
        }
        shootSession.stop();
    }

    private static void report(ShootSession shootSession) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(shootSession.report());
    }
}
