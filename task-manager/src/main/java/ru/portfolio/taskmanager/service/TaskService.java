
package ru.portfolio.taskmanager.service;

import ru.portfolio.taskmanager.model.Priority;
import ru.portfolio.taskmanager.model.Task;
import ru.portfolio.taskmanager.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class TaskService {

    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    public Task addTask(String title, String description,
                        Priority priority) {
        Task task = new Task(
                nextId, title, description, priority
        );

        tasks.add(task);
        nextId++;

        return task;
    }

    public List<Task> getAllTasks() {
        return List.copyOf(tasks);
    }

    public void updateStatus(int id, TaskStatus status) {
        findById(id).setStatus(status);
    }

    public void deleteTask(int id) {
        Task task = findById(id);
        tasks.remove(task);
    }

    private Task findById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }

        throw new IllegalArgumentException(
                "Задача с ID " + id + " не найдена"
        );
    }
}
