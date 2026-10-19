
package ru.portfolio.taskmanager.service;

import org.junit.jupiter.api.Test;
import ru.portfolio.taskmanager.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    @Test
    void shouldAddTask() {
        TaskService taskService = new TaskService();

        Task task = taskService.addTask(
                "Выучить Java",
                "Повторить ООП"
        );

        assertEquals(1, task.getId());
        assertEquals("Выучить Java", task.getTitle());
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
                () -> taskService.addTask("   ", "Описание")
        );
    }
}
