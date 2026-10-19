
package ru.portfolio.taskmanager.model;

public class Task {

    private final int id;
    private final String title;
    private final String description;

    public Task(int id, String title, String description) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID задачи должен быть положительным"
            );
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Название задачи не может быть пустым"
            );
        }

        this.id = id;
        this.title = title.trim();
        this.description = description == null
                ? ""
                : description.trim();
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

    @Override
    public String toString() {
        if (description.isEmpty()) {
            return "[" + id + "] " + title;
        }

        return "[" + id + "] " + title + " — " + description;
    }
}
