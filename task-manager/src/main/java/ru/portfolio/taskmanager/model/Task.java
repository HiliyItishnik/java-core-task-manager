
package ru.portfolio.taskmanager.model;

public class Task {

    private final int id;
    private final String title;
    private final String description;
    private final Priority priority;
    private TaskStatus status;

    public Task(int id, String title, String description,
                Priority priority) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID должен быть положительным"
            );
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Название задачи не может быть пустым"
            );
        }

        if (priority == null) {
            throw new IllegalArgumentException(
                    "Приоритет не может быть пустым"
            );
        }

        this.id = id;
        this.title = title.trim();
        this.description = description == null
                ? ""
                : description.trim();
        this.priority = priority;
        this.status = TaskStatus.NEW;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Статус не может быть пустым"
            );
        }

        this.status = status;
    }

    @Override
    public String toString() {
        return "[" + id + "] "
                + title
                + " | Приоритет: " + priority
                + " | Статус: " + status
                + (description.isEmpty()
                ? ""
                : " | Описание: " + description);
    }
}
