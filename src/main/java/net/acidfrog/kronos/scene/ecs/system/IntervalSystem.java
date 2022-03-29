package net.acidfrog.kronos.scene.ecs.system;

public abstract class IntervalSystem extends AbstractEntitySystem {

    private float interval;
    private float accumulator;

    public IntervalSystem(float interval) {
        this(interval, 0);
    }

    public IntervalSystem(float interval, int priority) {
        super(priority);
        this.interval = interval;
        this.accumulator = 0f;
    }

    @Override
    public void update(float dt) {
        accumulator += dt;

        if (accumulator >= interval) {
            accumulator -= interval;
            intervalUpdate();
        }
    }

    protected abstract void intervalUpdate();

    public float getInterval() {
        return interval;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }
    
}
