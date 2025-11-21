package com.ortecfinance.tasklist.api.rest.project;

import com.ortecfinance.tasklist.api.rest.project.dto.CreateProjectRequest;
import com.ortecfinance.tasklist.api.rest.project.dto.CreateTaskRequest;
import com.ortecfinance.tasklist.application.TaskListService;
import com.ortecfinance.tasklist.domain.project.Project;
import com.ortecfinance.tasklist.domain.task.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final TaskListService taskListService;

    public ProjectController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    @GetMapping("/{name}/tasks")
    public ResponseEntity<Map<Project, List<Task>>> getTasksByProject() {
        Map<Project, List<Task>> result = taskListService.getAllTasksGroupedByProject();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateProjectRequest request) {
        taskListService.createProject(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).build(); //No URI in location header because Project has no id field
    }

    @PostMapping("{name}/tasks")
    public ResponseEntity<Void> create(@RequestBody CreateTaskRequest request) {
        Task task = taskListService.addTaskToProject(request.projectName(), request.taskDescription());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("tasks/{id}")
                .buildAndExpand(task.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }


}
