package com.ortecfinance.tasklist.application;

import com.ortecfinance.tasklist.domain.project.Project;
import com.ortecfinance.tasklist.domain.project.ProjectRepository;
import com.ortecfinance.tasklist.domain.task.Task;
import com.ortecfinance.tasklist.domain.task.TaskRepository;
import com.ortecfinance.tasklist.exceptions.RecordNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskListService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskListService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public Map<Project, List<Task>> getAllTasksGroupedByProject() {
        Map<Project, List<Task>> map = projectRepository.findAll().stream()
                .collect(Collectors.toMap(
                        project -> project,
                        project -> new ArrayList<>(),
                        (a, b) -> a, LinkedHashMap::new //required to preserve order
                ));

        taskRepository.findAll()
                .forEach(task -> map.get(task.getProject()).add(task));

        return map;
    }

    public Map<LocalDate, List<Task>> getAllTasksGroupedByDueDate() {
        Map<LocalDate, List<Task>> tasksByDueDate = new TreeMap<>();

        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {
            if (task.getDueDate() != null) {
                tasksByDueDate.computeIfAbsent(task.getDueDate(), key -> new ArrayList<>())
                        .add(task);
            } else {
                tasksByDueDate.computeIfAbsent(LocalDate.MAX, key -> new ArrayList<>())
                        .add(task);
            }
        }

        return tasksByDueDate;
    }

    public void createProject(String name) {
        Project project = new Project(name);
        projectRepository.save(project);
    }

    public Task addTaskToProject(String projectName, String taskDescription) {
        Project project = projectRepository.findByName(projectName)
                .orElseThrow(() -> new RecordNotFoundException("Could not find a project with the name \"" + projectName + "\"."));

        Task task = new Task(project, taskDescription);
        return taskRepository.save(task);
    }

    public void changeStatusOnTask(int taskId, boolean done) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RecordNotFoundException("Could not find a task with an ID of " + taskId + "."));

        task.setDone(done);
    }

    public void setDeadlineOnTask(int taskId, LocalDate dueDate) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RecordNotFoundException("Could not find a task with an ID of " + taskId + "."));

        task.setDueDate(dueDate);
    }
}
