package com.example.taskmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public List<Task> getAllTasks(@RequestParam(value = "category", required = false) Task.Category category) {
        if (category != null) {
            return taskRepository.findByCategory(category);
        }
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return ResponseEntity.ok(task);
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TaskController.class);

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        logger.info("Received request to create task: {}", task);
        try {
            // Force status to TODO for all new tasks
            task.setStatus(TaskStatus.TODO);
            Task createdTask = taskRepository.save(task);
            logger.info("Task created successfully with ID: {}", createdTask.getId());
            return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating task", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @RequestBody Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        task.setTitle(taskDetails.getTitle());
        task.setCategory(taskDetails.getCategory());
        task.setPriority(taskDetails.getPriority());
        // task.setLifespanHours(taskDetails.getLifespanHours()); // Removed
        if (taskDetails.getStatus() != null) {
            task.setStatus(taskDetails.getStatus());
        }

        final Task updatedTask = taskRepository.save(task);
        return ResponseEntity.ok(updatedTask);
    }

    // New specific endpoint for status updates
    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus(@PathVariable("id") Long id,
            @RequestBody Map<String, String> statusUpdate) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        String newStatusStr = statusUpdate.get("status");
        if (newStatusStr != null) {
            try {
                task.setStatus(TaskStatus.valueOf(newStatusStr));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
            }
        }

        final Task updatedTask = taskRepository.save(task);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        taskRepository.delete(task);
        return ResponseEntity.noContent().build();
    }
}
