package fr.featherservices.zeus.scheduler;

import fr.featherservices.zeus.plugins.ZeusPlugin;

public class ZeusSchedulerTask {

    private int taskId;
    private ZeusPlugin plugin;
    private ZeusTask task;
    private SchedulerTaskType type;
    private long period;

    public ZeusSchedulerTask(ZeusPlugin plugin, ZeusTask task, int taskId, SchedulerTaskType type, long period) {
        this.plugin = plugin;
        this.task = task;
        this.taskId = taskId;
        this.type = type;
        this.period = period;
    }

    public ZeusPlugin getPlugin() {
        return plugin;
    }

    public ZeusTask getTask() {
        return task;
    }

    public int getTaskId() {
        return taskId;
    }

    public SchedulerTaskType getType() {
        return type;
    }

    public long getPeriod() {
        return period;
    }

    public static enum SchedulerTaskType {

        SYNC,
        ASYNC,
        TIMER_SYNC,
        TIMER_ASYNC;

    }

}
