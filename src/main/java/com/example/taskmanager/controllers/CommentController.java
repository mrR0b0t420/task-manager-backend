package com.example.taskmanager.controllers;

import com.example.taskmanager.models.Comment;
import com.example.taskmanager.models.Subtask;
import com.example.taskmanager.models.Task;
import com.example.taskmanager.models.User;
import com.example.taskmanager.repositories.CommentRepository;
import com.example.taskmanager.repositories.SubtaskRepository;
import com.example.taskmanager.repositories.TaskRepository;
import com.example.taskmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<Comment> addCommentToTask(@PathVariable Long taskId, @RequestBody Comment comment) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        comment.setTask(task);
        comment.setUser(getCurrentUser());
        comment.setCreatedAt(LocalDateTime.now());

        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.CREATED);
    }

    @PostMapping("/subtasks/{subtaskId}/comments")
    public ResponseEntity<Comment> addCommentToSubtask(@PathVariable Long subtaskId, @RequestBody Comment comment) {
        Subtask subtask = subtaskRepository.findById(subtaskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask not found"));

        comment.setSubtask(subtask);
        comment.setUser(getCurrentUser());
        comment.setCreatedAt(LocalDateTime.now());

        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.CREATED);
    }

    @GetMapping("/tasks/{taskId}/comments")
    public List<Comment> getTaskComments(@PathVariable Long taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    @GetMapping("/subtasks/{subtaskId}/comments")
    public List<Comment> getSubtaskComments(@PathVariable Long subtaskId) {
        return commentRepository.findBySubtaskId(subtaskId);
    }
}
