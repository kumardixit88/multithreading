package com.project.multithreading;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

public class TaskExecutorServiceTest {
	private TaskExecutorService taskExecutorService;

	@BeforeEach
	void setUp() {
		taskExecutorService = new TaskExecutorService();
	}

	@AfterEach
	void tearDown() {
		taskExecutorService.shutdown();
	}

	@Test
	void testSubmitSingleTask() throws Exception {
		TaskGroup taskGroup = new TaskGroup(UUID.randomUUID());
		Task<String> task = new Task<>(
				UUID.randomUUID(),
				taskGroup,
				Main.TaskType.READ,
				() -> "Read completed"
		);

		Future<String> future = taskExecutorService.submitTask(task);

		assertNotNull(future);
		assertEquals("Read completed", future.get());
	}

	@Test
	void testSubmitMultipleTasksInSameGroup() throws Exception {
		TaskGroup taskGroup = new TaskGroup(UUID.randomUUID());

		Task<Integer> task1 = new Task<>(
				UUID.randomUUID(),
				taskGroup,
				Main.TaskType.READ,
				() -> 1
		);

		Task<Integer> task2 = new Task<>(
				UUID.randomUUID(),
				taskGroup,
				Main.TaskType.READ,
				() -> 2
		);

		Future<Integer> future1 = taskExecutorService.submitTask(task1);
		Future<Integer> future2 = taskExecutorService.submitTask(task2);

		assertNotNull(future1);
		assertNotNull(future2);

		assertEquals(1, future1.get());
		assertEquals(2, future2.get());
	}

	@Test
	void testTaskExecutionOrderWithinGroup() throws Exception {
		TaskGroup taskGroup = new TaskGroup(UUID.randomUUID());

		StringBuilder log = new StringBuilder();

		Task<Void> task1 = new Task<>(
				UUID.randomUUID(),
				taskGroup,
				Main.TaskType.READ,
				() -> {
					log.append("Task 1 completed;");
					return null;
				}
		);

		Task<Void> task2 = new Task<>(
				UUID.randomUUID(),
				taskGroup,
				Main.TaskType.READ,
				() -> {
					log.append("Task 2 completed;");
					return null;
				}
		);

		taskExecutorService.submitTask(task1);
		taskExecutorService.submitTask(task2);

		Thread.sleep(2000); // Ensure tasks have time to run

		assertEquals("Task 1 completed;Task 2 completed;", log.toString());
	}

	@Test
	void testSubmitTaskToDifferentGroups() throws Exception {
		TaskGroup group1 = new TaskGroup(UUID.randomUUID());
		TaskGroup group2 = new TaskGroup(UUID.randomUUID());

		Task<String> task1 = new Task<>(
				UUID.randomUUID(),
				group1,
				Main.TaskType.READ,
				() -> "Group 1 Task"
		);

		Task<String> task2 = new Task<>(
				UUID.randomUUID(),
				group2,
				Main.TaskType.WRITE,
				() -> "Group 2 Task"
		);

		Future<String> future1 = taskExecutorService.submitTask(task1);
		Future<String> future2 = taskExecutorService.submitTask(task2);

		assertNotNull(future1);
		assertNotNull(future2);

		assertEquals("Group 1 Task", future1.get());
		assertEquals("Group 2 Task", future2.get());
	}
}
