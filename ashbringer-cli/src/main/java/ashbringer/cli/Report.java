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


import java.util.Objects;

public class Report {
    long totalReq;
    long lastSecondReq;
    long sessionStartTime;
    long sessions;
    int nThreads;
    String host;
    String path;
    int port;


    public Report(long totalReq, long lastSecondReq, long sessionStartTime, long sessions, int nThreads, String host,
                  String path, int port) {
        this.totalReq = totalReq;
        this.lastSecondReq = lastSecondReq;
        this.sessionStartTime = sessionStartTime;
        this.sessions = sessions;
        this.nThreads = nThreads;
        this.host = host;
        this.path = path;
        this.port = port;
    }

    double rps() {
        Objects.requireNonNull(sessionStartTime, "start time must not be null");
        return totalReq / ((System.currentTimeMillis() - sessionStartTime) / 1000);
    }

    @Override
    public String toString() {
        return "Report:" + "\n" +
                "\tRPS=" + rps() + "\n" +
                "\tlastSecondReq=" + lastSecondReq + "\n" +
                "\ttotalReq=" + totalReq + "\n" +
                "\tsessionStartTime=" + sessionStartTime + "\n" +
                "\tsessions=" + sessions + "\n" +
                "\tnThreads=" + nThreads + "\n" +
                "\thost='" + host + '\'' + "\n" +
                "\tpath='" + path + '\'' + "\n" +
                "\tport=" + port + "\n";

    }
}
