package fr.featherservices.zeus.scheduler;

import fr.featherservices.zeus.ZeusServer;
import fr.featherservices.zeus.plugins.ZeusPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ZeusScheduler {

    private AtomicInteger idProvider = new AtomicInteger(0);
    private Map<Long, List<ZeusSchedulerTask>> registeredTasks = new HashMap<>();
    private Map<Integer, ZeusSchedulerTask> tasksById = new HashMap<>();
    private Set<Integer> cancelledTasks = new HashSet<>();

    public ZeusScheduler() {

    }

    protected int nextId() {
        return idProvider.getAndUpdate(id -> id >= Integer.MAX_VALUE ? 0 : id + 1);
    }

    public void cancelTask(int taskId) {
        if (tasksById.containsKey(taskId)) {
            cancelledTasks.add(taskId);
        }
    }

    public void cancelTask(ZeusPlugin plugin) {
        for (ZeusSchedulerTask task : tasksById.values()) {
            if (task.getPlugin().getName().equals(plugin.getName())) {
                cancelledTasks.add(task.getTaskId());
            }
        }
    }

    protected int runTask(int taskId, ZeusPlugin plugin, ZeusTask task) {
        return runTaskLater(taskId, plugin, task, 0);
    }

    public int runTask(ZeusPlugin plugin, ZeusTask task) {
        return runTaskLater(plugin, task, 0);
    }

    protected int runTaskLater(int taskId, ZeusPlugin plugin, ZeusTask task, long delay) {
        ZeusSchedulerTask st = new ZeusSchedulerTask(plugin, task, taskId, ZeusSchedulerTask.SchedulerTaskType.SYNC, 0);
        if (delay <= 0) {
            delay = 1;
        }
        long tick = ZeusServer.getInstance().getHeartBeat().getCurrentTick() + delay;
        tasksById.put(taskId, st);
        List<ZeusSchedulerTask> list = registeredTasks.get(tick);
        if (list == null) {
            list = new ArrayList<>();
            registeredTasks.put(tick, list);
        }
        list.add(st);
        return taskId;
    }

    public int runTaskLater(ZeusPlugin plugin, ZeusTask task, long delay) {
        return runTaskLater(nextId(), plugin, task, delay);
    }

    protected int runTaskAsync(int taskId, ZeusPlugin plugin, ZeusTask task) {
        return runTaskLaterAsync(taskId, plugin, task, 0);
    }

    public int runTaskAsync(ZeusPlugin plugin, ZeusTask task) {
        return runTaskLaterAsync(plugin, task, 0);
    }

    protected int runTaskLaterAsync(int taskId, ZeusPlugin plugin, ZeusTask task, long delay) {
        ZeusSchedulerTask st = new ZeusSchedulerTask(plugin, task, taskId, ZeusSchedulerTask.SchedulerTaskType.ASYNC, 0);
        if (delay <= 0) {
            delay = 1;
        }
        long tick = ZeusServer.getInstance().getHeartBeat().getCurrentTick() + delay;
        tasksById.put(taskId, st);
        List<ZeusSchedulerTask> list = registeredTasks.get(tick);
        if (list == null) {
            list = new ArrayList<>();
            registeredTasks.put(tick, list);
        }
        list.add(st);
        return taskId;
    }

    public int runTaskLaterAsync(ZeusPlugin plugin, ZeusTask task, long delay) {
        return runTaskLaterAsync(nextId(), plugin, task, delay);
    }

    protected int runTaskTimer(int taskId, ZeusPlugin plugin, ZeusTask task, long delay, long period) {
        ZeusSchedulerTask st = new ZeusSchedulerTask(plugin, task, taskId, ZeusSchedulerTask.SchedulerTaskType.TIMER_SYNC, period);
        if (delay <= 0) {
            delay = 1;
        }
        if (period <= 0) {
            period = 1;
        }
        long tick = ZeusServer.getInstance().getHeartBeat().getCurrentTick() + delay;
        tasksById.put(taskId, st);
        List<ZeusSchedulerTask> list = registeredTasks.get(tick);
        if (list == null) {
            list = new ArrayList<>();
            registeredTasks.put(tick, list);
        }
        list.add(st);
        return taskId;
    }

    public int runTaskTimer(ZeusPlugin plugin, ZeusTask task, long delay, long period) {
        return runTaskTimer(nextId(), plugin, task, delay, period);
    }

    protected int runTaskTimerAsync(int taskId, ZeusPlugin plugin, ZeusTask task, long delay, long period) {
        ZeusSchedulerTask st = new ZeusSchedulerTask(plugin, task, taskId, ZeusSchedulerTask.SchedulerTaskType.TIMER_ASYNC, period);
        if (delay <= 0) {
            delay = 1;
        }
        if (period <= 0) {
            period = 1;
        }
        long tick = ZeusServer.getInstance().getHeartBeat().getCurrentTick() + delay;
        tasksById.put(taskId, st);
        List<ZeusSchedulerTask> list = registeredTasks.get(tick);
        if (list == null) {
            list = new ArrayList<>();
            registeredTasks.put(tick, list);
        }
        list.add(st);
        return taskId;
    }

    public int runTaskTimerAsync(ZeusPlugin plugin, ZeusTask task, long delay, long period) {
        return runTaskTimerAsync(nextId(), plugin, task, delay, period);
    }

    protected CurrentSchedulerTask collectTasks(long currentTick) {
        List<ZeusSchedulerTask> tasks = registeredTasks.remove(currentTick);
        if (tasks == null) {
            return null;
        }

        List<ZeusSchedulerTask> asyncTasks = new LinkedList<>();
        List<ZeusSchedulerTask> syncedTasks = new LinkedList<>();

        for (ZeusSchedulerTask task : tasks) {
            int taskId = task.getTaskId();
            if (cancelledTasks.contains(taskId)) {
                cancelledTasks.remove(taskId);
                continue;
            }

            switch (task.getType()) {
                case ASYNC:
                    asyncTasks.add(task);
                    break;
                case SYNC:
                    syncedTasks.add(task);
                    break;
                case TIMER_ASYNC:
                    asyncTasks.add(task);
                    runTaskTimerAsync(task.getTaskId(), task.getPlugin(), task.getTask(), task.getPeriod(), task.getPeriod());
                    break;
                case TIMER_SYNC:
                    syncedTasks.add(task);
                    runTaskTimer(task.getTaskId(), task.getPlugin(), task.getTask(), task.getPeriod(), task.getPeriod());
                    break;
            }
        }

        return new CurrentSchedulerTask(syncedTasks, asyncTasks);
    }

    public static class CurrentSchedulerTask {

        private List<ZeusSchedulerTask> asyncTasks;
        private List<ZeusSchedulerTask> syncedTasks;

        public CurrentSchedulerTask(List<ZeusSchedulerTask> syncedTasks, List<ZeusSchedulerTask> asyncTasks) {
            this.asyncTasks = asyncTasks;
            this.syncedTasks = syncedTasks;
        }

        public List<ZeusSchedulerTask> getAsyncTasks() {
            return asyncTasks;
        }

        public List<ZeusSchedulerTask> getSyncedTasks() {
            return syncedTasks;
        }

    }

}
