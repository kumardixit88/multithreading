package com.project.multithreading;

import java.util.*;
import java.util.concurrent.*;



public class TaskExecutorService implements Main.TaskExecutor {
    private final ExecutorService executorService ;
    private final Map<UUID, Queue<Callable<?>>> taskGroups;

    public TaskExecutorService() {
        this.executorService = Executors.newFixedThreadPool(10);
        this.taskGroups = new ConcurrentHashMap<>();
    }

    @Override
    public <T> Future<T> submitTask(Task<T> task) {
        UUID groupUUID = task.taskGroup().groupUUID();
        UUID uuid = task.taskGroup().groupUUID();
        if(!taskGroups.containsKey(uuid)){
            taskGroups.put(uuid,new LinkedList<>());
        }



        Future<T> future = null;
        synchronized (taskGroups.get(groupUUID)) {
                taskGroups.get(groupUUID).offer(task.taskAction());
                if (!taskGroups.get(groupUUID).isEmpty()) {
                    Callable callableTask =  taskGroups.get(groupUUID).poll();
                    System.out.println("executing task : "+callableTask);
                    assert callableTask != null;
                    future = executorService.submit(callableTask);
                }

        }
        return future;
    }


    public void shutdown() {
        executorService.shutdown();
    }


}
