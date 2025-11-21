package com.ortecfinance.tasklist.domain.task;

import com.ortecfinance.tasklist.domain.project.Project;

import java.time.LocalDate;

public final class Task {
    private final long id;
    private final String description;
    private LocalDate dueDate;
    private boolean done;
    private Project project;

    private static long lastId = 0;

    public Task(long id, String description, boolean done) {
        this.id = id;
        this.description = description;
        this.done = done;
    }

    public Task(Project project, String description) {
        this.project = project;
        this.description = description;
        id = nextId();
        done = false;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isDone() {
        return done;
    }

    public Project getProject() {
        return project;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    private long nextId() {
        return ++lastId;
    }
}
