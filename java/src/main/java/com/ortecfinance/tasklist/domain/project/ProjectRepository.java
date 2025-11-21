package com.ortecfinance.tasklist.domain.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProjectRepository {
    private final List<Project> projects = new ArrayList<>();

    private static ProjectRepository instance;

    public static ProjectRepository getInstance() {
        if (instance == null) {
            instance = new ProjectRepository();
        }
        return instance;
    }

    private ProjectRepository() {}

    public void save(Project project) {
        projects.add(project);
    }

    public List<Project> findAll() {
        return projects;
    }

    public Optional<Project> findByName(String name) {
        return projects.stream()
                .filter(project -> Objects.equals(project.getName(), name))
                .findFirst();
    }
}
