
package ru.portfolio.taskmanager.service;

import java.util.Locale;
import ru.portfolio.taskmanager.model.Priority;
import ru.portfolio.taskmanager.model.Task;
import ru.portfolio.taskmanager.model.TaskStatus;
import ru.portfolio.taskmanager.repository.TaskRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    public List<Task> searchByTitle(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String searchText = query.trim().toLowerCase(Locale.ROOT);

        return tasks.stream()
                .filter(task -> task.getTitle()
                        .toLowerCase(Locale.ROOT)
                        .contains(searchText))
                .toList();
    }

    public List<Task> filterByStatus(TaskStatus status) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .toList();
    }

    public List<Task> filterByPriority(Priority priority) {
        return tasks.stream()
                .filter(task -> task.getPriority() == priority)
                .toList();
    }


    private final List<Task> tasks = new ArrayList<>();
    private final TaskRepository taskRepository;
    private int nextId = 1;

    public TaskService(TaskRepository taskRepository)
            throws IOException {
        this.taskRepository = taskRepository;
        tasks.addAll(taskRepository.loadTasks());

        int maxId = 0;

        for (Task task : tasks) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }

        nextId = maxId + 1;
    }

    public Task addTask(String title, String description,
                        Priority priority) throws IOException {
        Task task = new Task(
                nextId, title, description, priority
        );

        tasks.add(task);
        nextId++;

        try {
            taskRepository.saveTasks(tasks);
        } catch (IOException e) {
            tasks.remove(task);
            nextId--;
            throw e;
        }

        return task;
    }

    public List<Task> getAllTasks() {
        return List.copyOf(tasks);
    }

    public void updateStatus(int id, TaskStatus status)
            throws IOException {
        Task task = findById(id);
        TaskStatus oldStatus = task.getStatus();

        task.setStatus(status);

        try {
            taskRepository.saveTasks(tasks);
        } catch (IOException e) {
            task.setStatus(oldStatus);
            throw e;
        }
    }

    public void deleteTask(int id) throws IOException {
        Task task = findById(id);
        int index = tasks.indexOf(task);

        tasks.remove(index);

        try {
            taskRepository.saveTasks(tasks);
        } catch (IOException e) {
            tasks.add(index, task);
            throw e;
        }
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
