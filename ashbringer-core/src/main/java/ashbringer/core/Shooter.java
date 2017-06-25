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
}

