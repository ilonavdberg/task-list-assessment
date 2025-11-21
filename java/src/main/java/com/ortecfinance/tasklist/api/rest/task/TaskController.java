package com.ortecfinance.tasklist.api.rest.task;

import com.ortecfinance.tasklist.api.rest.task.dto.UpdateTaskDeadlineRequest;
import com.ortecfinance.tasklist.api.rest.task.dto.UpdateTaskStatusRequest;
import com.ortecfinance.tasklist.application.TaskListService;
import com.ortecfinance.tasklist.domain.project.Project;
import com.ortecfinance.tasklist.domain.task.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskListService taskListService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public TaskController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    @GetMapping("/by-project")
    public ResponseEntity<Map<Project, List<Task>>> getTasksByProject() {
        Map<Project, List<Task>> result = taskListService.getAllTasksGroupedByProject();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-deadline")
    public ResponseEntity<Map<LocalDate, List<Task>>> getTasks() {
        Map<LocalDate, List<Task>> result = taskListService.getAllTasksGroupedByDueDate();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable int id,
            @RequestBody UpdateTaskStatusRequest request
    ) {
        taskListService.changeStatusOnTask(id, request.done());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateDeadline(
            @PathVariable int id,
            @RequestBody UpdateTaskDeadlineRequest request
            ) {
        taskListService.setDeadlineOnTask(id, LocalDate.parse(request.dueDate(), formatter));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
