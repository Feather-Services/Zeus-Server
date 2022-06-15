package fr.featherservices.zeus.scheduler;

import fr.featherservices.zeus.ZeusServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Tick {

    private int tickingInterval;
    private AtomicLong tick = new AtomicLong(0);

    private List<Thread> threads = new ArrayList<>();
    private Queue<ZeusSchedulerTask> asyncTasksQueue = new ConcurrentLinkedQueue<>();

    public Tick(ZeusServer instance) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tickingInterval = (int) Math.round(1000.0 / ZeusServer.getInstance().getServerProperties().getTicksPerSecond());

                for (int i = 0; i < 4; i++) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (instance.isRunning()) {
                                ZeusSchedulerTask task = asyncTasksQueue.poll();
                                if (task == null) {
                                    try {
                                        TimeUnit.NANOSECONDS.sleep(10000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    ZeusTask zeusTask = task.getTask();
                                    try {
                                        zeusTask.run();
                                    } catch (Throwable e) {
                                        System.err.println("Task " + task.getTaskId() + " threw an exception: " + e.getLocalizedMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                    thread.start();
                    threads.add(thread);
                }

                while (instance.isRunning()) {
                    long start = System.currentTimeMillis();
                    tick.incrementAndGet();
                    // Check all servers

                    ZeusScheduler.CurrentSchedulerTask tasks = instance.geScheduler().collectTasks(getCurrentTick());
                    if (tasks != null) {
                        asyncTasksQueue.addAll(tasks.getAsyncTasks());

                        tasks.getSyncedTasks().forEach(task -> {
                            ZeusTask zeusTask = task.getTask();
                            try {
                                zeusTask.run();
                            } catch (Throwable e) {
                                System.err.println("Task " + task.getTaskId() + " threw an exception: " + e.getLocalizedMessage());
                                e.printStackTrace();
                            }
                        });
                    }

                    long end = System.currentTimeMillis();
                    try {
                        TimeUnit.MILLISECONDS.sleep(tickingInterval - (end - start));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public long getCurrentTick() {
        return tick.get();
    }

    @SuppressWarnings("deprecation")
    public void waitAndKillThreads(long waitTime) {
        long end = System.currentTimeMillis() + waitTime;
        for (Thread thread : threads) {
            try {
                thread.join(Math.max(end - System.currentTimeMillis(), 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (thread.isAlive()) {
                thread.stop();
            }
        }
    }

}
