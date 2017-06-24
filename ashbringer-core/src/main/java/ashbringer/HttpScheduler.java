package ashbringer;

import java.util.concurrent.atomic.AtomicInteger;

public class HttpScheduler {

    /**
     * Current count of async http sessions
     */
    AtomicInteger currentSessions = new AtomicInteger(0);


    public void start(){

    }
}
