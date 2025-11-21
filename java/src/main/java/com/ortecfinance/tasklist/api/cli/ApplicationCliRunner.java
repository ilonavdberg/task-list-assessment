package com.ortecfinance.tasklist.api.cli;

import com.ortecfinance.tasklist.application.TaskListService;
import com.ortecfinance.tasklist.domain.project.Project;
import com.ortecfinance.tasklist.domain.project.ProjectRepository;
import com.ortecfinance.tasklist.domain.task.Task;
import com.ortecfinance.tasklist.domain.task.TaskRepository;
import com.ortecfinance.tasklist.exceptions.RecordNotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class ApplicationCliRunner implements Runnable {
    public static final String PROMPT = "> ";
    private static final String QUIT = "quit";

    private final Map<String, List<Task>> tasks = new LinkedHashMap<>(); //TODO: replace with repository
    private final TaskListService taskListService;
    private final BufferedReader in;
    private final PrintWriter out;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private long lastId = 0;

    public static void startConsole() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        new ApplicationCliRunner(new TaskListService(TaskRepository.getInstance(), ProjectRepository.getInstance()), in, out).run();
    }

    public ApplicationCliRunner(TaskListService taskListService, BufferedReader reader, PrintWriter writer) {
        this.taskListService = taskListService;
        this.in = reader;
        this.out = writer;
    }

    public void run() {
        out.println("Welcome to TaskList! Type 'help' for available commands.");
        while (true) {
            out.print(PROMPT);
            out.flush();
            String command;
            try {
                command = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (command.equals(QUIT)) {
                break;
            }
            execute(command);
        }
    }

    private void execute(String commandLine) {
        String[] commandRest = commandLine.split(" ", 2);
        String command = commandRest[0];
        switch (command) {
            case "show":
                show();
                break;
            case "show-by-deadline":
                showByDueDate();
                break;
            case "add":
                add(commandRest[1]);
                break;
            case "check":
                check(commandRest[1]);
                break;
            case "uncheck":
                uncheck(commandRest[1]);
                break;
            case "deadline":
                deadline(commandRest[1]);
                break;
            case "help":
                help();
                break;
            default:
                error(command);
                break;
        }
    }

    private void show() {
        Map<Project, List<Task>> tasks = taskListService.getAllTasksGroupedByProject();

        for (Map.Entry<Project, List<Task>> project : tasks.entrySet()) {
            out.println(project.getKey().getName());
            for (Task task : project.getValue()) {
                out.printf("    [%c] %d: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription());
            }
            out.println();
        }
    }

    private void showByDueDate() {
        Map<LocalDate, List<Task>> tasksByDate = taskListService.getAllTasksGroupedByDueDate();

        for (Map.Entry<LocalDate, List<Task>> entry : tasksByDate.entrySet()) {
            LocalDate date = entry.getKey();

            if (date.equals(LocalDate.MAX)) {
                out.println("No deadline:");
            } else {
                out.println(date.format(formatter) + ":");
            }

            for (Task task : entry.getValue()) {
                out.println("       " + task.getId() + ": " + task.getDescription());
            }
        }
        out.println();
    }

    private void add(String commandLine) {
        String[] subcommandRest = commandLine.split(" ", 2);
        String subcommand = subcommandRest[0];
        if (subcommand.equals("project")) {
            addProject(subcommandRest[1]);
        } else if (subcommand.equals("task")) {
            String[] projectTask = subcommandRest[1].split(" ", 2);
            addTask(projectTask[0], projectTask[1]);
        }
    }

    private void addProject(String name) {
        taskListService.createProject(name);
    }

    private void addTask(String projectName, String taskDescription) {
        try {
            taskListService.addTaskToProject(projectName, taskDescription);
        } catch(RecordNotFoundException exception) {
            out.printf(exception.getMessage());
            out.println();
        }
    }

    private void deadline(String commandLine) {
        String[] commandDetails = commandLine.split(" ", 2);
        int id = Integer.parseInt(commandDetails[0]);
        LocalDate dueDate = LocalDate.parse(commandDetails[1], formatter);

        try {
            taskListService.setDeadlineOnTask(id, dueDate);
        } catch (Exception exception) {
            out.printf(exception.getMessage());
            out.println();
        }
    }

    private void check(String idString) {
        setDone(idString, true);
    }

    private void uncheck(String idString) {
        setDone(idString, false);
    }

    private void setDone(String idString, boolean done) {
        int id = Integer.parseInt(idString);
        try {
            taskListService.changeStatusOnTask(id, done);
        } catch(RecordNotFoundException exception) {
            out.printf(exception.getMessage());
            out.println();
        }
    }

    private void help() {
        out.println("Commands:");
        out.println("  show");
        out.println("  show-by-deadline");
        out.println("  add project <project name>");
        out.println("  add task <project name> <task description>");
        out.println("  deadline <ID> <date (format: DD-MM-YYYY)>");
        out.println("  check <task ID>");
        out.println("  uncheck <task ID>");
        out.println();
    }

    private void error(String command) {
        out.printf("I don't know what the command \"%s\" is.", command);
        out.println();
    }

    private long nextId() {
        return ++lastId;
    }
}
