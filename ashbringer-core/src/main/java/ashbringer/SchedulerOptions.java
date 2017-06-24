package ashbringer;

public class SchedulerOptions {

    public String host = "localhost";
    public int port = 8080;
    public int threadCount = 1;
    public int maxSessions = 100;
    public String reportPath = ".";

    public SchedulerOptions setHost(String host) {
        this.host = host;
        return this;
    }

    public SchedulerOptions setPort(int port) {
        this.port = port;
        return this;
    }

    public SchedulerOptions setThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public SchedulerOptions setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
        return this;
    }

    public SchedulerOptions setReportPath(String reportPath) {
        this.reportPath = reportPath;
        return this;
    }
}
