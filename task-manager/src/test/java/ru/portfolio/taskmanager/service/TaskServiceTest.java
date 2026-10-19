
package ru.portfolio.taskmanager.service;

import org.junit.jupiter.api.Test;
import ru.portfolio.taskmanager.model.Priority;
import ru.portfolio.taskmanager.model.Task;
import ru.portfolio.taskmanager.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    @Test
    void shouldAddTask() {
        TaskService taskService = new TaskService();

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
    void shouldReturnEmptyListAtStart() {
        TaskService taskService = new TaskService();

        List<Task> tasks = taskService.getAllTasks();

        assertTrue(tasks.isEmpty());
    }

    @Test
    void shouldRejectBlankTitle() {
        TaskService taskService = new TaskService();

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.addTask(
                        "   ", "Описание", Priority.LOW
                )
        );
    }

    @Test
    void shouldUpdateTaskStatus() {
        TaskService taskService = new TaskService();

        Task task = taskService.addTask(
                "Выучить Java", "", Priority.HIGH
        );

        taskService.updateStatus(task.getId(), TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void shouldDeleteTask() {
        TaskService taskService = new TaskService();

        Task task = taskService.addTask(
                "Выучить Java", "", Priority.LOW
        );

        taskService.deleteTask(task.getId());

        assertTrue(taskService.getAllTasks().isEmpty());
    }
}
