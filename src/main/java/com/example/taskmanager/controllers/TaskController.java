package com.example.taskmanager.controllers;

import com.example.taskmanager.models.Task;
import com.example.taskmanager.models.User;
import com.example.taskmanager.models.TaskStatus;
import com.example.taskmanager.repositories.TaskRepository;
import com.example.taskmanager.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing tasks.
 * Provides endpoints for creating, retrieving, updating, and deleting tasks.
 * 
 * Base URL: /api/tasks
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private boolean isAdmin(User user) {
        return "ADMIN".equals(user.getRole());
    }

    @GetMapping
    public List<Task> getAllTasks(@RequestParam(value = "category", required = false) Task.Category category) {
        User currentUser = getCurrentUser();
        List<Task> tasks;

        if (isAdmin(currentUser)) {
            tasks = taskRepository.findAll();
        } else {
            tasks = taskRepository.findByUser(currentUser);
        }

        if (category != null) {
            return tasks.stream()
                    .filter(t -> t.getCategory() == category)
                    .toList();
        }
        return tasks;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        User currentUser = getCurrentUser();
        // Allow if Admin OR (Task has owner AND Owner is Current User)
        // Allow if Admin OR (Task has owner AND Owner is Current User) OR Task is
        // Legacy (No owner)
        if (!isAdmin(currentUser) && task.getUser() != null && !task.getUser().getId().equals(currentUser.getId())) {
            logger.warn("Access denied for user {} to task {}. role={}, taskOwner={}",
                    currentUser.getUsername(), id, currentUser.getRole(),
                    task.getUser().getUsername());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return ResponseEntity.ok(task);
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TaskController.class);

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        User currentUser = getCurrentUser();
        task.setUser(currentUser);

        logger.info("Creating task for user: {}", currentUser.getUsername());

        // Force status to TO-DO for all new tasks
        task.setStatus(TaskStatus.TODO);
        Task createdTask = taskRepository.save(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @RequestBody Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser) && task.getUser() != null && !task.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        task.setTitle(taskDetails.getTitle());
        task.setCategory(taskDetails.getCategory());
        task.setPriority(taskDetails.getPriority());
        if (taskDetails.getStatus() != null) {
            task.setStatus(taskDetails.getStatus());
        }

        final Task updatedTask = taskRepository.save(task);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus(@PathVariable("id") Long id,
            @RequestBody Map<String, String> statusUpdate) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser) && task.getUser() != null && !task.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

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

        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser) && task.getUser() != null && !task.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        taskRepository.delete(task);
        return ResponseEntity.noContent().build();
    }
}
