
package ru.portfolio.taskmanager;

import ru.portfolio.taskmanager.model.Priority;
import ru.portfolio.taskmanager.model.Task;
import ru.portfolio.taskmanager.model.TaskStatus;
import ru.portfolio.taskmanager.repository.TaskRepository;
import ru.portfolio.taskmanager.service.TaskService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskService taskService;

        try {
            taskService = new TaskService(new TaskRepository());
        } catch (IOException e) {
            System.out.println(
                    "Не удалось загрузить задачи: " + e.getMessage()
            );
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== TASK MANAGER ===");
                System.out.println("1. Показать задачи");
                System.out.println("2. Добавить задачу");
                System.out.println("3. Изменить статус");
                System.out.println("4. Удалить задачу");
                System.out.println("5. Поиск по названию");
                System.out.println("6. Фильтр по статусу");
                System.out.println("7. Фильтр по приоритету");
                System.out.println("0. Выход");
                System.out.print("Выбери действие: ");

                String choice = scanner.nextLine().trim();

                try {
                    switch (choice) {
                        case "1" -> showTasks(taskService);
                        case "2" -> addTask(scanner, taskService);
                        case "3" -> updateStatus(scanner, taskService);
                        case "4" -> deleteTask(scanner, taskService);
                        case "5" -> searchTasks(scanner, taskService);
                        case "6" -> filterByStatus(scanner, taskService);
                        case "7" -> filterByPriority(scanner, taskService);
                        case "0" -> {
                            System.out.println("До свидания!");
                            return;
                        }
                        default -> System.out.println(
                                "Ошибка: выбери 1, 2, 3, 4 или 0."
                        );
                    }
                } catch (IOException e) {
                    System.out.println(
                            "Не удалось сохранить данные: " + e.getMessage()
                    );
                }
            }
        }
    }

    private static void showTasks(TaskService taskService)
            throws IOException {
        List<Task> tasks = taskService.getAllTasks();

        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст.");
            return;
        }

        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    private static void addTask(
            Scanner scanner,
            TaskService taskService
    ) throws IOException {
        System.out.print("Название задачи: ");
        String title = scanner.nextLine();

        System.out.print("Описание задачи: ");
        String description = scanner.nextLine();

        System.out.println("Выбери приоритет:");
        System.out.println("1. LOW");
        System.out.println("2. MEDIUM");
        System.out.println("3. HIGH");
        System.out.print("Твой выбор: ");

        String choice = scanner.nextLine().trim();

        Priority priority;

        switch (choice) {
            case "1" -> priority = Priority.LOW;
            case "2" -> priority = Priority.MEDIUM;
            case "3" -> priority = Priority.HIGH;
            default -> {
                System.out.println("Неизвестный приоритет.");
                return;
            }
        }

        try {
            Task task = taskService.addTask(
                    title, description, priority
            );
            System.out.println("Задача добавлена: " + task);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void updateStatus(
            Scanner scanner,
            TaskService taskService
    ) throws IOException {
        System.out.print("ID задачи: ");
        int id = readId(scanner);

        if (id < 1) {
            System.out.println("Некорректный ID.");
            return;
        }

        System.out.println("1. NEW");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. DONE");
        System.out.print("Новый статус: ");

        String choice = scanner.nextLine().trim();

        TaskStatus status;

        switch (choice) {
            case "1" -> status = TaskStatus.NEW;
            case "2" -> status = TaskStatus.IN_PROGRESS;
            case "3" -> status = TaskStatus.DONE;
            default -> {
                System.out.println("Неизвестный статус.");
                return;
            }
        }

        try {
            taskService.updateStatus(id, status);
            System.out.println("Статус изменён.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void deleteTask(
            Scanner scanner,
            TaskService taskService
    ) throws IOException {
        System.out.print("ID задачи для удаления: ");
        int id = readId(scanner);

        if (id < 1) {
            System.out.println("Некорректный ID.");
            return;
        }

        try {
            taskService.deleteTask(id);
            System.out.println("Задача удалена.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static int readId(Scanner scanner)
            throws IOException {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    private static void searchTasks(
            Scanner scanner,
            TaskService taskService
    ) {
        System.out.print("Введи название или его часть: ");
        String query = scanner.nextLine();

        if (query.isBlank()) {
            System.out.println("Поисковый запрос не может быть пустым.");
            return;
        }

        List<Task> foundTasks = taskService.searchByTitle(query);
        printTasks(foundTasks);
    }

    private static void filterByStatus(
            Scanner scanner,
            TaskService taskService
    ) {
        System.out.println("1. NEW");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. DONE");
        System.out.print("Выбери статус: ");

        String choice = scanner.nextLine().trim();
        TaskStatus status;

        switch (choice) {
            case "1" -> status = TaskStatus.NEW;
            case "2" -> status = TaskStatus.IN_PROGRESS;
            case "3" -> status = TaskStatus.DONE;
            default -> {
                System.out.println("Неизвестный статус.");
                return;
            }
        }

        printTasks(taskService.filterByStatus(status));
    }

    private static void filterByPriority(
            Scanner scanner,
            TaskService taskService
    ) {
        System.out.println("1. LOW");
        System.out.println("2. MEDIUM");
        System.out.println("3. HIGH");
        System.out.print("Выбери приоритет: ");

        String choice = scanner.nextLine().trim();
        Priority priority;

        switch (choice) {
            case "1" -> priority = Priority.LOW;
            case "2" -> priority = Priority.MEDIUM;
            case "3" -> priority = Priority.HIGH;
            default -> {
                System.out.println("Неизвестный приоритет.");
                return;
            }
        }

        printTasks(taskService.filterByPriority(priority));
    }

    private static void printTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Задачи не найдены.");
            return;
        }

        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}

