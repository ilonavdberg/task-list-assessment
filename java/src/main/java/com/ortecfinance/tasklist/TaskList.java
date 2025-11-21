package com.ortecfinance.tasklist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class TaskList implements Runnable {
    private static final String QUIT = "quit";

    private final Map<String, List<Task>> tasks = new LinkedHashMap<>();
    private final BufferedReader in;
    private final PrintWriter out;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private long lastId = 0;

    public static void startConsole() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        new TaskList(in, out).run();
    }

    public TaskList(BufferedReader reader, PrintWriter writer) {
        this.in = reader;
        this.out = writer;
    }

    public void run() {
        out.println("Welcome to TaskList! Type 'help' for available commands.");
        while (true) {
            out.print("> ");
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
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            out.println(project.getKey());
            for (Task task : project.getValue()) {
                out.printf("    [%c] %d: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription());
            }
            out.println();
        }
    }

    private void showByDueDate() {
        Map<LocalDate, List<Task>> tasksByDate = new TreeMap<>();
        List<Task> noDeadline = new ArrayList<>();

        for (List<Task> projectTasks : tasks.values()) {
            for (Task task : projectTasks) {
                if (task.getDueDate() != null) {
                    tasksByDate.computeIfAbsent(task.getDueDate(), key -> new ArrayList<>())
                            .add(task);
                } else {
                    noDeadline.add(task);
                }
            }
        }

        for (Map.Entry<LocalDate, List<Task>> entry : tasksByDate.entrySet()) {
            out.println(entry.getKey().format(formatter) + ":");
            for (Task task : entry.getValue()) {
                out.println("       " + task.getId() + ": " + task.getDescription());
            }
        }

        if (!noDeadline.isEmpty()) {
            out.println("No deadline:");
            for (Task task : noDeadline) {
                out.println("       " + task.getId() + ": " + task.getDescription());
            }
        }

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

    private void deadline(String commandLine) {
        String[] commandDetails = commandLine.split(" ", 2);
        int id = Integer.parseInt(commandDetails[0]);
        Task task = findTaskById(id);

        LocalDate dueDate = LocalDate.parse(commandDetails[1], formatter);

        if (task == null) {
            out.printf("Could not find a task with an ID of %d.", id);
            out.println();
            return;
        }
        task.setDueDate(dueDate);
    }

    private void addProject(String name) {
        tasks.put(name, new ArrayList<>());
    }

    private void addTask(String project, String description) {
        List<Task> projectTasks = tasks.get(project);
        if (projectTasks == null) {
            out.printf("Could not find a project with the name \"%s\".", project);
            out.println();
            return;
        }
        projectTasks.add(new Task(nextId(), description, false));
    }

    private void check(String idString) {
        setDone(idString, true);
    }

    private void uncheck(String idString) {
        setDone(idString, false);
    }

    private void setDone(String idString, boolean done) {
        int id = Integer.parseInt(idString);
        Task task = findTaskById(id);

        if (task == null) {
            out.printf("Could not find a task with an ID of %d.", id);
            out.println();
            return;
        }

        task.setDone(done);
    }

    private Task findTaskById(int id) {
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            for (Task task : project.getValue()) {
                if (task.getId() == id) {
                    return task;
                }
            }
        }
        return null;
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
