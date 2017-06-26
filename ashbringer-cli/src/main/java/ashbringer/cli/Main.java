/*
 * Copyright 2017 Pavel Drankov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        int nThreads = Runtime.getRuntime().availableProcessors();
        String path = "";
        boolean ssl = false;
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
            ssl = args[5].equals("https");
        }

        if (args.length >= 7) {
            path = args[6];
        }

        ShootSession shootSession = new ShootSession(host, port, path, maxSessions, nThreads,ssl);
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
