package com.project.multithreading;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@SpringBootApplication
public class MultithreadingApplication {

	public static void main(String[] args) throws ExecutionException, InterruptedException {

		// Creating  read Groups
		int numOfTask = 10;
		UUID readUUID = UUID.randomUUID();
		TaskGroup readTaskGroup = new TaskGroup(readUUID);
		Future<String> [] readFutures = new Future[numOfTask];

		// Creating  write Groups
		UUID  writeUUID = UUID.randomUUID();
		TaskGroup writeTaskGroup = new TaskGroup(writeUUID);
		Future<String> [] writeFutures = new Future[numOfTask];

		TaskExecutorService taskExecutorService = new TaskExecutorService();
		for(int i =0;i<numOfTask;i++){
			int val = i;
			Callable<String> readAction = () -> {return "Read data : "+ val;};

			Task<String> readTask = new Task<String>(UUID.randomUUID(), readTaskGroup, Main.TaskType.READ, readAction);
			readFutures[i] = taskExecutorService.submitTask(readTask);

			Callable<String> writeAction = () -> {return "Write data : "+ val;};
			Task<String> writeTask = new Task<String>(UUID.randomUUID(), writeTaskGroup, Main.TaskType.WRITE, writeAction);
			writeFutures[i] = taskExecutorService.submitTask(writeTask);
		}


		for(int i =0;i<numOfTask;i++){
			String readResponse  = readFutures[i].get();
			System.out.println(readResponse);

			String writeResponse  = writeFutures[i].get();
			System.out.println(writeResponse);
		}

		taskExecutorService.shutdown();


    }

}
