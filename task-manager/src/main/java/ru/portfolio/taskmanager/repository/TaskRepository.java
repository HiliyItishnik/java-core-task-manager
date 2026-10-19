
package ru.portfolio.taskmanager.repository;

import ru.portfolio.taskmanager.model.Priority;
import ru.portfolio.taskmanager.model.Task;
import ru.portfolio.taskmanager.model.TaskStatus;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class TaskRepository {

    private final Path filePath;

    public TaskRepository() {
        this(Paths.get("data", "tasks.txt"));
    }

    public TaskRepository(Path filePath) {
        this.filePath = filePath;
    }

    public List<Task> loadTasks() throws IOException {
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(
                filePath, StandardCharsets.UTF_8
        );

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (!line.isBlank()) {
                tasks.add(parseTask(line, i + 1));
            }
        }

        return tasks;
    }

    public void saveTasks(List<Task> tasks) throws IOException {
        Path parent = filePath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath, StandardCharsets.UTF_8)) {

            for (Task task : tasks) {
                String line = task.getId() + "|"
                        + task.getStatus().name() + "|"
                        + task.getPriority().name() + "|"
                        + encode(task.getTitle()) + "|"
                        + encode(task.getDescription());

                writer.write(line);
                writer.newLine();
            }
        }
    }

    private Task parseTask(String line, int lineNumber)
            throws IOException {
        try {
            String[] parts = line.split("\\|", -1);

            if (parts.length != 5) {
                throw new IllegalArgumentException(
                        "Неверный формат строки"
                );
            }

            int id = Integer.parseInt(parts[0]);
            TaskStatus status = TaskStatus.valueOf(parts[1]);
            Priority priority = Priority.valueOf(parts[2]);

            String title = decode(parts[3]);
            String description = decode(parts[4]);

            Task task = new Task(
                    id, title, description, priority
            );

            task.setStatus(status);

            return task;
        } catch (IllegalArgumentException e) {
            throw new IOException(
                    "Некорректные данные в файле "
                            + filePath
                            + ", строка " + lineNumber,
                    e
            );
        }
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(
                value.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String decode(String value) {
        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }
}
