
package ru.portfolio.taskmanager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.portfolio.taskmanager.model.Priority;
import ru.portfolio.taskmanager.model.Task;
import ru.portfolio.taskmanager.model.TaskStatus;
import ru.portfolio.taskmanager.repository.TaskRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    @TempDir
    Path tempDir;

    private TaskService createTaskService() throws IOException {
        return new TaskService(
                new TaskRepository(tempDir.resolve("tasks.txt"))
        );
    }

    @Test
    void shouldAddTask() throws IOException {
        TaskService taskService = createTaskService();

        Task task = taskService.addTask(
                "Выучить Java",
                "Повторить ООП",
                Priority.MEDIUM
        );

        assertEquals(1, task.getId());
        assertEquals("Выучить Java", task.getTitle());
        assertEquals(Priority.MEDIUM, task.getPriority());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(1, taskService.getAllTasks().size());
    }

    @Test
    void shouldReturnEmptyListAtStart() throws IOException {
        TaskService taskService = createTaskService();

        List<Task> tasks = taskService.getAllTasks();

        assertTrue(tasks.isEmpty());
    }

    @Test
    void shouldRejectBlankTitle() throws IOException {
        TaskService taskService = createTaskService();

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.addTask(
                        "   ", "Описание", Priority.LOW
                )
        );
    }

    @Test
    void shouldUpdateTaskStatus() throws IOException {
        TaskService taskService = createTaskService();

        Task task = taskService.addTask(
                "Выучить Java", "", Priority.HIGH
        );

        taskService.updateStatus(task.getId(), TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void shouldDeleteTask() throws IOException {
        TaskService taskService = createTaskService();

        Task task = taskService.addTask(
                "Выучить Java", "", Priority.LOW
        );

        taskService.deleteTask(task.getId());

        assertTrue(taskService.getAllTasks().isEmpty());
    }

    @Test
    void shouldLoadTasksAfterRestart() throws IOException {
        TaskService firstService = createTaskService();

        Task task = firstService.addTask(
                "Сохранить проект",
                "Проверить запись в файл",
                Priority.HIGH
        );

        firstService.updateStatus(task.getId(), TaskStatus.DONE);

        TaskService secondService = createTaskService();

        List<Task> tasks = secondService.getAllTasks();

        assertEquals(1, tasks.size());
        assertEquals("Сохранить проект", tasks.get(0).getTitle());
        assertEquals(Priority.HIGH, tasks.get(0).getPriority());
        assertEquals(TaskStatus.DONE, tasks.get(0).getStatus());
    }
}
