package ashbringer.core;

/**
 * Each shooter must implement this interface
 */
public interface Shooter {
    /**
     * Trigger target shooting
     *
     * @param shootComplete callback, which will be called when target is shooted
     */
    void shoot(ShootComplete shootComplete);

    /**
     * Trigger target shooting and we have to shoot target several times during the session
     *
     * @param times                count of times we should shoot target during the one session.
     *                             if times=0 that means we should should target infitite times
     *                             otherwise {@code times} is positive
     * @param oneShootComplete     callback called when we shoot target during the session one time
     * @param shootSessionComplete callback called when we shoot target {@code times} times and we will not shoot
     *                             target anymore
     */
    void startShootConnection(int times,
                              ShootComplete oneShootComplete,
                              ShootComplete shootSessionComplete);
}

