package net.acidfrog.kronos.scene.ecs.system;

/**
 * Abstract implementation of {@link EntitySystem}, which updates in a fixed, interval manner.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public abstract class IntervalSystem extends AbstractEntitySystem {

    /** The interval at which the system updates */
    protected float interval;

    /** Increases by dt every step */
    protected float accumulator;

    /**
     * Creates a new system with the specified interval.
     * 
     * @param interval
     */
    public IntervalSystem(float interval) {
        this(interval, 0);
    }

    /**
     * Creates a new system with the specified interval and priority.
     * 
     * @param interval
     * @param priority
     */
    public IntervalSystem(float interval, int priority) {
        super(priority);
        this.interval = interval;
        this.accumulator = 0f;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void update(float dt) {
        accumulator += dt;

        if (accumulator >= interval) {
            accumulator -= interval;
            intervalUpdate();
        }
    }

    /**
     * Called when the system reaches the interval, during the update.
     */
    protected abstract void intervalUpdate();

    /**
     * @return the interval
     */
    public float getInterval() {
        return interval;
    }

    /**
     * Sets the interval.
     * 
     * @param interval
     */
    public void setInterval(float interval) {
        this.interval = interval;
    }
    
}
