package net.acidfrog.kronos.job;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.job.tasks.CloseTask;

public class JobScheduler {
    
    private ExecutorService priorityJobScheduler = Executors.newSingleThreadExecutor();
    private ExecutorService priorityJobPoolExecutor;
    private boolean running = false;

    private PriorityBlockingQueue<Job> priorityQueue;

    public JobScheduler(Integer poolSize, Integer queueSize) {
        this.priorityJobPoolExecutor = Executors.newFixedThreadPool(poolSize);
        this.priorityQueue = new PriorityBlockingQueue<Job>(queueSize, Comparator.comparing(Job::getPriority));
        this.running = true;

        priorityJobScheduler.execute(() -> {
            while (running) {
                try {
                    Job job = priorityQueue.take();

                    if (job.getTask() instanceof CloseTask) break;

                    priorityJobPoolExecutor.execute(job);
                } catch (InterruptedException e) {
                    Logger.instance.logFatal("JobScheduler interrupted");
                    break;
                }
            }

            close();
        });
    }

    public void scheduleJob(Job job) {
        priorityQueue.add(job);
    }

    public void close() {
        this.running = false;
        priorityJobPoolExecutor.shutdown();
        priorityJobScheduler.shutdown();
    }

    public static void main(String[] args) {
        Job job1 = new Job("Job1", JobPriority.LOW, () -> {
            System.out.println("Job1");
        });
        Job job2 = new Job("Job2", JobPriority.MEDIUM, () -> {
            System.out.println("Job2");
        });
        Job job3 = new Job("Job3", JobPriority.HIGH, () -> {
            System.out.println("Job3");
        });
        Job job4 = new Job("Job4", JobPriority.MEDIUM, () -> {
            System.out.println("Job4");
        });
        Job job5 = new Job("Job5", JobPriority.LOW, () -> {
            System.out.println("Job5");
        });
        Job job6 = new Job("Job6", JobPriority.LAST, new CloseTask() {});

        JobScheduler pjs = new JobScheduler(1, 10);
        
        pjs.scheduleJob(job1);
        pjs.scheduleJob(job2);
        pjs.scheduleJob(job3);
        pjs.scheduleJob(job4);
        pjs.scheduleJob(job5);
        pjs.scheduleJob(job6);
    }

}
