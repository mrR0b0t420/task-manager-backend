package com.example.taskmanager.controllers;

import com.example.taskmanager.models.Subtask;
import com.example.taskmanager.models.Task;
import com.example.taskmanager.repositories.SubtaskRepository;
import com.example.taskmanager.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class SubtaskController {

    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/tasks/{taskId}/subtasks")
    public ResponseEntity<Subtask> createSubtask(@PathVariable Long taskId, @RequestBody Subtask subtask) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        subtask.setTask(task);
        Subtask createdSubtask = subtaskRepository.save(subtask);
        return new ResponseEntity<>(createdSubtask, HttpStatus.CREATED);
    }

    @PatchMapping("/subtasks/{id}")
    public ResponseEntity<Subtask> updateSubtask(@PathVariable Long id, @RequestBody Subtask subtaskDetails) {
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask not found"));

        if (subtaskDetails.getTitle() != null) {
            subtask.setTitle(subtaskDetails.getTitle());
        }
        subtask.setCompleted(subtaskDetails.isCompleted());

        return ResponseEntity.ok(subtaskRepository.save(subtask));
    }

    @DeleteMapping("/subtasks/{id}")
    public ResponseEntity<Void> deleteSubtask(@PathVariable Long id) {
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask not found"));

        subtaskRepository.delete(subtask);
        return ResponseEntity.noContent().build();
    }
}
