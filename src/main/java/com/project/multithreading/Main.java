package com.project.multithreading;


import java.util.concurrent.Future;

public class Main {

    public enum TaskType {
        READ,
        WRITE
    }

    public interface TaskExecutor {

        <T> Future<T> submitTask(com.project.multithreading.Task<T> task);
    }
}