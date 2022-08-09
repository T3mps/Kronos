package net.acidfrog.kronos.inferno;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.acidfrog.kronos.scribe.Logger;
import net.acidfrog.kronos.scribe.LoggerFactory;

public final class Scheduler implements Closeable {
    private static final Logger LOGGER = LoggerFactory.get(Scheduler.class);
    
    private final int timeoutSeconds;
    private final Map<Runnable, Task> taskMap;
    private final List<Task> mainTasks;
    private final ExecutorService mainExecutor;
    private final ScheduledExecutorService updateExecutor;
    private final ForkJoinPool workStealExecutor;
    private final StampedLock scheduleLock;
    private final ReentrantLock updateLock;
    private ScheduledFuture<?> scheduledupdates;
    private int ups;
    private TimeStamp timeStamp;

    protected Scheduler(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        this.taskMap = new HashMap<Runnable, Task>();
        this.mainTasks = new ArrayList<Task>();
        var threadFactory = new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                SchedulerThread schedulerThread = new SchedulerThread(r);
                LOGGER.debug("New schedulerThread: " + schedulerThread.getName());
                return schedulerThread;
            }
        };
        
        this.mainExecutor = Executors.newSingleThreadExecutor(threadFactory);
        this.updateExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        int nThreads = Runtime.getRuntime().availableProcessors();
        this.workStealExecutor = (ForkJoinPool) Executors.newWorkStealingPool(nThreads);
        LOGGER.debug("Parallel executor created with max " + nThreads + " thread count");
        this.scheduleLock = new StampedLock();
        this.updateLock = new ReentrantLock();
        this.ups = 0;
        this.timeStamp = new TimeStamp(System.nanoTime(), 1);
    }

    private static TimeStamp computeUpdateTime(TimeStamp currentupdateTime) {
        long prevTime = currentupdateTime.time;
        long currentTime = System.nanoTime();
        return new TimeStamp(currentTime, currentTime - prevTime);
    }

    public Runnable schedule(Runnable system) {
        long stamp = scheduleLock.writeLock();

        try {
            taskMap.computeIfAbsent(system, sys -> {
                Single single = new Single(sys);
                mainTasks.add(single);
                return single;
            });
            
            LOGGER.debug("Scheduled a new system | slot[" + mainTasks.size() + "]");
            return system;
        } finally {
            scheduleLock.unlockWrite(stamp);
        }
    }

    public Runnable[] parallelSchedule(Runnable... systems) {
        long stamp = scheduleLock.writeLock();

        try {
            switch (systems.length) {
                case 0: return systems;
                case 1: schedule(systems[0]);
                default:
                    Cluster cluster = new Cluster(systems);
                    mainTasks.add(cluster);
                    taskMap.putAll(cluster.taskMap);
                    LOGGER.debug("Scheduled new parallel systems | slot[" + mainTasks.size() + "]");
            }
            
            return systems;
        } finally {
            scheduleLock.unlockWrite(stamp);
        }
    }

    public void forkAndJoin(Runnable subsystem) {
        Thread currentThread = Thread.currentThread();

        if (!(currentThread instanceof SchedulerThread || currentThread instanceof ForkJoinWorkerThread)) {
            throw new IllegalCallerException("Cannot invoke forkAndJoin() from outside other subsystems.");
        }

        try {
            workStealExecutor.invoke(new RecursiveAction() {

                @Override
                protected void compute() {
                    subsystem.run();
                }
            });
        } catch (RuntimeException e) {
            LOGGER.error("Fork-and-join failed", e);
        }
    }

    public void forkAndJoin(Runnable... subsystems) {
        if (!(Thread.currentThread() instanceof ForkJoinWorkerThread)) {
            throw new IllegalCallerException("Cannot invoke forkAndJoin() from outside other subsystems.");
        }

        ForkJoinTask.invokeAll(Arrays.stream(subsystems).map(system -> new RecursiveAction() {

            @Override
            protected void compute() {
                system.run();
            }
        }).toArray(ForkJoinTask[]::new));
    }

    public void suspend(Runnable system) {
        Single singleTask = (Single) taskMap.get(system);
        
        if (singleTask == null) {
            return;
        }
        singleTask.setEnabled(false);
    }

    public void resume(Runnable system) {
        Single singleTask = (Single) taskMap.get(system);

        if (singleTask == null) {
            return;
        }
        singleTask.setEnabled(true);
    }

    public void update() {
        updateLock.lock();

        try {
            timeStamp = computeUpdateTime(timeStamp);
            var tasks = mainExecutor.invokeAll(mainTasks);
            tasks.get(0).get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error("Error while executing main tasks", e);
        } finally {
            updateLock.unlock();
        }
    }

    public void update(int ups) {
        updateLock.lock();

        try {
            if (scheduledupdates != null && ups != this.ups) {
                try {
                    scheduledupdates.cancel(false);
                    scheduledupdates.get(timeoutSeconds, TimeUnit.SECONDS);
                } catch (Exception ignored) {
                }

                scheduledupdates = null;
            }

            this.ups = ups;
            if (this.ups == 0) {
                return;
            }

            scheduledupdates = updateExecutor.scheduleAtFixedRate(this::update, 0, 1000 / this.ups, TimeUnit.MILLISECONDS);
        } finally {
            updateLock.unlock();
        }
    }

    public double getDeltaTime() {
        return timeStamp.deltaTime / 1_000_000_000d;
    }

    @Override
    public void close() {
        updateExecutor.shutdown();
        mainExecutor.shutdown();
        workStealExecutor.shutdown();

        try {
            boolean terminated = mainExecutor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS) &&
                                 workStealExecutor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS) &&
                                 updateExecutor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);

            if (!terminated) {
                LOGGER.warn("Executor did not terminate within " + timeoutSeconds + " seconds");
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error while shutting down system scheduler", e);
        } finally {
            scheduledupdates = null;
        }
    }

    private sealed interface Task extends Callable<Void> permits Single, Cluster {
    }

    record TimeStamp(long time, long deltaTime) {
    }

    private static final class SchedulerThread extends Thread {

        private static final AtomicInteger counter = new AtomicInteger(0);

        public SchedulerThread(Runnable runnable) {
            super(runnable, new StringBuilder().append("Scheduler[").append(counter.getAndIncrement()).append("]").toString());
        }
    }

    private final class Single implements Task {

        private final Runnable system;
        private final AtomicBoolean enabled = new AtomicBoolean(true);

        public Single(Runnable system) {
            this.system = system;
        }

        public Runnable getSystem() {
            return system;
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        @Override
        public Void call() {
            if (isEnabled()) {
                forkAndJoin(system);
            }
            return null;
        }

        private void directRun() {
            if (isEnabled()) {
                system.run();
            }
        }
    }

    private final class Cluster implements Task {

        private final List<Single> tasks;
        private final Map<Runnable, Single> taskMap;

        private Cluster(Runnable[] systems) {
            this.tasks = Arrays.stream(systems).map(Single::new).toList();
            this.taskMap = tasks.stream().collect(Collectors.toMap(Single::getSystem, Function.identity()));
        }

        @Override
        public Void call() {
            forkAndJoin(() -> ForkJoinTask.invokeAll(tasks.stream().map(single -> new RecursiveAction() {

                @Override
                protected void compute() {
                    single.directRun();
                }
            }).toArray(ForkJoinTask[]::new)));
            return null;
        }
    }
}
