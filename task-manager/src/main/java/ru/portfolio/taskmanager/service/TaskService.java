
package ru.portfolio.taskmanager.service;

import ru.portfolio.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskService {

    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    public Task addTask(String title, String description) {
        Task task = new Task(nextId, title, description);

        tasks.add(task);
        nextId++;

        return task;
    }

    public List<Task> getAllTasks() {
        return List.copyOf(tasks);
    }
}
