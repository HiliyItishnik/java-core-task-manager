package ru.portfolio.taskmanager;

import ru.portfolio.taskmanager.model.Task;
import ru.portfolio.taskmanager.service.TaskService;

import java.util.List;
import java.util.Scanner;


    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== TASK MANAGER ===");
                System.out.println("1. Показать задачи");
                System.out.println("2. Добавить задачу");
                System.out.println("0. Выход");
                System.out.print("Выбери действие: ");

                String choice = scanner.nextLine().trim();

                    switch (choice) {
                        case "1" -> showTasks(taskService);
                        case "2" -> addTask(scanner, taskService);
                        case "0" -> {
                            System.out.println("До свидания!");
                            return;
                        }
                        default -> System.out.println(
                    );
                }
            }
        }
    }

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
        System.out.print("Название задачи: ");
        String title = scanner.nextLine();

        System.out.print("Описание задачи: ");
        String description = scanner.nextLine();

        try {
            System.out.println("Задача добавлена: " + task);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    }
