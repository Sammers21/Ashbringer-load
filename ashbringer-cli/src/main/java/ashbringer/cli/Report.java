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
