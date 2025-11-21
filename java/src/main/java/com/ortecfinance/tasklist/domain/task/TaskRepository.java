package com.ortecfinance.tasklist.domain.task;

import com.ortecfinance.tasklist.domain.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskRepository {
    private final List<Task> tasks = new ArrayList<>();

    private static TaskRepository instance;

    public static TaskRepository getInstance() {
        if (instance == null) {
            instance = new TaskRepository();
        }
        return instance;
    }

    private TaskRepository() {}

    public void save(Task task) {
        tasks.add(task);
    }

    public List<Task> findAll() {
        return tasks;
    }

    public Optional<Task> findById(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst();
    }

    public Map<Project, List<Task>> findAllTasksGroupedByProject() {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::getProject));
    }
}
